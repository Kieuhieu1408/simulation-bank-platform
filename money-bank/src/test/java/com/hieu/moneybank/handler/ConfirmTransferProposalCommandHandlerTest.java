package com.hieu.moneybank.handler;

import com.hieu.common.constant.CommonErrorCode;
import com.hieu.common.exception.CommonException;
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
import org.springframework.http.HttpStatus;

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
    @DisplayName("Confirm proposal thành công khi OTP hợp lệ và Corebank trả SUCCESS")
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
    @DisplayName("Ném BusinessException khi OTP không chính xác — proposal không bị chạm")
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

    @Test
    @DisplayName("TRF-INV-008: Corebank 5xx → ném OPERATION_OUTCOME_UNKNOWN, KHÔNG finalise proposal")
    void shouldThrowOutcomeUnknownOnCorebank5xx_AndNotFinalise() {
        String proposalId = "prop-5xx";
        String customerId = "CIF00000001";

        ConfirmTransferProposalCommand command = ConfirmTransferProposalCommand.builder()
                .proposalId(proposalId)
                .confirmerCustomerId(customerId)
                .otp("111111")
                .build();

        TransferProposal proposal = new TransferProposal(
                customerId, "ACC001", "ACC002",
                new BigDecimal("50000"), "VND", "Test transfer"
        );
        proposal.startValidating();

        when(proposalService.startValidating(proposalId, customerId)).thenReturn(proposal);
        when(corebankClient.executeTransfer(any()))
                .thenThrow(new CommonException(CommonErrorCode.OPERATION_OUTCOME_UNKNOWN,
                        "Kết quả chuyển tiền tại Corebank chưa xác định [503]"));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> {
                    CommonException ce = (CommonException) ex;
                    assertThat(ce.errorCode().code()).isEqualTo("202001");
                    assertThat(ce.errorCode().httpStatus()).isEqualTo(HttpStatus.ACCEPTED);
                });

        // finalizeTransfer KHÔNG được gọi → proposal vẫn giữ VALIDATING
        verify(proposalService, never()).finalizeTransfer(any(), any(), any(), anyBoolean());
    }

    @Test
    @DisplayName("TRF-INV-008: Corebank 4xx (BusinessException) → finalize FAILED")
    void shouldFinaliseFailedOnCorebank4xx() {
        String proposalId = "prop-4xx";
        String customerId = "CIF00000001";

        ConfirmTransferProposalCommand command = ConfirmTransferProposalCommand.builder()
                .proposalId(proposalId)
                .confirmerCustomerId(customerId)
                .otp("111111")
                .build();

        TransferProposal proposal = new TransferProposal(
                customerId, "ACC001", "ACC002",
                new BigDecimal("50000"), "VND", "Test transfer"
        );
        proposal.startValidating();

        when(proposalService.startValidating(proposalId, customerId)).thenReturn(proposal);
        when(corebankClient.executeTransfer(any()))
                .thenThrow(new BusinessException("Corebank từ chối giao dịch [422]: Số dư không đủ"));

        TransferProposal failedProposal = new TransferProposal(
                customerId, "ACC001", "ACC002",
                new BigDecimal("50000"), "VND", "Test transfer"
        );
        failedProposal.startValidating();
        failedProposal.markFailed("COREBANK_REJECTED: Corebank từ chối giao dịch [422]: Số dư không đủ");

        when(proposalService.finalizeTransfer(any(), isNull(), any(), eq(false)))
                .thenReturn(failedProposal);

        TransferProposalResponseDTO result = handler.handle(command);

        // finalizeTransfer được gọi với corebankOk=false
        verify(proposalService).finalizeTransfer(any(), isNull(), any(), eq(false));
        assertThat(result.getStatus().name()).isEqualTo("FAILED");
    }
}
