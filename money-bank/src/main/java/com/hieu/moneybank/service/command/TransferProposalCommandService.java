package com.hieu.moneybank.service.command;

import com.hieu.moneybank.domain.TransferProposal;
import java.util.Optional;

public interface TransferProposalCommandService {
    TransferProposal save(TransferProposal proposal);
    TransferProposal startValidating(String proposalId, String confirmerCustomerId);
    TransferProposal finalizeTransfer(String proposalId, String corebankTxId, String failureReason, boolean isSuccess);
    com.hieu.moneybank.dto.response.OtpResponseDTO sendOtp(String proposalId, String customerId);
}
