package com.hieu.cms.service;

import com.hieu.cms.dto.ProposalCreateRequest;
import com.hieu.cms.entity.Proposal;
import com.hieu.cms.entity.ProposalChangeLog;
import com.hieu.cms.repository.ProposalChangeLogRepository;
import com.hieu.cms.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProposalService {
    
    private final ProposalRepository proposalRepository;
    private final ProposalChangeLogRepository changeLogRepository;

    @Transactional
    public void createProposal(ProposalCreateRequest request) {
        Proposal proposal = new Proposal();
        proposal.setId(UUID.randomUUID());
        proposal.setProposalCode("PRP-" + System.currentTimeMillis()); // Mock generation
        proposal.setCustomerCif(request.getCustomerCif());
        proposal.setProposalType(request.getType());
        proposal.setNewData(request.getNewData());
        proposal.setDocumentUrls(request.getDocumentUrls());
        proposal.setStatus("PENDING");
        proposal.setMakerId(getCurrentUserId());
        proposal.setCreatedAt(LocalDateTime.now());
        proposalRepository.save(proposal);
    }

    @Transactional
    public void cancelProposal(UUID id) {
        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));

        if (!"PENDING".equals(proposal.getStatus())) {
            throw new RuntimeException("Only PENDING proposals can be cancelled");
        }

        // Only the maker who created this proposal can cancel it
        String currentUser = getCurrentUserId();
        if (!currentUser.equals(proposal.getMakerId())) {
            throw new RuntimeException("Only the maker can cancel their own proposal");
        }

        moveToChangeLog(proposal, "CANCELLED", null);
        proposalRepository.delete(proposal);
    }

    @Transactional
    public void approveProposal(UUID id) {
        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));

        if (!"PENDING".equals(proposal.getStatus())) {
            throw new RuntimeException("Only PENDING proposals can be approved");
        }

        // Maker-Checker separation: the maker cannot approve their own proposal
        String currentUser = getCurrentUserId();
        if (currentUser.equals(proposal.getMakerId())) {
            throw new RuntimeException("Maker cannot approve their own proposal (four-eyes principle)");
        }

        // TODO: Sync data to Core/Customer Service

        moveToChangeLog(proposal, "APPROVED", null);
        proposalRepository.delete(proposal);
    }

    @Transactional
    public void rejectProposal(UUID id, String reason) {
        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));

        if (!"PENDING".equals(proposal.getStatus())) {
            throw new RuntimeException("Only PENDING proposals can be rejected");
        }

        // Maker-Checker separation: the maker cannot reject their own proposal
        String currentUser = getCurrentUserId();
        if (currentUser.equals(proposal.getMakerId())) {
            throw new RuntimeException("Maker cannot reject their own proposal (four-eyes principle)");
        }

        moveToChangeLog(proposal, "REJECTED", reason);
        proposalRepository.delete(proposal);
    }

    private void moveToChangeLog(Proposal proposal, String finalStatus, String reason) {
        ProposalChangeLog log = new ProposalChangeLog();
        log.setId(UUID.randomUUID());
        log.setProposalId(proposal.getId());
        log.setProposalCode(proposal.getProposalCode());
        log.setCustomerCif(proposal.getCustomerCif());
        log.setProposalType(proposal.getProposalType());
        log.setOldData(proposal.getOldData());
        log.setNewData(proposal.getNewData());
        log.setDocumentUrls(proposal.getDocumentUrls());
        log.setFinalStatus(finalStatus);
        log.setMakerId(proposal.getMakerId());
        log.setCheckerId(getCurrentUserId());
        log.setReason(reason);
        log.setCompletedAt(LocalDateTime.now());
        changeLogRepository.save(log);
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
