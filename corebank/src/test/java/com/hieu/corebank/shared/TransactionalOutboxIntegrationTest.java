package com.hieu.corebank.shared;

import com.hieu.corebank.account.api.dto.request.AccountCreateRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.application.command.CreateAccountCommandHandler;
import com.hieu.corebank.customer.api.dto.request.CustomerCreateRequestDTO;
import com.hieu.corebank.customer.application.command.CreateCustomerCommandHandler;
import com.hieu.corebank.transfer.api.dto.request.TransferRequestDTO;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import com.hieu.corebank.transfer.application.command.TransferHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TransactionalOutboxIntegrationTest {

    @Autowired
    private CreateCustomerCommandHandler createCustomerHandler;

    @Autowired
    private CreateAccountCommandHandler createAccountHandler;

    @Autowired
    private TransferHandler transferHandler;

    @Autowired
    private JdbcClient jdbc;

    @Test
    @DisplayName("Phase 6: Tạo tài khoản ghi nhận Integration Event AccountOpened vào bảng outbox_event với schemaVersion 1.0")
    void testAccountOpenedOutboxEvent() {
        String cif = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(CustomerCreateRequestDTO.builder()
                .cifNumber(cif)
                .nationalId("012345" + String.format("%06d", Math.abs(UUID.randomUUID().hashCode() % 1_000_000L)))
                .build().toCommand());

        AccountResponseDTO account = createAccountHandler.handle(
                new AccountCreateRequestDTO(cif, "VND", new BigDecimal("5000000.00")).toCommand());

        List<Map<String, Object>> outboxRecords = jdbc.sql(
                "SELECT event_type, schema_version, payload FROM outbox_event WHERE aggregate_id = :accId"
        ).param("accId", account.getAccountId()).query().listOfRows();

        assertThat(outboxRecords).isNotEmpty();
        Map<String, Object> outbox = outboxRecords.get(0);
        assertThat(outbox.get("EVENT_TYPE")).isEqualTo("AccountOpened");
        assertThat(outbox.get("SCHEMA_VERSION")).isEqualTo("1.0");

        String payload = outbox.get("PAYLOAD").toString();
        assertThat(payload).contains("\"eventType\":\"AccountOpened\"");
        assertThat(payload).contains("\"schemaVersion\":\"1.0\"");
        assertThat(payload).contains(account.getAccountNumber());
        assertThat(payload).contains(cif);
    }

    @Test
    @DisplayName("Phase 6: Chuyển tiền hoàn tất chỉ ghi nhận TransferCompleted vào outbox_event, không rò rỉ intermediate events")
    void testTransferCompletedOutboxEvent() {
        // Tạo 2 tài khoản
        String cif1 = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(CustomerCreateRequestDTO.builder().cifNumber(cif1).build().toCommand());
        AccountResponseDTO src = createAccountHandler.handle(
                new AccountCreateRequestDTO(cif1, "VND", new BigDecimal("1000000.00")).toCommand());

        String cif2 = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(CustomerCreateRequestDTO.builder().cifNumber(cif2).build().toCommand());
        AccountResponseDTO dst = createAccountHandler.handle(
                new AccountCreateRequestDTO(cif2, "VND", BigDecimal.ZERO).toCommand());

        String key = "outbox-tx-" + UUID.randomUUID();
        TransferResponseDTO transferRes = transferHandler.handle(TransferRequestDTO.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("250000.00"))
                .currency("VND")
                .idempotencyKey(key)
                .description("Test Outbox integration event")
                .build());

        assertThat(transferRes).isNotNull();
        String txId = transferRes.getTransactionId();

        List<Map<String, Object>> outboxRecords = jdbc.sql(
                "SELECT event_type, schema_version, payload FROM outbox_event WHERE aggregate_id = :txId"
        ).param("txId", txId).query().listOfRows();

        // Chỉ có đúng 1 integration event cho transaction này là TransferCompleted
        assertThat(outboxRecords).hasSize(1);
        Map<String, Object> outbox = outboxRecords.get(0);
        assertThat(outbox.get("EVENT_TYPE")).isEqualTo("TransferCompleted");
        assertThat(outbox.get("SCHEMA_VERSION")).isEqualTo("1.0");

        String payload = outbox.get("PAYLOAD").toString();
        assertThat(payload).contains("\"eventType\":\"TransferCompleted\"");
        assertThat(payload).contains("\"schemaVersion\":\"1.0\"");
        assertThat(payload).contains("\"status\":\"COMPLETED\"");
        assertThat(payload).contains(src.getAccountId());
        assertThat(payload).contains(dst.getAccountId());

        // Kiểm tra chắc chắn các intermediate events KHÔNG bị ghi vào outbox_event
        List<String> eventTypes = outboxRecords.stream()
                .map(r -> r.get("EVENT_TYPE").toString())
                .toList();
        assertThat(eventTypes).doesNotContain("TransferInitiated", "TransferFundsReserved", "TransferDestinationCredited");
    }
}
