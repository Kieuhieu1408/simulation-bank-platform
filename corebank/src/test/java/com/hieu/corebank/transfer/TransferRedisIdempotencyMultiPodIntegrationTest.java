package com.hieu.corebank.transfer;

import com.hieu.common.constant.TransactionStatus;
import com.hieu.common.exception.ApiErrorResponse;
import com.hieu.corebank.account.api.dto.request.AccountCreateRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.application.command.AccountHandler;
import com.hieu.corebank.account.infrastructure.projection.AccountView;
import com.hieu.corebank.account.infrastructure.projection.AccountViewRepository;
import com.hieu.corebank.customer.api.dto.request.CustomerCreateRequestDTO;
import com.hieu.corebank.customer.application.command.CustomerHandler;
import com.hieu.corebank.shared.domain.ConflictException;
import com.hieu.corebank.shared.domain.InProgressConflictException;
import com.hieu.corebank.shared.infrastructure.web.ApiExceptionHandler;
import com.hieu.corebank.transfer.api.dto.request.TransferRequestDTO;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import com.hieu.corebank.transfer.application.command.TransferHandler;
import com.hieu.corebank.transfer.application.port.AccountGateway;
import com.hieu.corebank.transfer.infrastructure.idempotency.RedisTransferIdempotencyLockAdapter;
import com.hieu.corebank.transfer.infrastructure.projection.TransferView;
import com.hieu.corebank.transfer.infrastructure.projection.TransferViewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "corebank.transfer.sync-timeout-seconds=1",
        "corebank.transfer.idempotency-lock-ttl-seconds=30"
})
@DisplayName("Multi-Pod Redis In-Flight Idempotency Lock Integration Tests")
class TransferRedisIdempotencyMultiPodIntegrationTest {

    @Autowired
    private CustomerHandler customerHandler;

    @Autowired
    private AccountHandler accountHandler;

    @Autowired
    private TransferHandler transferHandler;

    @Autowired
    private AccountViewRepository accountViewRepository;

    @Autowired
    private TransferViewRepository transferViewRepository;

    @Autowired
    private ApiExceptionHandler apiExceptionHandler;

    @MockitoSpyBean
    private AccountGateway accountGateway;

    @MockitoBean
    private StringRedisTemplate redisTemplate;

    /** Bộ nhớ mô phỏng Redis dùng chung nguyên tử giữa các Pod trong bài test tích hợp. */
    private final ConcurrentHashMap<String, String> simulatedRedisStore = new ConcurrentHashMap<>();
    private final AtomicBoolean simulateRedisDown = new AtomicBoolean(false);

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUpSimulatedRedis() {
        simulatedRedisStore.clear();
        simulateRedisDown.set(false);
        reset(accountGateway);

        // Mô phỏng chính xác hành vi nguyên tử của cả 2 Lua Script (Acquire 2 args & Release 1 arg)
        lenient().when(redisTemplate.execute(any(RedisScript.class), anyList(), any(Object[].class)))
                .thenAnswer(invocation -> {
                    if (simulateRedisDown.get()) {
                        throw new RedisConnectionFailureException("Simulated Redis cluster outage");
                    }
                    RedisScript<?> script = invocation.getArgument(0);
                    List<String> keys = invocation.getArgument(1);
                    String key = keys.getFirst();

                    if (String.class.equals(script.getResultType())) {
                        // Acquire Script: KEYS[1], ARGV[1]=lockValue, ARGV[2]=ttlMs
                        String lockValue = (String) invocation.getArgument(2);
                        String previous = simulatedRedisStore.putIfAbsent(key, lockValue);
                        return previous == null ? "ACQUIRED" : previous;
                    } else {
                        // Release Script: KEYS[1], ARGV[1]=lockValue
                        String expectedValue = (String) invocation.getArgument(2);
                        boolean removed = simulatedRedisStore.remove(key, expectedValue);
                        return removed ? 1L : 0L;
                    }
                });
    }

    private AccountResponseDTO createTestAccount(BigDecimal initialBalance) {
        String cif = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        customerHandler.handle(new CustomerCreateRequestDTO(cif));
        return accountHandler.handle(new AccountCreateRequestDTO(cif, "VND", initialBalance));
    }

    @Test
    @DisplayName("2 Pod xử lý cùng request: Pod 2 bị chặn ngay bởi Redis Lock (409002), khi Pod 1 xong xóa sạch lock & retry trả Replay")
    void concurrentDuplicateAcrossPodsShouldRejectSecondWith409002ThenCleanLockAndReplay() throws Exception {
        AccountResponseDTO src = createTestAccount(new BigDecimal("1000000.00"));
        AccountResponseDTO dst = createTestAccount(new BigDecimal("500000.00"));

        CountDownLatch pod1InsideSagaLatch = new CountDownLatch(1);
        CountDownLatch allowPod1ContinueLatch = new CountDownLatch(1);

        doAnswer(invocation -> {
            pod1InsideSagaLatch.countDown();
            allowPod1ContinueLatch.await(5, TimeUnit.SECONDS);
            return invocation.callRealMethod();
        }).when(accountGateway).reserveFunds(any(), any(), any());

        String idempotencyKey = "multipod-same-" + UUID.randomUUID();
        TransferRequestDTO cmd = TransferRequestDTO.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("200000.00"))
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Multi-pod test")
                .build();

