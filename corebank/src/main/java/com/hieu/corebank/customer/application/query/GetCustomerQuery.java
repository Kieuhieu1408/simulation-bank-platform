package com.hieu.corebank.customer.application.query;

import com.hieu.common.cqrs.Query;
import com.hieu.corebank.customer.api.dto.response.CustomerResponseDTO;

public record GetCustomerQuery(String cifNumber) implements Query<CustomerResponseDTO> {
}
