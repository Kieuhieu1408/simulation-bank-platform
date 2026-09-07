package com.hieu.moneybank.shared.idempotency;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.hieu.moneybank.shared.error.IdempotencyConflictException;
import com.hieu.moneybank.shared.error.InvalidRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Kiểm chứng {@code AD-DC-T01}, {@code AD-DC-T02}, {@code AD-DC-T03} và
 * {@code AD-TR-T01}, {@code AD-TR-T02}.
 *
 * <p>Đây là test quan trọng nhất của lớp idempotency: nó chứng minh unique
 * constraint thực sự là cơ chế quyết định dưới tải đồng thời, không phải chỉ là
 * ràng buộc trang trí. Test đơn luồng sẽ pass cả với implementation
 * check-then-act có race, nên không đủ.
 *
 * <p>Giới hạn: chạy trên H2. Hành vi lock và mã lỗi của Oracle khác, nên bộ test
 * này phải được chạy lại trên database đích trước khi lên production
 * (SRD 16.2).
 */
@SpringBootTest
@ActiveProfiles("test")
class IdempotencyServiceConcurrencyIT {

    private static final int CONCURRENT_REQUESTS = 100;

    @Autowired
    private IdempotencyService idempotencyService;

    @Autowired
    private CanonicalRequestHasher hasher;

    @Autowired
    private IdempotencyRecordRepository repository;

    private String newKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String transferHash(String amount) {
        return hasher.hash(CanonicalPayload.builder()
                .text("sourceAccount", "1234567890")
                .text("destinationAccount", "9876543210")
                .money("amount", new BigDecimal(amount), "VND"));
    }

    @Test
    @DisplayName("100 request đồng thời cùng key và cùng payload chỉ tạo đúng một claim")
    void concurrentIdenticalRequestsProduceExactlyOneClaim() throws Exception {
        String customerId = "CUST-" + newKey();
        String idempotencyKey = newKey();
        String requestHash = transferHash("1500000");

        AtomicInteger claimed = new AtomicInteger();
        AtomicInteger duplicates = new AtomicInteger();
        AtomicInteger leakedTechnicalErrors = new AtomicInteger();

        CountDownLatch startGate = new CountDownLatch(1);
        List<Callable<String>> tasks = new java.util.ArrayList<>(CONCURRENT_REQUESTS);
        for (int i = 0; i < CONCURRENT_REQUESTS; i++) {
            tasks.add(() -> {
                // Chặn toàn bộ thread ở cùng một điểm để tối đa hóa xác suất trùng
                // thời điểm insert, thay vì để chúng chạy lệch nhau.
                startGate.await();
                try {
                    IdempotencyClaim claim = idempotencyService.claim(
                            customerId, IdempotencyOperation.TRANSFER_CREATE, idempotencyKey, requestHash);
                    switch (claim) {
                        case IdempotencyClaim.Claimed c -> claimed.incrementAndGet();
                        case IdempotencyClaim.InProgress c -> duplicates.incrementAndGet();
                        case IdempotencyClaim.AlreadySettled c -> duplicates.incrementAndGet();
                    }
                    return claim.record().transactionId();
                } catch (DataIntegrityViolationException | IllegalStateException technical) {
                    // AD-DC-T02: duplicate không được lộ ra dưới dạng lỗi kỹ thuật.
                    leakedTechnicalErrors.incrementAndGet();
                    return null;
                }
            });
        }

        try (ExecutorService pool = Executors.newFixedThreadPool(32)) {
            List<Future<String>> futures = tasks.stream().map(pool::submit).toList();
            startGate.countDown();
            List<String> transactionIds = new java.util.ArrayList<>();
            for (Future<String> future : futures) {
                transactionIds.add(future.get(60, TimeUnit.SECONDS));
            }

            assertThat(leakedTechnicalErrors.get())
                    .as("duplicate không được trả về dưới dạng lỗi kỹ thuật")
                    .isZero();
            assertThat(claimed.get())
                    .as("chỉ một request được phép thực thi command")
                    .isEqualTo(1);
            assertThat(duplicates.get()).isEqualTo(CONCURRENT_REQUESTS - 1);
            // Mọi request phải nhận cùng transactionId để client polling ở đâu cũng
            // thấy đúng một giao dịch.
            assertThat(transactionIds).doesNotContainNull().containsOnly(transactionIds.getFirst());
        }

        assertThat(repository.findByCustomerIdAndOperationAndIdempotencyKey(
                customerId, IdempotencyOperation.TRANSFER_CREATE, idempotencyKey))
                .isPresent();
        assertThat(repository.count()).isPositive();
    }