        String expectedTransferId = UUID.nameUUIDFromBytes(
                (src.getAccountId() + ":" + idempotencyKey).getBytes(StandardCharsets.UTF_8)
        ).toString();
        String expectedRedisKey = RedisTransferIdempotencyLockAdapter.LOCK_KEY_PREFIX + expectedTransferId;

        ExecutorService pod1Executor = Executors.newSingleThreadExecutor();
        try {
            // Pod 1 bắt đầu chạy và giữ lock IN_FLIGHT
            Future<TransferResponseDTO> pod1Future = pod1Executor.submit(() -> transferHandler.handle(cmd));
            assertThat(pod1InsideSagaLatch.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(simulatedRedisStore).containsKey(expectedRedisKey);

            // Pod 2 nhận request trùng đúng lúc Pod 1 đang IN_FLIGHT -> ném InProgressConflictException (409002)
            assertThatThrownBy(() -> transferHandler.handle(cmd))
                    .isInstanceOf(InProgressConflictException.class)
                    .satisfies(ex -> {
                        ResponseEntity<ApiErrorResponse> errResp =
                                apiExceptionHandler.inProgressConflict((InProgressConflictException) ex);
                        assertThat(errResp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                        assertThat(errResp.getBody()).isNotNull();
                        assertThat(errResp.getBody().code()).isEqualTo("409002");
                    });

            // Cho phép Pod 1 hoàn tất Saga
            allowPod1ContinueLatch.countDown();
            TransferResponseDTO pod1Res = pod1Future.get(5, TimeUnit.SECONDS);
            assertThat(pod1Res.getStatus()).isEqualTo(TransactionStatus.SUCCESS);

            // Cách 1: Ngay khi Pod 1 chạy xong, key trên Redis đã bị xóa sạch hoàn toàn (0 byte RAM tồn đọng)
            assertThat(simulatedRedisStore).doesNotContainKey(expectedRedisKey);
            assertThat(simulatedRedisStore).isEmpty();

            // Sau đó money-bank retry lần 3 -> Nhận ngay kết quả Idempotent Replay từ transfer_view, không chạm Redis
            clearInvocations(redisTemplate);
            TransferResponseDTO replayRes = transferHandler.handle(cmd);
            assertThat(replayRes.getTransactionId()).isEqualTo(pod1Res.getTransactionId());
            assertThat(replayRes.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
            verifyNoInteractions(redisTemplate);

            // Đảm bảo tiền chỉ bị trừ đúng 1 lần duy nhất
            AccountView srcView = accountViewRepository.findById(src.getAccountId()).orElseThrow();
            assertThat(srcView.getActualBalance()).isEqualByComparingTo(new BigDecimal("800000.00"));
        } finally {
            pod1Executor.shutdownNow();
        }
    }

    @Test
    @DisplayName("Pod 2 gửi cùng idempotencyKey nhưng ĐỔI SỐ TIỀN khi Pod 1 đang IN_FLIGHT -> Trả lỗi 409001 (Payload Conflict)")
    void concurrentModifiedPayloadWhileInFlightShouldReturn409001() throws Exception {
        AccountResponseDTO src = createTestAccount(new BigDecimal("1000000.00"));
        AccountResponseDTO dst = createTestAccount(new BigDecimal("500000.00"));

        CountDownLatch pod1InsideSagaLatch = new CountDownLatch(1);
        CountDownLatch allowPod1ContinueLatch = new CountDownLatch(1);

        doAnswer(invocation -> {
            pod1InsideSagaLatch.countDown();
            allowPod1ContinueLatch.await(5, TimeUnit.SECONDS);
            return invocation.callRealMethod();
        }).when(accountGateway).reserveFunds(any(), any(), any());

        String idempotencyKey = "multipod-tamper-" + UUID.randomUUID();
        TransferRequestDTO cmd1 = TransferRequestDTO.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("100000.00"))
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Original")
                .build();

        TransferRequestDTO tamperedCmd = TransferRequestDTO.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("500000.00")) // Đổi số tiền!
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Tampered")
                .build();

        ExecutorService pod1Executor = Executors.newSingleThreadExecutor();
        try {
            Future<TransferResponseDTO> pod1Future = pod1Executor.submit(() -> transferHandler.handle(cmd1));
            assertThat(pod1InsideSagaLatch.await(5, TimeUnit.SECONDS)).isTrue();

            // Phải ném ConflictException thường (409001), KHÔNG phải InProgressConflictException (409002)
            assertThatThrownBy(() -> transferHandler.handle(tamperedCmd))
                    .isExactlyInstanceOf(ConflictException.class)
                    .hasMessageContaining("Idempotency key conflict");

            allowPod1ContinueLatch.countDown();
            pod1Future.get(5, TimeUnit.SECONDS);
        } finally {
            pod1Executor.shutdownNow();
        }
    }

