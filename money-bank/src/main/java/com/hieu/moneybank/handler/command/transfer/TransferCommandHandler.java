package com.hieu.moneybank.handler.command.transfer;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.request.TransferRequestDTO;
import com.hieu.moneybank.dto.response.TransferResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.service.otp.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferCommandHandler implements CommandHandler<TransferRequestDTO, TransferResponseDTO> {

    private final CorebankClient corebankClient;
    private final OtpService otpService;

    @Override
    public TransferResponseDTO handle(TransferRequestDTO request) {
        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new BusinessException("Source and destination accounts must be different");
        }

        // Xác thực OTP nếu có truyền vào
        if (request.getOtp() != null && !request.getOtp().isBlank()) {
            otpService.validateOtp(request.getSourceAccountId(), request.getOtp());
        }

        // Tự động sinh Idempotency Key tại BE nếu Client không truyền
        if (request.getIdempotencyKey() == null || request.getIdempotencyKey().isBlank()) {
            String generatedKey = UUID.randomUUID().toString();
            request.setIdempotencyKey(generatedKey);
            log.info("Auto-generated server-side idempotencyKey={} for transfer from {} to {}",
                    generatedKey, request.getSourceAccountId(), request.getDestinationAccountId());
        }

        return corebankClient.executeTransfer(request);
    }
}
