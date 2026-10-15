package com.hieu.moneybank.handler.command.transfer;

import com.hieu.common.constant.CommonErrorCode;
import com.hieu.common.constant.ErrorCategory;
import com.hieu.common.cqrs.CommandHandler;
import com.hieu.common.exception.CommonException;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.domain.TransferProposal;
import com.hieu.moneybank.dto.request.ConfirmTransferProposalCommand;
import com.hieu.moneybank.dto.request.TransferRequestDTO;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import com.hieu.moneybank.dto.response.TransferResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.service.command.TransferProposalCommandService;
import com.hieu.moneybank.service.otp.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfirmTransferProposalCommandHandler
        implements CommandHandler<ConfirmTransferProposalCommand, TransferProposalResponseDTO> {

    private final TransferProposalCommandService proposalService;
    private final CorebankClient corebankClient;
    private final OtpService otpService;

    @Override
    public TransferProposalResponseDTO handle(ConfirmTransferProposalCommand command) {

        // 0. Xác thực OTP
        otpService.validateOtp(command.getProposalId(), command.getOtp());

        // 1. Transaction 1: Lock và chuyển sang VALIDATING
        TransferProposal proposal = proposalService.startValidating(
            command.getProposalId(), command.getConfirmerCustomerId());

        // 2. Gọi Corebank (ngoài transaction)
        String corebankTxId  = null;
        String failureReason = null;
        boolean corebankOk   = false;

        try {
            TransferRequestDTO req = buildCorebankRequest(proposal);
            TransferResponseDTO res = corebankClient.executeTransfer(req);
            corebankTxId = res.getTransactionId();
            corebankOk = true;
            log.info("eventName=COREBANK_TRANSFER_OK proposalId={} corebankTxId={}",
                proposal.getId(), corebankTxId);

        } catch (BusinessException e) {
            // 4xx: Corebank từ chối dứt điểm → FAILED
            failureReason = "COREBANK_REJECTED: " + sanitize(e.getMessage());
            log.warn("eventName=COREBANK_TRANSFER_REJECTED proposalId={} reason={}",
                proposal.getId(), failureReason);

        } catch (CommonException e) {
            // OPERATION_OUTCOME_UNKNOWN (5xx / timeout): kết quả chưa xác định.
            // TRF-INV-008: KHÔNG được finalize thành FAILED. Proposal giữ VALIDATING.
            // Trả 202 ACCEPTED để client tự tra cứu trạng thái.
            log.error("eventName=COREBANK_TRANSFER_UNCERTAIN proposalId={} errorCode={}",
                proposal.getId(), e.errorCode().code(), e);
            throw e;  // GlobalExceptionHandler sẽ trả HTTP 202

        } catch (Exception e) {
            // Lỗi kỹ thuật không lường trước (network interrupted, serialization lỗi…)
            // Cũng giữ VALIDATING — không biết kết quả tại Corebank.
            log.error("eventName=COREBANK_TRANSFER_UNCERTAIN_UNEXPECTED proposalId={} errorClass={}",
                proposal.getId(), e.getClass().getSimpleName(), e);
            throw new CommonException(CommonErrorCode.OPERATION_OUTCOME_UNKNOWN,
                    "Kết quả chuyển tiền chưa xác định do lỗi kỹ thuật", e);
        }

        // 3. Transaction 2: Cập nhật state CONFIRMED/FAILED và ghi Outbox
        TransferProposal finalized = proposalService.finalizeTransfer(
            proposal.getId(), corebankTxId, failureReason, corebankOk);

        return TransferProposalResponseDTO.from(finalized);
    }

    private TransferRequestDTO buildCorebankRequest(TransferProposal p) {
        return TransferRequestDTO.builder()
            .sourceAccountId(p.getSourceAccountId())
            .destinationAccountId(p.getDestinationAccountId())
            .amount(p.getAmount())
            .currency(p.getCurrency())
            .idempotencyKey(p.getIdempotencyKey())
            .description(p.getDescription())
            .build();
    }

    private String sanitize(String message) {
        if (message == null) return "UNKNOWN";
        return message.length() > 200 ? message.substring(0, 200) + "..." : message;
    }
}
