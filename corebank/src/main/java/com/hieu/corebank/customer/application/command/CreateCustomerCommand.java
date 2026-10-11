package com.hieu.corebank.customer.application.command;

import com.hieu.common.cqrs.Command;
import com.hieu.corebank.customer.api.dto.response.CustomerResponseDTO;

public record CreateCustomerCommand(
        String cifNumber,
        String nationalId,
        String fullName,
        String phoneNumber,
        String email
) implements Command<CustomerResponseDTO> {

    public CreateCustomerCommand(String cifNumber) {
        this(cifNumber, null, null, null, null);
    }
}
