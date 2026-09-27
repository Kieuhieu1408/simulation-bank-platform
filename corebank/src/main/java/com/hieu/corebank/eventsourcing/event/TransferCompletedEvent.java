package com.hieu.corebank.eventsourcing.event;

import java.math.BigDecimal;

/**
 * Chuyển khoản hoàn tất — bên nhận đã xác nhận.
 * actual_balance -= amount  (available_balance không đổi, đã trừ ở FundsReserved).
 */
public final class TransferCompletedEvent extends DomainEvent {

    private final BigDecimal amount;
    private final String     currency;
    private final String     transferId;
    private final String     destinationAccountId;

    public TransferCompletedEvent(String aggregateId, long version,
                                  BigDecimal amount, String currency,
                                  String transferId, String destinationAccountId) {
        super(aggregateId, version);
        this.amount               = amount;
        this.currency             = currency;
        this.transferId           = transferId;
        this.destinationAccountId = destinationAccountId;
    }

    @Override public String eventType()     { return "TransferCompleted"; }
    @Override public String aggregateType() { return "Account"; }

    public BigDecimal amount()               { return amount; }
    public String     currency()             { return currency; }
    public String     transferId()           { return transferId; }
    public String     destinationAccountId() { return destinationAccountId; }
}