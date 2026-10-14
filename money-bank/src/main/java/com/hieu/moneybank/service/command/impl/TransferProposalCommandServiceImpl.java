package com.hieu.moneybank.service.command.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieu.common.outbox.OutboxRecorder;
import com.hieu.moneybank.config.OtpProperties;
import com.hieu.moneybank.constant.ProposalStatus;
import com.hieu.moneybank.domain.TransferProposal;
import com.hieu.moneybank.dto.response.OtpResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.exception.NotFoundException;
import com.hieu.moneybank.repository.TransferProposalRepository;
import com.hieu.moneybank.service.command.TransferProposalCommandService;
import com.hieu.moneybank.service.otp.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferProposalCommandServiceImpl implements TransferProposalCommandService {

    private final TransferProposalRepository repository;
    private final OutboxRecorder outboxRecorder;
    private final ObjectMapper objectMapper;
    private final OtpService otpService;
    private final OtpProperties otpProperties;

    @Override
    @Transactional
    public TransferProposal save(TransferProposal proposal) {
        return repository.save(proposal);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransferProposal startValidating(String proposalId, String confirmerCustomerId) {
        TransferProposal proposal = repository.findByIdForUpdate(proposalId)
            .orElseThrow(() -> new NotFoundException("Proposal không tồn tại: " + proposalId));

        if (confirmerCustomerId != null && !confirmerCustomerId.equals(proposal.getInitiatorCustomerId())) {
            throw new BusinessException("Chỉ người tạo proposal mới có quyền xác nhận");
        }

        try {
            proposal.startValidating();
        } catch (IllegalStateException e) {
            log.warn("eventName=PROPOSAL_CONFIRM_INVALID_STATE proposalId={} status={}",
                proposal.getId(), proposal.getStatus());
            throw new BusinessException(e.getMessage());
        }

        return repository.saveAndFlush(proposal);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransferProposal finalizeTransfer(String proposalId, String corebankTxId, String failureReason, boolean isSuccess) {
        TransferProposal proposal = repository.findById(proposalId)
            .orElseThrow(() -> new NotFoundException("Proposal không tồn tại: " + proposalId));

        if (isSuccess) {
            proposal.markConfirmed(corebankTxId);
            recordOutboxEvent(proposal, "TransferConfirmed");
        } else {
            proposal.markFailed(failureReason);
            recordOutboxEvent(proposal, "TransferFailed");
        }

        return repository.saveAndFlush(proposal);
    }

    @Override
    @Transactional(readOnly = true)
    public OtpResponseDTO sendOtp(String proposalId, String customerId) {
        TransferProposal proposal = repository.findById(proposalId)
            .orElseThrow(() -> new NotFoundException("Proposal không tồn tại: " + proposalId));

        if (customerId != null && !customerId.equals(proposal.getInitiatorCustomerId())) {
            throw new BusinessException("Chỉ người tạo proposal mới có quyền nhận OTP");
        }

        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new BusinessException("Chỉ có thể lấy OTP cho proposal ở trạng thái PENDING");
        }

        String otp = otpService.generateOtp(proposalId);
        return OtpResponseDTO.builder()
            .proposalId(proposalId)
            .otp(otp)
            .message("Mã OTP đã được gửi thành công (Mặc định test: " + otp + ")")
            .expiresInSeconds(otpProperties.getExpiresInSeconds())
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
            outboxRecorder.record("TRANSFER", proposal.getId(), eventType, "v1", payload);
        } catch (JsonProcessingException e) {
            log.error("eventName=OUTBOX_SERIALIZE_FAILED proposalId={} eventType={} — event bị mất",
                proposal.getId(), eventType, e);
            throw new IllegalStateException("Failed to serialize outbox event", e);
        }
    }

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
