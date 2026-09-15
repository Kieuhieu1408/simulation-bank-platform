package com.hieu.moneybank.pipeline;

import com.hieu.common.annotation.IdempotentCommand;
import com.hieu.common.cqrs.Command;
import com.hieu.common.cqrs.PipelineBehavior;
import com.hieu.common.cqrs.RequestHandlerDelegate;
import com.hieu.moneybank.domain.BankTransaction;
import com.hieu.moneybank.dto.request.TransferRequestDTO;
import com.hieu.moneybank.dto.response.TransferResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.service.command.TransactionCommandService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Middleware chặn các Command có gắn @IdempotentCommand tại MoneyBank.
 */
@Component
@Order(3) // Chạy sau Logging và Validation
public class IdempotencyBehavior<C extends Command<R>, R> implements PipelineBehavior<C, R> {

    private final TransactionCommandService transactionCommandService;

    public IdempotencyBehavior(TransactionCommandService transactionCommandService) {
        this.transactionCommandService = transactionCommandService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R handle(C request, RequestHandlerDelegate<R> next) {
        IdempotentCommand annotation = request.getClass().getAnnotation(IdempotentCommand.class);
        if (annotation == null) {
            return next.handle(); // Không yêu cầu Idempotency
        }

        if (request instanceof TransferRequestDTO transferRequest) {
            BankTransaction existing = transactionCommandService.findByIdempotencyKey(transferRequest.getIdempotencyKey());
            
            if (existing != null) {
                // Đã xử lý trước đó -> check conflict
                if (!sameRequest(existing, transferRequest)) {
                    throw new BusinessException("idempotencyKey was used for another transfer");
                }
                // Trả về kết quả cũ
                return (R) TransferResponseDTO.from(existing);
            }
        }

        // Nếu là request mới tinh -> Đi tiếp vào Handler lõi
        return next.handle();
    }

    private boolean sameRequest(BankTransaction t, TransferRequestDTO r) {
        return t.getSourceAccount().getId().equals(r.getSourceAccountId())
                && t.getDestinationAccount().getId().equals(r.getDestinationAccountId())
                && t.getAmount().compareTo(r.getAmount()) == 0
                && t.getCurrency().equalsIgnoreCase(r.getCurrency());
    }
}
