package com.hieu.moneybank.handler.command.transfer;

import com.hieu.common.constant.AccountStatus;
import com.hieu.common.cqrs.CommandHandler;
import com.hieu.moneybank.client.CorebankClient;
import com.hieu.moneybank.domain.TransferProposal;
import com.hieu.moneybank.dto.request.CreateTransferProposalCommand;
import com.hieu.moneybank.dto.response.AccountResponseDTO;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.service.command.TransferProposalCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * Handler tạo Transfer Proposal (bước 1/2).
 *
 * <h3>Luồng xử lý</h3>
 * <ol>
 *   <li>Gọi Corebank lấy thông tin tài khoản nguồn và đích.</li>
 *   <li>Kiểm tra tài khoản nguồn thuộc về khách hàng khởi tạo (từ JWT) — nếu không: 403.</li>
 *   <li>Kiểm tra trạng thái tài khoản đang ACTIVE.</li>
 *   <li>Kiểm tra currency khớp giữa lệnh và tài khoản nguồn.</li>
 *   <li>Tạo TransferProposal (BE tự sinh ID và gán idempotencyKey = ID).</li>
 *   <li>Lưu proposal với status PENDING.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateTransferProposalCommandHandler
        implements CommandHandler<CreateTransferProposalCommand, TransferProposalResponseDTO> {

    private final CorebankClient corebankClient;
    private final TransferProposalCommandService proposalService;

    @Override
    public TransferProposalResponseDTO handle(CreateTransferProposalCommand command) {

        // 1. Lấy thông tin tài khoản nguồn từ Corebank
        AccountResponseDTO source = corebankClient.getAccount(command.getSourceAccountId());

        // 2. Ownership: account nguồn phải thuộc người khởi tạo (customerId từ JWT)
        if (!source.getCifNumber().equals(command.getInitiatorCustomerId())) {
            log.warn("eventName=PROPOSAL_SOURCE_NOT_OWNED sourceAccountId={} initiatorCif={}",
                    source.getAccountId(), command.getInitiatorCustomerId());
            throw new AccessDeniedException("Tài khoản nguồn không thuộc khách hàng đang đăng nhập");
        }

        // 3. Lấy thông tin tài khoản đích từ Corebank
        AccountResponseDTO destination = corebankClient.getAccount(command.getDestinationAccountId());

        // 4. Validate accounts đang ACTIVE
        validateAccountActive(source, "Tài khoản nguồn");
        validateAccountActive(destination, "Tài khoản đích");

        // 5. Validate currency khớp với tài khoản nguồn
        if (!source.getCurrency().equalsIgnoreCase(command.getCurrency())) {
            throw new BusinessException(
                    "Currency của lệnh (" + command.getCurrency() +
                            ") không khớp với tài khoản nguồn (" + source.getCurrency() + ")");
        }

        // 6. Tạo proposal (domain invariants và server-side UUID / idempotencyKey được thiết lập bên trong constructor)
        TransferProposal proposal = new TransferProposal(
                command.getInitiatorCustomerId(),
                command.getSourceAccountId(),
                command.getDestinationAccountId(),
                command.getAmount(),
                command.getCurrency(),
                command.getDescription()
        );

        TransferProposal saved = proposalService.save(proposal);
        log.info("eventName=PROPOSAL_CREATED proposalId={} sourceAccountId={} destinationAccountId={} amount={} currency={}",
                saved.getId(), saved.getSourceAccountId(), saved.getDestinationAccountId(),
                saved.getAmount(), saved.getCurrency());

        return TransferProposalResponseDTO.from(saved);
    }

    private void validateAccountActive(AccountResponseDTO account, String label) {
        if (account.getStatus() == null) {
            throw new BusinessException(label + " không có trạng thái hợp lệ");
        }
        switch (account.getStatus()) {
            case ACTIVE -> { /* ok */ }
            case BLOCKED -> throw new BusinessException(label + " đang bị khóa");
            case CLOSED  -> throw new BusinessException(label + " đã đóng");
        }
    }
}
