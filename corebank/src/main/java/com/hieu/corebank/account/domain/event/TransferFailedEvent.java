package com.hieu.corebank.account.domain.event;

import com.hieu.corebank.shared.domain.DomainEvent;

import java.math.BigDecimal;

/**
 * Giữ tiền thất bại hoặc bị hủy — hoàn lại available_balance.
 */
public final class TransferFailedEvent extends DomainEvent {

    private final BigDecimal amount;
    private final String     currency;
    private final String     transferId;
    private final String     reason;

    public TransferFailedEvent(String aggregateId, long version,
                               BigDecimal amount, String currency,
                               String transferId, String reason) {
        super(aggregateId, version);
        this.amount     = amount;
        this.currency   = currency;
        this.transferId = transferId;
        this.reason     = reason;
    }

    @Override public String eventType()     { return "TransferFailed"; }
    @Override public String aggregateType() { return "Account"; }

    public BigDecimal amount()     { return amount; }
    public String     currency()   { return currency; }
    public String     transferId() { return transferId; }
    public String     reason()     { return reason; }
}
