package com.hieu.corebank.dto.request;

import com.hieu.common.cqrs.Command;
import com.hieu.corebank.dto.response.IssueCardResponseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueCardRequestDTO implements Command<IssueCardResponseDTO> {
    @NotBlank
    private String accountId;
}