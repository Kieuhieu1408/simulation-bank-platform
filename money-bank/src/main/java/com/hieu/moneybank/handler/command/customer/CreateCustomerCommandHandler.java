package com.hieu.moneybank.handler.command.customer;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.request.CustomerCreateRequestDTO;
import com.hieu.moneybank.dto.response.CustomerResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateCustomerCommandHandler implements CommandHandler<CustomerCreateRequestDTO, CustomerResponseDTO> {

    private final CorebankClient corebankClient;

    @Override
    public CustomerResponseDTO handle(CustomerCreateRequestDTO command) {
        return corebankClient.createCustomer(command);
    }
}
