package com.hieu.moneybank.handler.command.account;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.request.AccountCreateRequestDTO;
import com.hieu.moneybank.dto.response.AccountResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateAccountCommandHandler implements CommandHandler<AccountCreateRequestDTO, AccountResponseDTO> {

    private final CorebankClient corebankClient;

    @Override
    public AccountResponseDTO handle(AccountCreateRequestDTO request) {
        return corebankClient.createAccount(request);
    }
}
