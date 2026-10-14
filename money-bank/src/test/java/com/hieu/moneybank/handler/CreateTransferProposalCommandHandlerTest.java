package com.hieu.moneybank.handler;

import com.hieu.common.constant.AccountStatus;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.constant.ProposalStatus;
import com.hieu.moneybank.domain.TransferProposal;
import com.hieu.moneybank.dto.request.CreateTransferProposalCommand;
import com.hieu.moneybank.dto.response.AccountResponseDTO;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.handler.command.transfer.CreateTransferProposalCommandHandler;
import com.hieu.moneybank.service.command.TransferProposalCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTransferProposalCommandHandlerTest {

    @Mock
    private CorebankClient corebankClient;

    @Mock
    private TransferProposalCommandService proposalService;

    private CreateTransferProposalCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CreateTransferProposalCommandHandler(corebankClient, proposalService);
    }

    @Test
    @DisplayName("Tạo proposal thành công với server-generated proposalId và idempotencyKey khớp nhau")
    void shouldCreateProposalSuccessfully() {
        CreateTransferProposalCommand command = CreateTransferProposalCommand.builder()
                .initiatorCustomerId("CIF00000001")
                .sourceAccountId("ACC001")
                .destinationAccountId("ACC002")
                .amount(new BigDecimal("200000"))
                .currency("VND")
                .description("Payment for order #123")
                .build();

        AccountResponseDTO source = AccountResponseDTO.builder()
                .accountId("ACC001")
                .cifNumber("CIF00000001")
                .status(AccountStatus.ACTIVE)
                .currency("VND")
                .build();

        AccountResponseDTO destination = AccountResponseDTO.builder()
                .accountId("ACC002")
                .cifNumber("CIF00000002")
                .status(AccountStatus.ACTIVE)
                .currency("VND")
                .build();

        when(corebankClient.getAccount("ACC001")).thenReturn(source);
        when(corebankClient.getAccount("ACC002")).thenReturn(destination);
        when(proposalService.save(any(TransferProposal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransferProposalResponseDTO response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(ProposalStatus.PENDING);
        assertThat(response.getProposalId()).isNotBlank();
        assertThat(response.getProposalId()).matches("^[0-9a-fA-F-]{36}$");

        ArgumentCaptor<TransferProposal> captor = ArgumentCaptor.forClass(TransferProposal.class);
        verify(proposalService).save(captor.capture());

        TransferProposal saved = captor.getValue();
        // Kiểm tra BE tự sinh idempotencyKey = id của proposal
        assertThat(saved.getIdempotencyKey()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Từ chối 403 khi tài khoản nguồn không thuộc CIF của user đang đăng nhập")
    void shouldThrowAccessDeniedWhenSourceNotOwned() {
        CreateTransferProposalCommand command = CreateTransferProposalCommand.builder()
                .initiatorCustomerId("CIF_DIFFERENT")
                .sourceAccountId("ACC001")
                .destinationAccountId("ACC002")
                .amount(new BigDecimal("200000"))
                .currency("VND")
                .description("Hack attempt")
                .build();

        AccountResponseDTO source = AccountResponseDTO.builder()
                .accountId("ACC001")
                .cifNumber("CIF00000001") // Thuộc CIF00000001, không phải CIF_DIFFERENT
                .status(AccountStatus.ACTIVE)
                .currency("VND")
                .build();

        when(corebankClient.getAccount("ACC001")).thenReturn(source);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Tài khoản nguồn không thuộc khách hàng đang đăng nhập");
    }

    @Test
    @DisplayName("Từ chối khi tài khoản nguồn không ACTIVE (bị khóa hoặc đóng)")
    void shouldRejectWhenSourceAccountNotActive() {
        CreateTransferProposalCommand command = CreateTransferProposalCommand.builder()
                .initiatorCustomerId("CIF00000001")
                .sourceAccountId("ACC001")
                .destinationAccountId("ACC002")
                .amount(new BigDecimal("100000"))
                .currency("VND")
                .description("Transfer from blocked account")
                .build();

        AccountResponseDTO source = AccountResponseDTO.builder()
                .accountId("ACC001")
                .cifNumber("CIF00000001")
                .status(AccountStatus.BLOCKED)
                .currency("VND")
                .build();

        AccountResponseDTO destination = AccountResponseDTO.builder()
                .accountId("ACC002")
                .cifNumber("CIF00000002")
                .status(AccountStatus.ACTIVE)
                .currency("VND")
                .build();

        when(corebankClient.getAccount("ACC001")).thenReturn(source);
        when(corebankClient.getAccount("ACC002")).thenReturn(destination);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("đang bị khóa");
    }

    @Test
    @DisplayName("Từ chối khi currency của lệnh chuyển không khớp currency của tài khoản nguồn")
    void shouldRejectWhenCurrencyMismatch() {
        CreateTransferProposalCommand command = CreateTransferProposalCommand.builder()
                .initiatorCustomerId("CIF00000001")
                .sourceAccountId("ACC001")
                .destinationAccountId("ACC002")
                .amount(new BigDecimal("100"))
                .currency("USD") // Lệnh đòi chuyển USD
                .description("Currency mismatch")
                .build();

        AccountResponseDTO source = AccountResponseDTO.builder()
                .accountId("ACC001")
                .cifNumber("CIF00000001")
                .status(AccountStatus.ACTIVE)
                .currency("VND") // Tài khoản nguồn là VND
                .build();

        AccountResponseDTO destination = AccountResponseDTO.builder()
                .accountId("ACC002")
                .cifNumber("CIF00000002")
                .status(AccountStatus.ACTIVE)
                .currency("VND")
                .build();

        when(corebankClient.getAccount("ACC001")).thenReturn(source);
        when(corebankClient.getAccount("ACC002")).thenReturn(destination);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("không khớp với tài khoản nguồn");
    }
}
