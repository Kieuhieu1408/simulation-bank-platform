package com.hieu.corebank.customer.application.command;

import com.hieu.corebank.customer.api.dto.request.CustomerCreateRequestDTO;

public class CreateCustomerCommand extends CustomerCreateRequestDTO {
    public CreateCustomerCommand(String cifNumber, String nationalId, String fullName, String phoneNumber, String email) {
        super(cifNumber, nationalId, fullName, phoneNumber, email);
    }

    public CreateCustomerCommand(String cifNumber) {
        super(cifNumber);
    }

    public String cifNumber() {
        return getCifNumber();
    }

    public String nationalId() {
        return getNationalId();
    }

    public String fullName() {
        return getFullName();
    }

    public String phoneNumber() {
        return getPhoneNumber();
    }

    public String email() {
        return getEmail();
    }
}
