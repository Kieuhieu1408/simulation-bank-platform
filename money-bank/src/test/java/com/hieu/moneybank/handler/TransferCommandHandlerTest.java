package com.hieu.moneybank.handler;

import com.hieu.common.constant.TransactionStatus;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.dto.request.TransferRequestDTO;
import com.hieu.moneybank.dto.response.TransferResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.handler.command.transfer.TransferCommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferCommandHandlerTest {

    @Mock
    private CorebankClient corebankClient;

    private TransferCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TransferCommandHandler(corebankClient);
    }

    @Test
    @DisplayName("Tự động sinh server-side idempotencyKey khi request không có key")
    void shouldAutoGenerateIdempotencyKeyWhenMissing() {
        TransferRequestDTO request = TransferRequestDTO.builder()
                .sourceAccountId("ACC001")
                .destinationAccountId("ACC002")
                .amount(new BigDecimal("100000"))
                .currency("VND")
                .description("Test transfer without key")
                .idempotencyKey(null)
                .build();

        TransferResponseDTO expectedResponse = TransferResponseDTO.builder()
                .transactionId("tx-123")
                .status(TransactionStatus.SUCCESS)
                .sourceAccountId("ACC001")
                .destinationAccountId("ACC002")
                .amount(new BigDecimal("100000"))
                .currency("VND")
                .createdAt(Instant.now())
                .build();

        when(corebankClient.executeTransfer(any())).thenReturn(expectedResponse);

        TransferResponseDTO response = handler.handle(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(TransactionStatus.SUCCESS);

        ArgumentCaptor<TransferRequestDTO> captor = ArgumentCaptor.forClass(TransferRequestDTO.class);
        verify(corebankClient).executeTransfer(captor.capture());

        TransferRequestDTO sentRequest = captor.getValue();
        assertThat(sentRequest.getIdempotencyKey()).isNotBlank();
        assertThat(sentRequest.getIdempotencyKey()).matches("^[0-9a-fA-F-]{36}$"); // UUID format
    }

    @Test
    @DisplayName("Giữ nguyên idempotencyKey khi client có truyền")
    void shouldPreserveClientIdempotencyKeyWhenProvided() {
        TransferRequestDTO request = TransferRequestDTO.builder()
                .sourceAccountId("ACC001")
                .destinationAccountId("ACC002")
                .amount(new BigDecimal("50000"))
                .currency("VND")
                .idempotencyKey("client-key-999")
                .build();

        when(corebankClient.executeTransfer(any())).thenReturn(TransferResponseDTO.builder().build());

        handler.handle(request);

        ArgumentCaptor<TransferRequestDTO> captor = ArgumentCaptor.forClass(TransferRequestDTO.class);
        verify(corebankClient).executeTransfer(captor.capture());

        assertThat(captor.getValue().getIdempotencyKey()).isEqualTo("client-key-999");
    }

    @Test
    @DisplayName("Ném lỗi BusinessException khi tài khoản nguồn trùng tài khoản đích")
    void shouldRejectWhenSourceEqualsDestination() {
        TransferRequestDTO request = TransferRequestDTO.builder()
                .sourceAccountId("ACC001")
                .destinationAccountId("ACC001")
                .amount(new BigDecimal("10000"))
                .currency("VND")
                .build();

        assertThatThrownBy(() -> handler.handle(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Source and destination accounts must be different");
    }
}
