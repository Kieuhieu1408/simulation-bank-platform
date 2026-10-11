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
public class ReserveFundsCommandHandler implements CommandHandler<ReserveFundsCommand, Void> {

    private final EventStore eventStore;

    @Override
    @Transactional
    public Void handle(ReserveFundsCommand command) {
        AccountAggregate account = eventStore.load(command.accountId().value());
        account.reserve(command.transferId(), command.amount());
        eventStore.append(account);
        log.info("Reserved funds on accountId={}, transferId={}, amount={}",
                command.accountId().value(), command.transferId().value(), command.amount());
        return null;
    }
}
