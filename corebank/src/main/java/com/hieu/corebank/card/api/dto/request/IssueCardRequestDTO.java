package com.hieu.corebank.card.api.dto.request;

import com.hieu.corebank.card.application.command.IssueCardCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueCardRequestDTO {
    @NotBlank
    private String accountId;

    public IssueCardCommand toCommand() {
        return new IssueCardCommand(accountId);
    }
}
