package com.hieu.moneybank.handler.query.customer;

import com.hieu.common.cqrs.Query;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.response.CustomerResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetCustomerQueryHandler implements QueryHandler<GetCustomerQueryHandler.GetCustomerQuery, CustomerResponseDTO> {

    public record GetCustomerQuery(String cifNumber) implements Query<CustomerResponseDTO> {
    }

    private final CorebankClient corebankClient;

    @Override
    public CustomerResponseDTO handle(GetCustomerQuery query) {
        return corebankClient.getCustomer(query.cifNumber().toUpperCase());
    }
}
