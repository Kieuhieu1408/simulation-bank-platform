package com.hieu.corebank.eventsourcing.event;

import java.math.BigDecimal;

/**
 * Tiền đã được giữ lại để chuẩn bị chuyển khoản.
 * available_balance -= amount  (actual_balance giữ nguyên).
 */
public final class FundsReservedEvent extends DomainEvent {

    private final BigDecimal amount;
    private final String     currency;
    private final String     transferId;

    public FundsReservedEvent(String aggregateId, long version,
                              BigDecimal amount, String currency, String transferId) {
        super(aggregateId, version);
        this.amount     = amount;
        this.currency   = currency;
        this.transferId = transferId;
    }

    @Override public String eventType()     { return "FundsReserved"; }
    @Override public String aggregateType() { return "Account"; }

    public BigDecimal amount()     { return amount; }
    public String     currency()   { return currency; }
    public String     transferId() { return transferId; }
}