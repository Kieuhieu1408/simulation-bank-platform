package com.hieu.corebank.transfer;

import com.hieu.common.constant.TransactionStatus;
import com.hieu.corebank.account.api.dto.request.AccountCreateRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.application.command.CreateAccountCommandHandler;
import com.hieu.corebank.account.infrastructure.projection.AccountView;
import com.hieu.corebank.account.infrastructure.projection.AccountViewRepository;
import com.hieu.corebank.customer.application.command.CreateCustomerCommand;
import com.hieu.corebank.customer.application.command.CreateCustomerCommandHandler;
import com.hieu.corebank.transfer.api.dto.request.TransferRequestDTO;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import com.hieu.corebank.transfer.application.command.TransferHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TransferConcurrencyTest {

    @Autowired
    private CreateCustomerCommandHandler createCustomerHandler;

    @Autowired
    private CreateAccountCommandHandler createAccountHandler;

    @Autowired
    private TransferHandler transferHandler;

    @Autowired
    private AccountViewRepository accountViewRepository;

    @Test
    @DisplayName("AC-CB-03: Bắn đồng thời 50 request chuyển tiền từ 1 tài khoản chỉ đủ tiền cho 1 GD: Duy nhất 1 GD thành công, không âm tiền")
    void testFiftyConcurrentTransfersOnlyOneSucceeds() throws Exception {
        int threadCount = 50;
        BigDecimal transferAmount = new BigDecimal("100000.00");
        BigDecimal initialBalance = new BigDecimal("100000.00"); // Chỉ đủ cho đúng 1 giao dịch!

        // Tạo tài khoản nguồn và đích
        String cif1 = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif1));
        AccountResponseDTO src = createAccountHandler.handle(
                new AccountCreateRequestDTO(cif1, "VND", initialBalance).toCommand());

        String cif2 = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif2));
        AccountResponseDTO dst = createAccountHandler.handle(
                new AccountCreateRequestDTO(cif2, "VND", BigDecimal.ZERO).toCommand());

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final String idempotencyKey = "concurrent-tx-" + i + "-" + UUID.randomUUID();
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await(); // Chờ tất cả 50 threads sẵn sàng để xuất phát cùng thời điểm
                    TransferRequestDTO cmd = TransferRequestDTO.builder()
                            .sourceAccountId(src.getAccountId())
                            .destinationAccountId(dst.getAccountId())
                            .amount(transferAmount)
                            .currency("VND")
                            .idempotencyKey(idempotencyKey)
                            .description("Concurrent test")
                            .build();

                    TransferResponseDTO response = transferHandler.handle(cmd);
                    if (response != null && response.getStatus() == TransactionStatus.SUCCESS) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } catch (Exception ex) {
                    // Lỗi do thiếu số dư hoặc Optimistic lock conflict
                    failureCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown(); // Bắn 50 request cùng lúc!
        boolean finished = doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(finished).isTrue();
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(49);

        // Kiểm tra số dư tài khoản nguồn: chính xác 0 VND, TUYỆT ĐỐI KHÔNG ÂM TIỀN
        AccountView srcView = accountViewRepository.findById(src.getAccountId()).orElseThrow();
        assertThat(srcView.getAvailableBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(srcView.getActualBalance()).isEqualByComparingTo(BigDecimal.ZERO);

        // Kiểm tra số dư tài khoản đích: chính xác 100,000 VND
        AccountView dstView = accountViewRepository.findById(dst.getAccountId()).orElseThrow();
        assertThat(dstView.getAvailableBalance()).isEqualByComparingTo(transferAmount);
        assertThat(dstView.getActualBalance()).isEqualByComparingTo(transferAmount);
    }
}
