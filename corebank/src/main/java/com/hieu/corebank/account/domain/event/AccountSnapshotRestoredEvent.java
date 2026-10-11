package com.hieu.corebank.account.domain.event;

import com.hieu.corebank.shared.domain.DomainEvent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * AccountSnapshotRestoredEvent — Event tổng hợp tái tạo trạng thái đầy đủ của AccountAggregate từ Snapshot.
 * Giữ nguyên vẹn số dư khả dụng, các khoản phong tỏa (holds), lịch sử credited transfers và trạng thái tài khoản.
 */
public final class AccountSnapshotRestoredEvent extends DomainEvent {

    private final String             accountNumber;
    private final String             customerId;
    private final String             currency;
    private final BigDecimal         actualBalance;
    private final BigDecimal         availableBalance;
    private final String             status;
    private final List<HoldSnapshot> holds;
    private final Set<String>        creditedTransfers;

    public AccountSnapshotRestoredEvent(String aggregateId, long version,
                                        String accountNumber, String customerId,
                                        String currency, BigDecimal actualBalance,
                                        BigDecimal availableBalance, String status,
                                        List<HoldSnapshot> holds, Set<String> creditedTransfers) {
        super(aggregateId, version);
        this.accountNumber     = accountNumber;
        this.customerId        = customerId;
        this.currency          = currency;
        this.actualBalance     = actualBalance;
        this.availableBalance  = availableBalance;
        this.status            = status;
        this.holds             = holds == null ? List.of() : List.copyOf(holds);
        this.creditedTransfers = creditedTransfers == null ? Set.of() : Set.copyOf(creditedTransfers);
    }

    @Override public String eventType()     { return "AccountSnapshotRestored"; }
    @Override public String aggregateType() { return "Account"; }

    public String             accountNumber()     { return accountNumber; }
    public String             customerId()        { return customerId; }
    public String             currency()          { return currency; }
    public BigDecimal         actualBalance()     { return actualBalance; }
    public BigDecimal         availableBalance()  { return availableBalance; }
    public String             status()            { return status; }
    public List<HoldSnapshot> holds()             { return holds; }
    public Set<String>        creditedTransfers() { return creditedTransfers; }

    public record HoldSnapshot(
            String transferId,
            BigDecimal amount,
            String status,
            Instant createdAt
    ) implements Serializable {}
}
