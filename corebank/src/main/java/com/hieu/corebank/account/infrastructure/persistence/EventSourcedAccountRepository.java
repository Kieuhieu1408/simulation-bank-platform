package com.hieu.corebank.account.infrastructure.persistence;

import com.hieu.corebank.account.domain.AccountAggregate;
import com.hieu.corebank.account.domain.AccountRepository;
import com.hieu.corebank.shared.infrastructure.eventsourcing.EventStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventSourcedAccountRepository implements AccountRepository {

    private final EventStore eventStore;

    @Override
    public AccountAggregate load(String accountId) {
        return eventStore.load(accountId);
    }

    @Override
    public void save(AccountAggregate account) {
        eventStore.append(account);
    }
}
