package com.hieu.moneybank.handler.command.customer;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.moneybank.domain.Customer;
import com.hieu.moneybank.dto.request.CustomerCreateRequestDTO;
import com.hieu.moneybank.dto.response.CustomerResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.repository.CustomerRepository;
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
