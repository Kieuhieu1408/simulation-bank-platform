package com.hieu.corebank.api.impl;

import com.hieu.corebank.api.BaseController;
import com.hieu.corebank.api.CustomerController;
import com.hieu.corebank.dto.CustomerCreateRequestDTO;
import com.hieu.corebank.dto.AccountResponseDTO;
import com.hieu.corebank.dto.IssueCardResponseDTO;
import com.hieu.corebank.dto.CustomerResponseDTO;
import com.hieu.corebank.handler.query.account.GetAccountsByCifQueryHandler.GetAccountsByCifQuery;
import com.hieu.corebank.handler.query.card.GetCardsByCifQueryHandler.GetCardsByCifQuery;
import com.hieu.corebank.handler.query.customer.GetCustomerQueryHandler.GetCustomerQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerControllerImpl extends BaseController implements CustomerController {

    @Override
    public ResponseEntity<CustomerResponseDTO> create(CustomerCreateRequestDTO request) {
        return execute(request, CustomerResponseDTO.class);
    }

    @Override
    public ResponseEntity<CustomerResponseDTO> get(String cifNumber) {
        return executeQuery(new GetCustomerQuery(cifNumber), CustomerResponseDTO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<List<AccountResponseDTO>> accounts(String cifNumber) {
        return executeQuery(new GetAccountsByCifQuery(cifNumber), (Class<List<AccountResponseDTO>>) (Class<?>) List.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<List<IssueCardResponseDTO>> cards(String cifNumber) {
        // Validate customer exists implicitly if needed, or rely on the query handler.
        return executeQuery(new GetCardsByCifQuery(cifNumber), (Class<List<IssueCardResponseDTO>>) (Class<?>) List.class);
    }
}