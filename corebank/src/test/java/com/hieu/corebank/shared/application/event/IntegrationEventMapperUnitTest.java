package com.hieu.corebank.shared.application.event;

import com.hieu.corebank.account.domain.event.AccountCreatedEvent;
import com.hieu.corebank.account.domain.event.FundsReservedEvent;
import com.hieu.corebank.shared.domain.DomainEvent;
import com.hieu.corebank.transfer.domain.event.TransferCompletedEvent;
import com.hieu.corebank.transfer.domain.event.TransferDestinationCreditedEvent;
import com.hieu.corebank.transfer.domain.event.TransferFailedEvent;
import com.hieu.corebank.transfer.domain.event.TransferInitiatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationEventMapperUnitTest {

    private final IntegrationEventMapper mapper = new DefaultIntegrationEventMapper();

    @Test
    @DisplayName("Phase 6: AccountCreatedEvent ánh xạ thành AccountOpenedEventDTO với schemaVersion 1.0")
    void testAccountCreatedMapping() {
        AccountCreatedEvent event = new AccountCreatedEvent(
                "acc-123", 1L, "101000000001", "CIF00000001", "VND", new BigDecimal("1000000.00")
        );

        Optional<IntegrationEvent> result = mapper.toIntegrationEvent(event);

        assertThat(result).isPresent();
        IntegrationEvent ie = result.get();
        assertThat(ie).isInstanceOf(AccountOpenedEventDTO.class);
        AccountOpenedEventDTO dto = (AccountOpenedEventDTO) ie;

        assertThat(dto.getEventType()).isEqualTo("AccountOpened");
        assertThat(dto.getSchemaVersion()).isEqualTo("1.0");
        assertThat(dto.getAggregateType()).isEqualTo("Account");
        assertThat(dto.getAccountId()).isEqualTo("acc-123");
        assertThat(dto.getAccountNumber()).isEqualTo("101000000001");
        assertThat(dto.getCifNumber()).isEqualTo("CIF00000001");
        assertThat(dto.getCurrency()).isEqualTo("VND");
        assertThat(dto.getInitialBalance()).isEqualByComparingTo(new BigDecimal("1000000.00"));
    }

    @Test
    @DisplayName("Phase 6: TransferCompletedEvent ánh xạ thành TransferCompletedEventDTO với schemaVersion 1.0")
    void testTransferCompletedMapping() {
        TransferCompletedEvent event = new TransferCompletedEvent(
                "tx-999", 4L, "src-acc", "dst-acc", new BigDecimal("200000.00"), "VND"
        );

        Optional<IntegrationEvent> result = mapper.toIntegrationEvent(event);

        assertThat(result).isPresent();
        IntegrationEvent ie = result.get();
        assertThat(ie).isInstanceOf(TransferCompletedEventDTO.class);
        TransferCompletedEventDTO dto = (TransferCompletedEventDTO) ie;

        assertThat(dto.getEventType()).isEqualTo("TransferCompleted");
        assertThat(dto.getSchemaVersion()).isEqualTo("1.0");
        assertThat(dto.getAggregateType()).isEqualTo("Transfer");
        assertThat(dto.getTransferId()).isEqualTo("tx-999");
        assertThat(dto.getSourceAccountId()).isEqualTo("src-acc");
        assertThat(dto.getDestinationAccountId()).isEqualTo("dst-acc");
        assertThat(dto.getAmount()).isEqualByComparingTo(new BigDecimal("200000.00"));
        assertThat(dto.getCurrency()).isEqualTo("VND");
        assertThat(dto.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("Phase 6: TransferFailedEvent ánh xạ thành TransferFailedEventDTO với schemaVersion 1.0")
    void testTransferFailedMapping() {
        TransferFailedEvent event = new TransferFailedEvent(
                "tx-888", 2L, "src-acc", "dst-acc", new BigDecimal("500000.00"), "VND", "Insufficient funds"
        );

        Optional<IntegrationEvent> result = mapper.toIntegrationEvent(event);

        assertThat(result).isPresent();
        IntegrationEvent ie = result.get();
        assertThat(ie).isInstanceOf(TransferFailedEventDTO.class);
        TransferFailedEventDTO dto = (TransferFailedEventDTO) ie;

        assertThat(dto.getEventType()).isEqualTo("TransferFailed");
        assertThat(dto.getSchemaVersion()).isEqualTo("1.0");
        assertThat(dto.getAggregateType()).isEqualTo("Transfer");
        assertThat(dto.getTransferId()).isEqualTo("tx-888");
        assertThat(dto.getReason()).isEqualTo("Insufficient funds");
        assertThat(dto.getStatus()).isEqualTo("FAILED");
    }

    @Test
    @DisplayName("Phase 6: Các intermediate domain event không được ánh xạ ra ngoài (tránh rò rỉ payload)")
    void testIntermediateEventsNotExposed() {
        DomainEvent initiated = new TransferInitiatedEvent(
                "tx-1", 1L, "src", "dst", BigDecimal.TEN, "VND", "desc", "hash123"
        );
        DomainEvent reserved = new FundsReservedEvent("acc-1", 2L, BigDecimal.TEN, "tx-1", "dst");
        DomainEvent credited = new TransferDestinationCreditedEvent("tx-1", 3L);

        assertThat(mapper.toIntegrationEvent(initiated)).isEmpty();
        assertThat(mapper.toIntegrationEvent(reserved)).isEmpty();
        assertThat(mapper.toIntegrationEvent(credited)).isEmpty();
    }
}
