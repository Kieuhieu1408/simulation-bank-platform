package com.hieu.corebank.transfer.domain;

import com.hieu.corebank.shared.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Transfer Aggregate Pure Unit Tests (Level 3 Invariants & State Machine)")
class TransferAggregateUnitTest {

    private final TransferId transferId = TransferId.of("TX-12345");
    private final AccountId srcAcc = AccountId.of("ACC-SOURCE-001");
    private final AccountId dstAcc = AccountId.of("ACC-DEST-002");
    private final Money amount = Money.of(new BigDecimal("100000"), Currency.of("VND"));
    private final String requestHash = "abc123hash";

    @Test
    @DisplayName("initiate() thành công tạo aggregate ở trạng thái INITIATED")
    void initiateSuccess() {
        Transfer transfer = Transfer.initiate(transferId, srcAcc, dstAcc, amount, "Test transfer", requestHash);

        assertThat(transfer.id()).isEqualTo(transferId);
        assertThat(transfer.sourceAccountId()).isEqualTo(srcAcc);
        assertThat(transfer.destinationAccountId()).isEqualTo(dstAcc);
        assertThat(transfer.amount()).isEqualTo(amount);
        assertThat(transfer.status()).isEqualTo(TransferStatus.INITIATED);
        assertThat(transfer.version()).isEqualTo(1L);
        assertThat(transfer.uncommittedEvents()).hasSize(1);
    }

    @Test
    @DisplayName("initiate() tài khoản nguồn trùng tài khoản đích: ném DomainException")
    void initiateSameSourceAndDestinationThrows() {
        assertThatThrownBy(() -> Transfer.initiate(transferId, srcAcc, srcAcc, amount, "Test", requestHash))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("different");
    }

    @Test
    @DisplayName("initiate() số tiền không dương: ném DomainException")
    void initiateNonPositiveAmountThrows() {
        Money zeroAmount = Money.zero(Currency.of("VND"));
        assertThatThrownBy(() -> Transfer.initiate(transferId, srcAcc, dstAcc, zeroAmount, "Test", requestHash))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("positive");
    }

