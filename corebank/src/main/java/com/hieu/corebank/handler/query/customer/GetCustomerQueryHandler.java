package com.hieu.corebank.handler.query.customer;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.domain.Customer;
import com.hieu.corebank.dto.CustomerResponseDTO;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetCustomerQueryHandler implements QueryHandler<GetCustomerQueryHandler.GetCustomerQuery, CustomerResponseDTO> {

    public record GetCustomerQuery(String cifNumber) implements Query<CustomerResponseDTO> {
    }

    private final CustomerRepository customers;

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO handle(GetCustomerQuery query) {
        Customer customer = customers.findById(query.cifNumber().toUpperCase())
                .orElseThrow(() -> new NotFoundException("CIF not found: " + query.cifNumber()));
        return CustomerResponseDTO.from(customer);
    }
}
