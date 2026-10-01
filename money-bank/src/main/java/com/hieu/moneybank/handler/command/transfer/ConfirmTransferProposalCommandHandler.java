package com.hieu.moneybank.handler.command.transfer;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.domain.TransferProposal;
import com.hieu.moneybank.dto.request.ConfirmTransferProposalCommand;
import com.hieu.moneybank.dto.request.TransferRequestDTO;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import com.hieu.moneybank.dto.response.TransferResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.service.command.TransferProposalCommandService;
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

    @Override
    public TransferProposalResponseDTO handle(ConfirmTransferProposalCommand command) {
        
        // 1. Transaction 1: Lock và chuyển sang VALIDATING
        TransferProposal proposal = proposalService.startValidating(
            command.getProposalId(), command.getConfirmerCustomerId());

        // 2. Gọi Corebank (Ngoài transaction)
        String corebankTxId   = null;
        String failureReason  = null;
        boolean corebankOk    = false;

        try {
            TransferRequestDTO req = buildCorebankRequest(proposal);
            TransferResponseDTO res = corebankClient.executeTransfer(req);
            corebankTxId = res.getTransactionId();
            corebankOk = true;
            log.info("eventName=COREBANK_TRANSFER_OK proposalId={} corebankTxId={}",
                proposal.getId(), corebankTxId);

        } catch (BusinessException e) {
            // 4xx: lỗi nghiệp vụ cuối → FAILED
            failureReason = "COREBANK_REJECTED: " + sanitize(e.getMessage());
            log.warn("eventName=COREBANK_TRANSFER_REJECTED proposalId={} reason={}",
                proposal.getId(), failureReason);

        } catch (Exception e) {
            // 5xx / timeout: không rõ trạng thái tại Corebank
            // Bắn exception ngay, KHÔNG finalize. Proposal giữ nguyên trạng thái VALIDATING.
            log.error("eventName=COREBANK_TRANSFER_UNCERTAIN proposalId={} errorClass={}",
                proposal.getId(), e.getClass().getSimpleName(), e);
            throw new RuntimeException("Kết quả chuyển tiền chưa xác định, vui lòng kiểm tra lại sau.", e);
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
