package com.hieu.corebank.handler.command.customer;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.domain.Customer;
import com.hieu.corebank.dto.request.CustomerCreateRequestDTO;
import com.hieu.corebank.dto.response.CustomerResponseDTO;
import com.hieu.corebank.exception.BusinessException;
import com.hieu.corebank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateCustomerCommandHandler implements CommandHandler<CustomerCreateRequestDTO, CustomerResponseDTO> {

    private final CustomerRepository customers;

    @Override
    @Transactional
    public CustomerResponseDTO handle(CustomerCreateRequestDTO command) {
        String cifNumber = command.getCifNumber().toUpperCase();
        if (customers.existsByCifNumber(cifNumber)) {
            throw new BusinessException("CIF already exists: " + cifNumber);
        }
        Customer customer = customers.save(new Customer(cifNumber));
        return CustomerResponseDTO.from(customer);
    }
}
