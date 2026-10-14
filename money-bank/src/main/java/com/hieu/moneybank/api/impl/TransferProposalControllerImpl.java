package com.hieu.moneybank.api.impl;

import com.hieu.common.security.IdentityContextResolver;
import com.hieu.moneybank.api.BaseController;
import com.hieu.moneybank.api.TransferProposalController;
import com.hieu.moneybank.dto.request.ConfirmTransferProposalCommand;
import com.hieu.moneybank.dto.request.CreateTransferProposalCommand;
import com.hieu.moneybank.dto.request.TransferProposalGetRequestDTO;
import com.hieu.moneybank.dto.request.TransferProposalHistoryRequestDTO;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class TransferProposalControllerImpl extends BaseController implements TransferProposalController {

    private final IdentityContextResolver identityContextResolver;
    private final com.hieu.moneybank.service.command.TransferProposalCommandService proposalService;

    @Autowired
    public TransferProposalControllerImpl(IdentityContextResolver identityContextResolver,
                                          com.hieu.moneybank.service.command.TransferProposalCommandService proposalService) {
        this.identityContextResolver = identityContextResolver;
        this.proposalService = proposalService;
    }

    @Override
    public ResponseEntity<TransferProposalResponseDTO> create(CreateTransferProposalCommand request) {
        request.setInitiatorCustomerId(identityContextResolver.requireCurrent().customerId());
        TransferProposalResponseDTO result = dispatcher.dispatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Override
    public ResponseEntity<TransferProposalResponseDTO> confirm(String id, com.hieu.moneybank.dto.request.ConfirmTransferProposalRequestDTO request) {
        String otp = (request != null) ? request.getOtp() : null;
        ConfirmTransferProposalCommand command = ConfirmTransferProposalCommand.builder()
            .proposalId(id)
            .confirmerCustomerId(identityContextResolver.requireCurrent().customerId())
            .otp(otp)
            .build();
        return execute(command, TransferProposalResponseDTO.class);
    }

    @Override
    public ResponseEntity<com.hieu.moneybank.dto.response.OtpResponseDTO> sendOtp(String id) {
        String customerId = identityContextResolver.requireCurrent().customerId();
        com.hieu.moneybank.dto.response.OtpResponseDTO response = proposalService.sendOtp(id, customerId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TransferProposalResponseDTO> get(String id) {
        return executeQuery(new TransferProposalGetRequestDTO(id), TransferProposalResponseDTO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<Page<TransferProposalResponseDTO>> history(
            String accountId, Instant from, Instant to, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return executeQuery(
            new TransferProposalHistoryRequestDTO(accountId, from, to, pageable),
            (Class<Page<TransferProposalResponseDTO>>) (Class<?>) Page.class
        );
    }
}
