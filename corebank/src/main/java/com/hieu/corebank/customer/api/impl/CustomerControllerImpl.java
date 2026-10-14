package com.hieu.corebank.customer.api.impl;

import com.hieu.common.constant.ActionType;
import com.hieu.corebank.access.security.CoreBankAuthorization;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.api.dto.request.AccountsByCifRequestDTO;
import com.hieu.corebank.card.api.dto.request.CardsByCifRequestDTO;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;
import com.hieu.corebank.customer.api.CustomerController;
import com.hieu.corebank.customer.api.dto.request.CustomerCreateRequestDTO;
import com.hieu.corebank.customer.api.dto.request.CustomerGetRequestDTO;
import com.hieu.corebank.customer.api.dto.response.CustomerResponseDTO;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.infrastructure.web.BaseController;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Profile({ProfileConstants.COMMAND, ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@RestController
public class CustomerControllerImpl extends BaseController implements CustomerController {

    @Override
    @CoreBankAuthorization(menuCode = "customer_management", action = ActionType.CREATE)
    public ResponseEntity<CustomerResponseDTO> create(CustomerCreateRequestDTO request) {
        return execute(request, CustomerResponseDTO.class);
    }

    @Override
    @CoreBankAuthorization(menuCode = "customer_management", action = ActionType.READ)
    public ResponseEntity<CustomerResponseDTO> get(String cifNumber) {
        return executeQuery(new CustomerGetRequestDTO(cifNumber), CustomerResponseDTO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    @CoreBankAuthorization(menuCode = "account_management", action = ActionType.READ)
    public ResponseEntity<List<AccountResponseDTO>> accounts(String cifNumber) {
        return executeQuery(new AccountsByCifRequestDTO(cifNumber), (Class<List<AccountResponseDTO>>) (Class<?>) List.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    @CoreBankAuthorization(menuCode = "card_management", action = ActionType.READ)
    public ResponseEntity<List<IssueCardResponseDTO>> cards(String cifNumber) {
        return executeQuery(new CardsByCifRequestDTO(cifNumber), (Class<List<IssueCardResponseDTO>>) (Class<?>) List.class);
    }
}
