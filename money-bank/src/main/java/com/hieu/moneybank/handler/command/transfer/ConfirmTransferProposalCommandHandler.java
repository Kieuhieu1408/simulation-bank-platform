package com.hieu.moneybank.handler.command.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieu.common.cqrs.CommandHandler;
import com.hieu.common.outbox.OutboxRecorder;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.domain.TransferProposal;
import com.hieu.moneybank.dto.request.ConfirmTransferProposalCommand;
import com.hieu.moneybank.dto.request.TransferRequestDTO;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import com.hieu.moneybank.dto.response.TransferResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.exception.NotFoundException;
import com.hieu.moneybank.service.command.TransferProposalCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler xác nhận Transfer Proposal (U-06 bước 2/2).
 *
 * <h3>State transition</h3>
 * <pre>
 * PENDING ──startValidating()──► VALIDATING
 *                                     │
 *                   ┌─────────────────┤──────────────────────┐
 *                   │ corebank ok                             │ corebank fail
 *                   ▼                                         ▼
 *             markConfirmed()                           markFailed()
 *             CONFIRMED (terminal)                      FAILED (terminal)
 * </pre>
 *
 * <h3>Đảm bảo atomicity</h3>
 * <p>Một @Transactional bao gồm:
 * <ol>
 *   <li>Pessimistic lock proposal row.</li>
 *   <li>startValidating() — chặn concurrent confirm.</li>
 *   <li>Gọi Corebank (blocking HTTP — trade-off: giữ connection trong khi chờ).</li>
 *   <li>markConfirmed/markFailed + OutboxRecorder.record() — commit cùng transaction.</li>
 * </ol>
 *
 * <p>Nếu Corebank timeout/network error → proposal FAILED với failureReason="OUTCOME_UNKNOWN".
 * Reconciliation job (U-09) sẽ xử lý sau.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ConfirmTransferProposalCommandHandler
        implements CommandHandler<ConfirmTransferProposalCommand, TransferProposalResponseDTO> {

    private final TransferProposalCommandService proposalService;
    private final CorebankClient corebankClient;
    private final OutboxRecorder outboxRecorder;
    private final ObjectMapper objectMapper;

    private static final String AGGREGATE_TYPE  = "TRANSFER";
    private static final String SCHEMA_VERSION  = "v1";
    private static final String EVENT_CONFIRMED = "TransferConfirmed";
    private static final String EVENT_FAILED    = "TransferFailed";

    @Override
    @Transactional
    public TransferProposalResponseDTO handle(ConfirmTransferProposalCommand command) {

        // 1. Load proposal với pessimistic lock — chặn concurrent confirm
        TransferProposal proposal = proposalService.findByIdForUpdate(command.getProposalId())
            .orElseThrow(() -> new NotFoundException(
                "Proposal không tồn tại: " + command.getProposalId()));

        // 2. Kiểm tra quyền: chỉ initiator mới được confirm
        //    (bỏ qua khi confirmerCustomerId là null — demo/test mode)
        if (command.getConfirmerCustomerId() != null
                && !command.getConfirmerCustomerId().equals(proposal.getInitiatorCustomerId())) {
            throw new BusinessException("Chỉ người tạo proposal mới có quyền xác nhận");
        }

        // 3. State guard — TRF-INV-005
        //    startValidating() ném IllegalStateException nếu không PENDING
        try {
            proposal.startValidating();
        } catch (IllegalStateException e) {
            log.warn("eventName=PROPOSAL_CONFIRM_INVALID_STATE proposalId={} status={}",
                proposal.getId(), proposal.getStatus());
            throw new BusinessException(e.getMessage());
        }

        // Lưu trạng thái VALIDATING ngay — để concurrent request thấy row đã lock
        proposalService.save(proposal);

        // 4. Gọi Corebank
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
            // 4xx từ Corebank: lỗi nghiệp vụ cuối — không retry
            failureReason = "COREBANK_REJECTED: " + sanitize(e.getMessage());
            log.warn("eventName=COREBANK_TRANSFER_REJECTED proposalId={} reason={}",
                proposal.getId(), failureReason);

        } catch (Exception e) {
            // 5xx hoặc network error: kết quả chưa xác định
            failureReason = "OUTCOME_UNKNOWN";
            log.error("eventName=COREBANK_TRANSFER_UNCERTAIN proposalId={} errorClass={}",
                proposal.getId(), e.getClass().getSimpleName(), e);
        }

        // 5. Cập nhật state + ghi Outbox trong cùng transaction
        if (corebankOk) {
            proposal.markConfirmed(corebankTxId);
            recordOutboxEvent(proposal, EVENT_CONFIRMED);
            log.info("eventName=PROPOSAL_CONFIRMED proposalId={} corebankTxId={}",
                proposal.getId(), corebankTxId);
        } else {
            proposal.markFailed(failureReason);
            recordOutboxEvent(proposal, EVENT_FAILED);
            log.warn("eventName=PROPOSAL_FAILED proposalId={} reason={}",
                proposal.getId(), failureReason);
        }

        return TransferProposalResponseDTO.from(proposalService.save(proposal));
    }

    // ──────────────────────────────────────────────────────────────────────────

    private TransferRequestDTO buildCorebankRequest(TransferProposal p) {
        return TransferRequestDTO.builder()
            .sourceAccountId(p.getSourceAccountId())
            .destinationAccountId(p.getDestinationAccountId())
            .amount(p.getAmount())
            .currency(p.getCurrency())
            .idempotencyKey(p.getIdempotencyKey()) // Corebank cũng idempotent với cùng key
            .description(p.getDescription())
            .build();
    }

    private void recordOutboxEvent(TransferProposal proposal, String eventType) {
        try {
            String payload = objectMapper.writeValueAsString(new TransferOutboxPayload(
                proposal.getId(),
                proposal.getCorebankTransactionId(),
                proposal.getSourceAccountId(),
                proposal.getDestinationAccountId(),
                proposal.getAmount().toPlainString(),
                proposal.getCurrency(),
                proposal.getStatus().name(),
                proposal.getFailureReason()
            ));
            outboxRecorder.record(AGGREGATE_TYPE, proposal.getId(), eventType, SCHEMA_VERSION, payload);
        } catch (JsonProcessingException e) {
            // Không để lỗi serialize Outbox rollback giao dịch tài chính đã thành công
            log.error("eventName=OUTBOX_SERIALIZE_FAILED proposalId={} eventType={} — event bị mất",
                proposal.getId(), eventType, e);
        }
    }

    private String sanitize(String message) {
        if (message == null) return "UNKNOWN";
        return message.length() > 200 ? message.substring(0, 200) + "..." : message;
    }

    /** Outbox payload — sanitized, không chứa PAN/CVV/token. */
    record TransferOutboxPayload(
        String proposalId,
        String corebankTransactionId,
        String sourceAccountId,
        String destinationAccountId,
        String amount,
        String currency,
        String status,
        String failureReason
    ) {}
}
