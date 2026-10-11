package com.hieu.corebank.account.application.command;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.account.domain.AccountAggregate;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.infrastructure.eventsourcing.EventStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Profile({ProfileConstants.COMMAND, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class SettleReservationCommandHandler implements CommandHandler<SettleReservationCommand, Void> {

    private final EventStore eventStore;

    @Override
    @Transactional
    public Void handle(SettleReservationCommand command) {
        AccountAggregate account = eventStore.load(command.accountId().value());
        account.settle(command.transferId());
        eventStore.append(account);
        log.info("Settled reservation on accountId={}, transferId={}",
                command.accountId().value(), command.transferId().value());
        return null;
    }
}
