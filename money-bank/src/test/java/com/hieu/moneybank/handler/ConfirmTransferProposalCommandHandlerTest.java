package com.hieu.moneybank.handler;

import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.domain.TransferProposal;
import com.hieu.moneybank.dto.request.ConfirmTransferProposalCommand;
import com.hieu.moneybank.dto.request.TransferRequestDTO;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import com.hieu.moneybank.dto.response.TransferResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.handler.command.transfer.ConfirmTransferProposalCommandHandler;
import com.hieu.moneybank.service.command.TransferProposalCommandService;
import com.hieu.moneybank.service.otp.OtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmTransferProposalCommandHandlerTest {

    @Mock
    private TransferProposalCommandService proposalService;

    @Mock
    private CorebankClient corebankClient;

    @Mock
    private OtpService otpService;

    private ConfirmTransferProposalCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ConfirmTransferProposalCommandHandler(proposalService, corebankClient, otpService);
    }

    @Test
    @DisplayName("Confirm proposal thành công khi OTP hợp lệ")
    void shouldConfirmSuccessfullyWhenOtpValid() {
        String proposalId = "prop-123";
        String customerId = "CIF00000001";
        String otp = "111111";

        ConfirmTransferProposalCommand command = ConfirmTransferProposalCommand.builder()
                .proposalId(proposalId)
                .confirmerCustomerId(customerId)
                .otp(otp)
                .build();

        TransferProposal proposal = new TransferProposal(
                customerId, "ACC001", "ACC002",
                new BigDecimal("50000"), "VND", "Test transfer"
        );
        proposal.startValidating();

        when(proposalService.startValidating(proposalId, customerId)).thenReturn(proposal);

        TransferResponseDTO corebankResponse = TransferResponseDTO.builder()
                .transactionId("tx-core-999")
                .build();
        when(corebankClient.executeTransfer(any(TransferRequestDTO.class))).thenReturn(corebankResponse);

        TransferProposal confirmedProposal = new TransferProposal(
                customerId, "ACC001", "ACC002",
                new BigDecimal("50000"), "VND", "Test transfer"
        );
        confirmedProposal.startValidating();
        confirmedProposal.markConfirmed("tx-core-999");

        when(proposalService.finalizeTransfer(any(), eq("tx-core-999"), isNull(), eq(true)))
                .thenReturn(confirmedProposal);

        TransferProposalResponseDTO result = handler.handle(command);

        verify(otpService).validateOtp(proposalId, otp);
        assertThat(result.getStatus().name()).isEqualTo("CONFIRMED");
    }

    @Test
    @DisplayName("Ném BusinessException khi OTP không chính xác")
    void shouldThrowExceptionWhenOtpInvalid() {
        String proposalId = "prop-123";
        String customerId = "CIF00000001";
        String wrongOtp = "999999";

        ConfirmTransferProposalCommand command = ConfirmTransferProposalCommand.builder()
                .proposalId(proposalId)
                .confirmerCustomerId(customerId)
                .otp(wrongOtp)
                .build();

        doThrow(new BusinessException("Mã OTP không chính xác"))
                .when(otpService).validateOtp(proposalId, wrongOtp);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Mã OTP không chính xác");

        verifyNoInteractions(proposalService);
        verifyNoInteractions(corebankClient);
    }
}
