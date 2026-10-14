package com.hieu.moneybank.dto.request;

import com.hieu.common.cqrs.Query;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferProposalHistoryRequestDTO implements Query<Page<TransferProposalResponseDTO>> {
    @NotBlank
    private String accountId;
    private Instant from;
    private Instant to;
    private Pageable pageable;
}