    @Test
    @DisplayName("Cùng key nhưng khác payload luôn bị từ chối")
    void sameKeyDifferentPayloadIsRejected() {
        String customerId = "CUST-" + newKey();
        String idempotencyKey = newKey();

        IdempotencyClaim first = idempotencyService.claim(
                customerId, IdempotencyOperation.TRANSFER_CREATE, idempotencyKey, transferHash("1500000"));
        assertThat(first).isInstanceOf(IdempotencyClaim.Claimed.class);

        assertThatThrownBy(() -> idempotencyService.claim(
                customerId, IdempotencyOperation.TRANSFER_CREATE, idempotencyKey, transferHash("9999999")))
                .isInstanceOf(IdempotencyConflictException.class);
    }

    @Test
    @DisplayName("Retry cùng key và cùng payload nhận lại đúng transaction đang xử lý")
    void retryWithSamePayloadReturnsInProgress() {
        String customerId = "CUST-" + newKey();
        String idempotencyKey = newKey();
        String requestHash = transferHash("250000");

        IdempotencyClaim first = idempotencyService.claim(
                customerId, IdempotencyOperation.TRANSFER_CREATE, idempotencyKey, requestHash);
        IdempotencyClaim retry = idempotencyService.claim(
                customerId, IdempotencyOperation.TRANSFER_CREATE, idempotencyKey, requestHash);

        assertThat(retry).isInstanceOf(IdempotencyClaim.InProgress.class);
        assertThat(retry.record().transactionId()).isEqualTo(first.record().transactionId());
    }

    @Test
    @DisplayName("Cùng key ở hai operation khác nhau không chặn lẫn nhau")
    void keyScopeIsPerOperation() {
        String customerId = "CUST-" + newKey();
        String idempotencyKey = newKey();
        String requestHash = transferHash("100000");

        IdempotencyClaim transfer = idempotencyService.claim(
                customerId, IdempotencyOperation.TRANSFER_CREATE, idempotencyKey, requestHash);
        IdempotencyClaim proposal = idempotencyService.claim(
                customerId, IdempotencyOperation.ACCOUNT_PROPOSAL_CREATE, idempotencyKey, requestHash);

        assertThat(transfer).isInstanceOf(IdempotencyClaim.Claimed.class);
        assertThat(proposal).isInstanceOf(IdempotencyClaim.Claimed.class);
        assertThat(transfer.record().transactionId()).isNotEqualTo(proposal.record().transactionId());
    }

    @Test
    @DisplayName("Cùng key của hai khách hàng khác nhau không chặn lẫn nhau")
    void keyScopeIsPerCustomer() {
        String idempotencyKey = newKey();
        String requestHash = transferHash("100000");

        IdempotencyClaim first = idempotencyService.claim(
                "CUST-" + newKey(), IdempotencyOperation.TRANSFER_CREATE, idempotencyKey, requestHash);
        IdempotencyClaim second = idempotencyService.claim(
                "CUST-" + newKey(), IdempotencyOperation.TRANSFER_CREATE, idempotencyKey, requestHash);

        assertThat(first).isInstanceOf(IdempotencyClaim.Claimed.class);
        assertThat(second).isInstanceOf(IdempotencyClaim.Claimed.class);
    }

    @Test
    @DisplayName("Key sai khuôn dạng bị từ chối trước khi ghi database")
    void malformedKeyIsRejected() {
        String customerId = "CUST-" + newKey();
        String requestHash = transferHash("100000");

        assertThatThrownBy(() -> idempotencyService.claim(
                customerId, IdempotencyOperation.TRANSFER_CREATE, "1", requestHash))
                .isInstanceOf(InvalidRequestException.class);

        assertThatThrownBy(() -> idempotencyService.claim(
                customerId, IdempotencyOperation.TRANSFER_CREATE, "key with spaces and symbols!", requestHash))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    @DisplayName("Thiếu customerId bị từ chối, không có định danh mặc định")
    void missingCustomerIsRejected() {
        assertThatThrownBy(() -> idempotencyService.claim(
                null, IdempotencyOperation.TRANSFER_CREATE, newKey(), transferHash("100000")))
                .isInstanceOf(InvalidRequestException.class);
    }
}