    @Test
    @DisplayName("initiate() thiếu thuộc tính bắt buộc: ném DomainException")
    void initiateNullAttributesThrows() {
        assertThatThrownBy(() -> Transfer.initiate(null, srcAcc, dstAcc, amount, "Test", requestHash))
                .isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> Transfer.initiate(transferId, null, dstAcc, amount, "Test", requestHash))
                .isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> Transfer.initiate(transferId, srcAcc, null, amount, "Test", requestHash))
                .isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> Transfer.initiate(transferId, srcAcc, dstAcc, null, "Test", requestHash))
                .isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> Transfer.initiate(transferId, srcAcc, dstAcc, amount, "Test", null))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("State Machine chuyển trạng thái chuẩn tuần tự: INITIATED -> SOURCE_RESERVED -> DESTINATION_CREDITED -> COMPLETED")
    void validSequentialStateTransitions() {
        Transfer transfer = Transfer.initiate(transferId, srcAcc, dstAcc, amount, "Test", requestHash);
        assertThat(transfer.status()).isEqualTo(TransferStatus.INITIATED);

        transfer.markSourceReserved();
        assertThat(transfer.status()).isEqualTo(TransferStatus.SOURCE_RESERVED);
        assertThat(transfer.version()).isEqualTo(2L);

        transfer.markDestinationCredited();
        assertThat(transfer.status()).isEqualTo(TransferStatus.DESTINATION_CREDITED);
        assertThat(transfer.version()).isEqualTo(3L);

        transfer.complete();
        assertThat(transfer.status()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(transfer.version()).isEqualTo(4L);
    }

    @Test
    @DisplayName("Các bước chuyển trạng thái mang tính Idempotent (gọi lại cùng bước là no-op)")
    void idempotentStateTransitions() {
        Transfer transfer = Transfer.initiate(transferId, srcAcc, dstAcc, amount, "Test", requestHash);

        // Gọi reserve 2 lần
        transfer.markSourceReserved();
        int eventCount = transfer.uncommittedEvents().size();
        transfer.markSourceReserved(); // no-op
        assertThat(transfer.uncommittedEvents()).hasSize(eventCount);

        // Gọi credit 2 lần
        transfer.markDestinationCredited();
        eventCount = transfer.uncommittedEvents().size();
        transfer.markDestinationCredited(); // no-op
        assertThat(transfer.uncommittedEvents()).hasSize(eventCount);

        // Gọi complete 2 lần
        transfer.complete();
        eventCount = transfer.uncommittedEvents().size();
        transfer.complete(); // no-op
        assertThat(transfer.uncommittedEvents()).hasSize(eventCount);
    }

    @Test
    @DisplayName("Nhảy cóc bước trạng thái không hợp lệ: ném DomainException")
    void invalidStepJumpThrows() {
        Transfer transfer = Transfer.initiate(transferId, srcAcc, dstAcc, amount, "Test", requestHash);

        // Từ INITIATED nhảy thẳng sang complete
        assertThatThrownBy(transfer::complete)
                .isInstanceOf(DomainException.class);

        // Từ INITIATED nhảy sang markDestinationCredited
        assertThatThrownBy(transfer::markDestinationCredited)
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("fail() đánh dấu trạng thái FAILED; gọi lại mang tính Idempotent")
    void failTransfer() {
        Transfer transfer = Transfer.initiate(transferId, srcAcc, dstAcc, amount, "Test", requestHash);
        transfer.fail("Account insufficient balance");

        assertThat(transfer.status()).isEqualTo(TransferStatus.FAILED);
        assertThat(transfer.failureReason()).isEqualTo("Account insufficient balance");

        // Gọi lại fail() lần 2 là no-op
        int count = transfer.uncommittedEvents().size();
        transfer.fail("Another reason");
        assertThat(transfer.uncommittedEvents()).hasSize(count);
    }

    @Test
    @DisplayName("Không được phép fail() một transfer đã COMPLETED")
    void cannotFailCompletedTransfer() {
        Transfer transfer = Transfer.initiate(transferId, srcAcc, dstAcc, amount, "Test", requestHash);
        transfer.markSourceReserved();
        transfer.markDestinationCredited();
        transfer.complete();

        assertThatThrownBy(() -> transfer.fail("Something late"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("completed");
    }

    @Test
    @DisplayName("Không được phép fail() một transfer đã DESTINATION_CREDITED (post-pivot invariant: settlement must proceed)")
    void cannotFailDestinationCreditedTransfer() {
        Transfer transfer = Transfer.initiate(transferId, srcAcc, dstAcc, amount, "Test", requestHash);
        transfer.markSourceReserved();
        transfer.markDestinationCredited();

        assertThatThrownBy(() -> transfer.fail("Something post-pivot"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("post-pivot");
    }

    @Test
    @DisplayName("reconstitute() nạp lại chính xác trạng thái từ danh sách Event")
    void reconstituteFromHistory() {
        Transfer transfer = Transfer.initiate(transferId, srcAcc, dstAcc, amount, "Test", requestHash);
        transfer.markSourceReserved();
        transfer.markDestinationCredited();
        transfer.complete();

        List<DomainEvent> history = transfer.uncommittedEvents();
        Transfer replayed = Transfer.reconstitute(history);

        assertThat(replayed.id()).isEqualTo(transferId);
        assertThat(replayed.sourceAccountId()).isEqualTo(srcAcc);
        assertThat(replayed.destinationAccountId()).isEqualTo(dstAcc);
        assertThat(replayed.amount()).isEqualTo(amount);
        assertThat(replayed.status()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(replayed.version()).isEqualTo(4L);
        assertThat(replayed.uncommittedEvents()).isEmpty();
    }
}