    @Test
    @DisplayName("Khi Saga vượt sync-timeout (1s) chuyển sang chạy ngầm: Lock vẫn được giữ tới khi background Saga thực sự xong")
    void lockShouldRemainHeldDuringAsyncBackgroundSagaAndReleaseUponCompletion() throws Exception {
        AccountResponseDTO src = createTestAccount(new BigDecimal("1000000.00"));
        AccountResponseDTO dst = createTestAccount(new BigDecimal("500000.00"));

        CountDownLatch allowBackgroundSagaFinish = new CountDownLatch(1);
        doAnswer(invocation -> {
            // Chạy lâu hơn sync-timeout-seconds=1s
            allowBackgroundSagaFinish.await(5, TimeUnit.SECONDS);
            return invocation.callRealMethod();
        }).when(accountGateway).reserveFunds(any(), any(), any());

        String idempotencyKey = "async-hold-lock-" + UUID.randomUUID();
        TransferRequestDTO cmd = TransferRequestDTO.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("150000.00"))
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Slow async saga")
                .build();

        String expectedTransferId = UUID.nameUUIDFromBytes(
                (src.getAccountId() + ":" + idempotencyKey).getBytes(StandardCharsets.UTF_8)
        ).toString();
        String expectedRedisKey = RedisTransferIdempotencyLockAdapter.LOCK_KEY_PREFIX + expectedTransferId;

        // Pod 1 vượt ngưỡng 1s -> trả về PENDING, nhưng Saga vẫn chạy ngầm
        TransferResponseDTO pendingRes = transferHandler.handle(cmd);
        assertThat(pendingRes.getStatus()).isEqualTo(TransactionStatus.PENDING);

        // Lock trên Redis VẪN PHẢI ĐANG ĐƯỢC GIỮ vì luồng ngầm chưa xong!
        assertThat(simulatedRedisStore).containsKey(expectedRedisKey);

        // Nếu Pod 2 gọi đè ngay lúc luồng ngầm chưa xong -> bị chặn 409002!
        assertThatThrownBy(() -> transferHandler.handle(cmd))
                .isInstanceOf(InProgressConflictException.class);

        // Cho phép luồng Saga ngầm chạy xong
        allowBackgroundSagaFinish.countDown();

        for (int i = 0; i < 25; i++) {
            Thread.sleep(100);
            TransferView view = transferViewRepository.findById(expectedTransferId).orElse(null);
            if (view != null && "COMPLETED".equals(view.getStatus()) && !simulatedRedisStore.containsKey(expectedRedisKey)) {
                break;
            }
        }

        // Luồng ngầm xong -> Redis Lock đã được xóa sạch, trạng thái COMPLETED, retry trả về SUCCESS
        assertThat(simulatedRedisStore).doesNotContainKey(expectedRedisKey);
        TransferResponseDTO replayRes = transferHandler.handle(cmd);
        assertThat(replayRes.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
    }

    @Test
    @DisplayName("Khi Redis sập hoàn toàn: Hệ thống tự động fallback xuống DB, chuyển tiền thành công và vẫn chống trùng + check hash")
    void shouldFallbackGracefullyToDatabaseWhenRedisIsDown() {
        simulateRedisDown.set(true);

        AccountResponseDTO src = createTestAccount(new BigDecimal("1000000.00"));
        AccountResponseDTO dst = createTestAccount(new BigDecimal("500000.00"));

        String idempotencyKey = "redis-down-" + UUID.randomUUID();
        TransferRequestDTO cmd = TransferRequestDTO.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("250000.00"))
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Redis outage test")
                .build();

        TransferResponseDTO res1 = transferHandler.handle(cmd);
        assertThat(res1.getStatus()).isEqualTo(TransactionStatus.SUCCESS);

        // Retry cùng payload -> Replay bình thường
        TransferResponseDTO res2 = transferHandler.handle(cmd);
        assertThat(res2.getTransactionId()).isEqualTo(res1.getTransactionId());
        assertThat(res2.getStatus()).isEqualTo(TransactionStatus.SUCCESS);

        // Retry đổi số tiền -> Vẫn chặn 409001 qua DB!
        TransferRequestDTO tampered = TransferRequestDTO.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("300000.00"))
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Tampered during Redis outage")
                .build();

        assertThatThrownBy(() -> transferHandler.handle(tampered))
                .isExactlyInstanceOf(ConflictException.class);
    }
}
