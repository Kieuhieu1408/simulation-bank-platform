package com.hieu.moneybank.handler.command.transfer;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.request.TransferRequestDTO;
import com.hieu.moneybank.dto.response.TransferResponseDTO;
import com.hieu.moneybank.domain.Account;
import com.hieu.moneybank.domain.BankTransaction;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.exception.NotFoundException;
import com.hieu.moneybank.service.command.AccountCommandService;
import com.hieu.moneybank.service.command.TransactionCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@com.hieu.common.annotation.CommandHandler
@RequiredArgsConstructor
@Slf4j
public class TransferCommandHandler implements CommandHandler<TransferRequestDTO, TransferResponseDTO>  {

    private final AccountCommandService accountCommandService;
    private final TransactionCommandService transactionCommandService;
    private final CorebankClient corebankClient;

    @Override
    public TransferResponseDTO handle(TransferRequestDTO request) {
        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new BusinessException("Source and destination accounts must be different");
        }

        // 1. Kiểm tra tài khoản tồn tại
        Account source = accountCommandService.findById(request.getSourceAccountId())
                .orElseThrow(() -> new NotFoundException("Source account not found"));
        Account destination = accountCommandService.findById(request.getDestinationAccountId())
                .orElseThrow(() -> new NotFoundException("Destination account not found"));

        String currency = request.getCurrency().toUpperCase();

        // 2. Tạo log giao dịch với trạng thái PENDING
        BankTransaction transaction = new BankTransaction(
                source,
                destination,
                request.getAmount(),
                currency,
                request.getIdempotencyKey(),
                request.getDescription()
        );
        // Lưu xuống DB (đồng bộ để đảm bảo có record)
        BankTransaction savedTransaction = transactionCommandService.save(transaction);
        log.info("Transaction logged locally in moneybank as PENDING with ID: {}", savedTransaction.getId());

        // 3. Gọi Corebank và cập nhật trạng thái bất đồng bộ
        try {
            TransferResponseDTO corebankResponse = corebankClient.executeTransfer(request);
            
            // Cập nhật trạng thái SUCCESS ở một thread khác (bất đồng bộ)
            CompletableFuture.runAsync(() -> {
                try {
                    savedTransaction.markAsSuccess();
                    transactionCommandService.save(savedTransaction);
                    log.info("Transaction {} marked as SUCCESS asynchronously", savedTransaction.getId());
                } catch (Exception ex) {
                    log.error("Failed to asynchronously mark transaction {} as SUCCESS", savedTransaction.getId(), ex);
                }
            });
            
            // Trả về kết quả ngay lập tức cho người dùng
            return corebankResponse;
        } catch (Exception e) {
            // Cập nhật trạng thái FAILED ở một thread khác (bất đồng bộ)
            CompletableFuture.runAsync(() -> {
                try {
                    savedTransaction.markAsFailed();
                    transactionCommandService.save(savedTransaction);
                    log.info("Transaction {} marked as FAILED asynchronously", savedTransaction.getId());
                } catch (Exception ex) {
                    log.error("Failed to asynchronously mark transaction {} as FAILED", savedTransaction.getId(), ex);
                }
            });
            throw e; // Vẫn ném lỗi ra cho GlobalExceptionHandler xử lý để trả về người dùng
        }
    }
}
