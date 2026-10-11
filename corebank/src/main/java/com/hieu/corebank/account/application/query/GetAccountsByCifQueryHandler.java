package com.hieu.corebank.account.application.query;

import com.hieu.common.cqrs.Dispatcher;
import com.hieu.common.cqrs.QueryHandler;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.infrastructure.projection.AccountView;
import com.hieu.corebank.account.infrastructure.projection.AccountViewRepository;
import com.hieu.corebank.customer.application.query.GetCustomerQuery;
import com.hieu.corebank.shared.constant.ProfileConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Profile({ProfileConstants.QUERY, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class GetAccountsByCifQueryHandler implements QueryHandler<GetAccountsByCifQuery, List<AccountResponseDTO>> {

    private final AccountViewRepository accounts;
    private final Dispatcher            dispatcher;

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> handle(GetAccountsByCifQuery query) {
        // Ensure customer exists
        dispatcher.dispatch(new GetCustomerQuery(query.cifNumber()));
        List<AccountView> accountList = accounts.findByCustomerIdOrderByLastUpdatedAtDesc(query.cifNumber().toUpperCase());
        return accountList.stream().map(AccountResponseDTO::from).collect(Collectors.toList());
    }
}
