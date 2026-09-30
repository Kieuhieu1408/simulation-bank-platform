package com.hieu.moneybank.api.impl;

import com.hieu.moneybank.api.BaseController;
import com.hieu.moneybank.api.TransferProposalController;
import com.hieu.moneybank.dto.request.ConfirmTransferProposalCommand;
import com.hieu.moneybank.dto.request.CreateTransferProposalCommand;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import com.hieu.moneybank.handler.query.transfer.GetTransferProposalQueryHandler.GetProposalByIdQuery;
import com.hieu.moneybank.handler.query.transfer.GetTransferProposalQueryHandler.GetProposalsByAccountQuery;
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

    @Override
    public ResponseEntity<TransferProposalResponseDTO> create(CreateTransferProposalCommand request) {
        // initiatorCustomerId được lấy từ security context trong môi trường thực.
        // Demo: để null → handler bỏ qua kiểm tra quyền.
        TransferProposalResponseDTO result = dispatcher.dispatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Override
    public ResponseEntity<TransferProposalResponseDTO> confirm(String id) {
        ConfirmTransferProposalCommand command = ConfirmTransferProposalCommand.builder()
            .proposalId(id)
            // confirmerCustomerId: lấy từ SecurityContext — demo để null
            .build();
        return execute(command, TransferProposalResponseDTO.class);
    }

    @Override
    public ResponseEntity<TransferProposalResponseDTO> get(String id) {
        return executeQuery(new GetProposalByIdQuery(id), TransferProposalResponseDTO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<Page<TransferProposalResponseDTO>> history(
            String accountId, Instant from, Instant to, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return executeQuery(
            new GetProposalsByAccountQuery(accountId, from, to, pageable),
            (Class<Page<TransferProposalResponseDTO>>) (Class<?>) Page.class
        );
    }
}
