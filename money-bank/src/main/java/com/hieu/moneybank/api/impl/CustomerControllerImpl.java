package com.hieu.moneybank.api.impl;

import com.hieu.moneybank.api.BaseController;
import com.hieu.moneybank.api.CustomerController;
import com.hieu.moneybank.dto.request.CustomerCreateRequestDTO;
import com.hieu.moneybank.dto.response.AccountResponseDTO;
import com.hieu.moneybank.dto.response.CardResponseDTO;
import com.hieu.moneybank.dto.response.CustomerResponseDTO;
import com.hieu.moneybank.handler.query.account.GetAccountsByCifQueryHandler.GetAccountsByCifQuery;
import com.hieu.moneybank.handler.query.card.GetCardsByCifQueryHandler.GetCardsByCifQuery;
import com.hieu.moneybank.handler.query.customer.GetCustomerQueryHandler.GetCustomerQuery;
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
    public ResponseEntity<List<CardResponseDTO>> cards(String cifNumber) {
        // Validate customer exists implicitly if needed, or rely on the query handler.
        return executeQuery(new GetCardsByCifQuery(cifNumber), (Class<List<CardResponseDTO>>) (Class<?>) List.class);
    }
}