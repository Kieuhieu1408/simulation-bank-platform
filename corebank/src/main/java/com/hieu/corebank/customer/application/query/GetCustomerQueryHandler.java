package com.hieu.corebank.customer.application.query;

import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.customer.api.dto.response.CustomerResponseDTO;
import com.hieu.corebank.customer.domain.Customer;
import com.hieu.corebank.customer.domain.CustomerRepository;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile({ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class GetCustomerQueryHandler implements QueryHandler<GetCustomerQuery, CustomerResponseDTO> {

    private final CustomerRepository customers;

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO handle(GetCustomerQuery query) {
        Customer customer = customers.findById(query.cifNumber().toUpperCase())
                .orElseThrow(() -> new NotFoundException("CIF not found: " + query.cifNumber()));
        return CustomerResponseDTO.from(customer);
    }
}
