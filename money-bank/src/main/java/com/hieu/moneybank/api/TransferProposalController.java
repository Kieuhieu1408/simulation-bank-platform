package com.hieu.moneybank.api;

import com.hieu.moneybank.dto.request.CreateTransferProposalCommand;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

/**
 * API cho Transfer Proposal — luồng 2 bước (U-06).
 *
 * <pre>
 * POST  /api/v1/transfers/proposals               ← Tạo proposal (bước 1)
 * POST  /api/v1/transfers/proposals/{id}/confirm  ← Xác nhận (bước 2)
 * GET   /api/v1/transfers/proposals/{id}          ← Lấy proposal theo ID
 * GET   /api/v1/transfers/proposals?accountId=&from=&to=&page=&size= ← Lịch sử
 * </pre>
 */
@RequestMapping("/api/v1/transfers/proposals")
public interface TransferProposalController {

    /**
     * Bước 1: Tạo proposal.
     * Corebank chưa được gọi. Trả về proposal với status=PENDING.
     */
    @PostMapping
    ResponseEntity<TransferProposalResponseDTO> create(
        @Valid @RequestBody CreateTransferProposalCommand request
    );

    /**
     * Bước 2: Xác nhận proposal.
     * Gọi Corebank và cập nhật status → CONFIRMED | FAILED.
     */
    @PostMapping("/{id}/confirm")
    ResponseEntity<TransferProposalResponseDTO> confirm(@PathVariable String id);

    /**
     * Lấy proposal theo ID.
     */
    @GetMapping("/{id}")
    ResponseEntity<TransferProposalResponseDTO> get(@PathVariable String id);

    /**
     * Lịch sử proposal của một account.
     */
    @GetMapping
    ResponseEntity<Page<TransferProposalResponseDTO>> history(
        @RequestParam String accountId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    );
}
