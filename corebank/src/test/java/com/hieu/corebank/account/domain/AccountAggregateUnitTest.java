package com.hieu.corebank.account.domain;

import com.hieu.common.constant.AccountStatus;
import com.hieu.corebank.account.domain.event.AccountCreatedEvent;
import com.hieu.corebank.account.domain.event.FundsReservedEvent;
import com.hieu.corebank.shared.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AccountAggregate Pure Unit Tests (Level 3 Invariants & Idempotency)")
public class AccountAggregateUnitTest {

    @Test
    @DisplayName("Create account successfully initializes balance and status")
    void createAccount_Success() {
        AccountId accountId = AccountId.of("ACC-001");
        AccountNumber accountNumber = AccountNumber.of("100000000001");
        CustomerId customerId = CustomerId.of("CIF00000001");
        Currency currency = Currency.VND;
        Money initial = Money.of(1000000, currency);

        AccountAggregate agg = AccountAggregate.create(accountId, accountNumber, customerId, currency, initial);

        assertThat(agg.id()).isEqualTo(accountId);
        assertThat(agg.accountId()).isEqualTo("ACC-001");
        assertThat(agg.accountNumber()).isEqualTo("100000000001");
        assertThat(agg.customerId()).isEqualTo("CIF00000001");
        assertThat(agg.currency()).isEqualTo("VND");
        assertThat(agg.actualBalance()).isEqualByComparingTo("1000000");
        assertThat(agg.availableBalance()).isEqualByComparingTo("1000000");
        assertThat(agg.status()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(agg.uncommittedEvents()).hasSize(1);
        assertThat(agg.uncommittedEvents().get(0)).isInstanceOf(AccountCreatedEvent.class);
    }

    @Test
    @DisplayName("reserve() thành công: giảm available balance, tạo Hold ở trạng thái HELD")
    void reserve_Success() {
        AccountAggregate agg = AccountAggregate.create("ACC-001", "100000000001", "CIF00000001", "VND", new BigDecimal("1000000"));
        agg.markCommitted();

        TransferId txId = TransferId.of("TX-100");
        Money amount = Money.of(300000, Currency.VND);

        agg.reserve(txId, amount);

        assertThat(agg.availableBalance()).isEqualByComparingTo("700000");
        assertThat(agg.actualBalance()).isEqualByComparingTo("1000000"); // Actual chưa đổi
        assertThat(agg.holds()).containsKey("TX-100");

        Hold hold = agg.holds().get("TX-100");
        assertThat(hold.status()).isEqualTo(HoldStatus.HELD);
        assertThat(hold.amount()).isEqualTo(amount);
        assertThat(agg.uncommittedEvents()).hasSize(1);
    }

    @Test
    @DisplayName("reserve() thất bại khi không đủ số dư: ném BusinessException (Level 3 Invariant)")
    void reserve_InsufficientBalance_ThrowsException() {
        AccountAggregate agg = AccountAggregate.create("ACC-001", "100000000001", "CIF00000001", "VND", new BigDecimal("200000"));
        agg.markCommitted();

        TransferId txId = TransferId.of("TX-101");
        Money amount = Money.of(300000, Currency.VND);

        assertThatThrownBy(() -> agg.reserve(txId, amount))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient available balance");

        assertThat(agg.uncommittedEvents()).isEmpty();
    }

    @Test
    @DisplayName("reserve() idempotency: gọi lần 2 cùng transferId thì no-op, không trừ thêm tiền")
    void reserve_Idempotent_NoOp() {
        AccountAggregate agg = AccountAggregate.create("ACC-001", "100000000001", "CIF00000001", "VND", new BigDecimal("1000000"));
        agg.markCommitted();

        TransferId txId = TransferId.of("TX-102");
        Money amount = Money.of(300000, Currency.VND);

        agg.reserve(txId, amount);
        assertThat(agg.availableBalance()).isEqualByComparingTo("700000");
        int eventCountAfterFirst = agg.uncommittedEvents().size();

        // Gọi lại lần 2 cùng transferId
        agg.reserve(txId, amount);

        // Số dư và số lượng event giữ nguyên
        assertThat(agg.availableBalance()).isEqualByComparingTo("700000");
        assertThat(agg.uncommittedEvents()).hasSize(eventCountAfterFirst);
    }

    @Test
    @DisplayName("credit() thành công và idempotent: cộng cả 2 balance, gọi lại no-op")
    void credit_Success_And_Idempotent() {
        AccountAggregate agg = AccountAggregate.create("ACC-002", "100000000002", "CIF00000002", "VND", new BigDecimal("500000"));
        agg.markCommitted();

        TransferId txId = TransferId.of("TX-200");
        Money amount = Money.of(200000, Currency.VND);

        agg.credit(txId, amount);
        assertThat(agg.actualBalance()).isEqualByComparingTo("700000");
        assertThat(agg.availableBalance()).isEqualByComparingTo("700000");
        assertThat(agg.creditedTransfers()).contains("TX-200");
        assertThat(agg.uncommittedEvents()).hasSize(1);

        // Gọi lại cùng transferId -> idempotent no-op
        agg.credit(txId, amount);
        assertThat(agg.actualBalance()).isEqualByComparingTo("700000");
        assertThat(agg.availableBalance()).isEqualByComparingTo("700000");
        assertThat(agg.uncommittedEvents()).hasSize(1);
    }

    @Test
    @DisplayName("settle() thành công và idempotent: giảm actual balance, chuyển hold sang SETTLED")
    void settle_Success_And_Idempotent() {
        AccountAggregate agg = AccountAggregate.create("ACC-001", "100000000001", "CIF00000001", "VND", new BigDecimal("1000000"));
        TransferId txId = TransferId.of("TX-300");
        Money amount = Money.of(400000, Currency.VND);
        agg.reserve(txId, amount);
        agg.markCommitted();

        assertThat(agg.actualBalance()).isEqualByComparingTo("1000000");
        assertThat(agg.availableBalance()).isEqualByComparingTo("600000");

        // Settle lần 1
        agg.settle(txId);
        assertThat(agg.actualBalance()).isEqualByComparingTo("600000");
        assertThat(agg.availableBalance()).isEqualByComparingTo("600000");
        assertThat(agg.holds().get("TX-300").status()).isEqualTo(HoldStatus.SETTLED);
        assertThat(agg.uncommittedEvents()).hasSize(1);

        // Settle lần 2 -> idempotent no-op
        agg.settle(txId);
        assertThat(agg.actualBalance()).isEqualByComparingTo("600000");
        assertThat(agg.uncommittedEvents()).hasSize(1);
    }

    @Test
    @DisplayName("release() thành công và idempotent: hoàn lại available balance, chuyển hold sang RELEASED")
    void release_Success_And_Idempotent() {
        AccountAggregate agg = AccountAggregate.create("ACC-001", "100000000001", "CIF00000001", "VND", new BigDecimal("1000000"));
        TransferId txId = TransferId.of("TX-400");
        Money amount = Money.of(400000, Currency.VND);
        agg.reserve(txId, amount);
        agg.markCommitted();

        assertThat(agg.availableBalance()).isEqualByComparingTo("600000");

        // Release lần 1
        agg.release(txId, "Destination account inactive");
        assertThat(agg.availableBalance()).isEqualByComparingTo("1000000"); // Được hoàn lại
        assertThat(agg.actualBalance()).isEqualByComparingTo("1000000");
        assertThat(agg.holds().get("TX-400").status()).isEqualTo(HoldStatus.RELEASED);
        assertThat(agg.uncommittedEvents()).hasSize(1);

        // Release lần 2 -> idempotent no-op
        agg.release(txId, "Duplicate retry");
        assertThat(agg.availableBalance()).isEqualByComparingTo("1000000");
        assertThat(agg.uncommittedEvents()).hasSize(1);
    }

    @Test
    @DisplayName("settle() hoặc release() hold không tồn tại: ném BusinessException")
    void settleAndRelease_NonExistentHold_ThrowsException() {
        AccountAggregate agg = AccountAggregate.create("ACC-001", "100000000001", "CIF00000001", "VND", new BigDecimal("1000000"));

        assertThatThrownBy(() -> agg.settle(TransferId.of("NOT_EXIST")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Hold not found");

        assertThatThrownBy(() -> agg.release(TransferId.of("NOT_EXIST"), "Reason"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Hold not found");
    }

    @Test
    @DisplayName("reconstitute() nạp từ lịch sử event tái tạo đúng số dư và holds")
    void reconstitute_ReplaysHistoryCorrectly() {
        AccountAggregate agg = AccountAggregate.create("ACC-001", "100000000001", "CIF00000001", "VND", new BigDecimal("1000000"));
        TransferId tx1 = TransferId.of("TX-501");
        agg.reserve(tx1, Money.of(200000, Currency.VND));
        agg.settle(tx1);

        TransferId tx2 = TransferId.of("TX-502");
        agg.reserve(tx2, Money.of(300000, Currency.VND));

        List<DomainEvent> history = agg.uncommittedEvents();

        // Reconstitute lại từ event history
        AccountAggregate replayed = AccountAggregate.reconstitute(history);

        assertThat(replayed.accountId()).isEqualTo("ACC-001");
        assertThat(replayed.actualBalance()).isEqualByComparingTo("800000");
        assertThat(replayed.availableBalance()).isEqualByComparingTo("500000");
        assertThat(replayed.holds()).hasSize(2);
        assertThat(replayed.holds().get("TX-501").status()).isEqualTo(HoldStatus.SETTLED);
        assertThat(replayed.holds().get("TX-502").status()).isEqualTo(HoldStatus.HELD);
        assertThat(replayed.version()).isEqualTo(agg.version());
    }

    @Test
    @DisplayName("Currency mismatch ném BusinessException")
    void currencyMismatch_ThrowsException() {
        AccountAggregate agg = AccountAggregate.create("ACC-001", "100000000001", "CIF00000001", "VND", new BigDecimal("1000000"));

        assertThatThrownBy(() -> agg.reserve(TransferId.of("TX-ERR"), Money.of(100, Currency.USD)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Currency mismatch");
    }

    @Test
    @DisplayName("reconstitute() từ AccountSnapshotRestoredEvent bảo toàn 100% holds, available balance và status")
    void reconstitute_FromSnapshotRestoredEvent_PreservesFullState() {
        AccountAggregate agg = AccountAggregate.create("ACC-001", "100000000001", "CIF00000001", "VND", new BigDecimal("1000000"));
        TransferId tx1 = TransferId.of("TX-SNAP-1");
        agg.reserve(tx1, Money.of(300000, Currency.VND));

        // Tạo snapshot event với 1 hold đang HELD và availableBalance = 700000
        var holdSnap = new com.hieu.corebank.account.domain.event.AccountSnapshotRestoredEvent.HoldSnapshot(
                "TX-SNAP-1",
                new BigDecimal("300000"),
                HoldStatus.HELD.name(),
                java.time.Instant.now()
        );
        var snapshotEvent = new com.hieu.corebank.account.domain.event.AccountSnapshotRestoredEvent(
                "ACC-001", 50L,
                "100000000001", "CIF00000001",
                "VND", new BigDecimal("1000000"),
                new BigDecimal("700000"),
                AccountStatus.ACTIVE.name(),
                List.of(holdSnap),
                java.util.Set.of("TX-CREDITED-1")
        );

        AccountAggregate restored = AccountAggregate.reconstitute(List.of(snapshotEvent));

        assertThat(restored.actualBalance()).isEqualByComparingTo("1000000");
        assertThat(restored.availableBalance()).isEqualByComparingTo("700000");
        assertThat(restored.holds()).containsKey("TX-SNAP-1");
        assertThat(restored.holds().get("TX-SNAP-1").status()).isEqualTo(HoldStatus.HELD);
        assertThat(restored.creditedTransfers()).contains("TX-CREDITED-1");

        // Sau khi restore từ snapshot, settle hold cũ phải thành công!
        restored.settle(tx1);
        assertThat(restored.actualBalance()).isEqualByComparingTo("700000");
        assertThat(restored.availableBalance()).isEqualByComparingTo("700000");
        assertThat(restored.holds().get("TX-SNAP-1").status()).isEqualTo(HoldStatus.SETTLED);
    }
}
