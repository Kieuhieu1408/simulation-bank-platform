package com.hieu.corebank.account.application.query;

import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.account.api.dto.request.AccountsByCifRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.application.port.CustomerDirectory;
import com.hieu.corebank.account.infrastructure.projection.AccountView;
import com.hieu.corebank.account.infrastructure.projection.AccountViewRepository;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * GetAccountsByCifQueryHandler — Truy vấn danh sách tài khoản theo CIF khách hàng.
 */
@Profile({ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class GetAccountsByCifQueryHandler implements QueryHandler<AccountsByCifRequestDTO, List<AccountResponseDTO>> {

    private final AccountViewRepository accounts;
    private final CustomerDirectory customerDirectory;

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> handle(AccountsByCifRequestDTO query) {
        String cif = query.getCifNumber().toUpperCase();
        if (!customerDirectory.existsByCif(cif)) {
            throw new NotFoundException("Customer not found: " + cif);
        }
        List<AccountView> accountList = accounts.findByCustomerIdOrderByLastUpdatedAtDesc(cif);
        return accountList.stream().map(AccountResponseDTO::from).collect(Collectors.toList());
    }
}
