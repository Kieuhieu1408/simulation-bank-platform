package com.hieu.moneybank.service.command.impl;

import com.hieu.moneybank.domain.TransferProposal;
import com.hieu.moneybank.repository.TransferProposalRepository;
import com.hieu.moneybank.service.command.TransferProposalCommandService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransferProposalCommandServiceImpl implements TransferProposalCommandService {

    private final TransferProposalRepository repository;

    public TransferProposalCommandServiceImpl(TransferProposalRepository repository) {
        this.repository = repository;
    }

    @Override
    public TransferProposal save(TransferProposal proposal) {
        return repository.save(proposal);
    }

    @Override
    public Optional<TransferProposal> findByIdForUpdate(String id) {
        return repository.findByIdForUpdate(id);
    }

    @Override
    public Optional<TransferProposal> findByIdempotencyKey(String idempotencyKey) {
        return repository.findByIdempotencyKey(idempotencyKey);
    }
}
