package com.hieu.corebank.card.api.dto.request;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardGetRequestDTO implements Query<IssueCardResponseDTO> {
    @NotBlank
    private String cardId;
}
