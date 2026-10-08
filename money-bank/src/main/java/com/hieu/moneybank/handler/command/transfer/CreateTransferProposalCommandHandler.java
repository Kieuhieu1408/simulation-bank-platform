package com.hieu.moneybank.handler.command.transfer;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.moneybank.domain.Account;
import com.hieu.moneybank.domain.TransferProposal;
import com.hieu.moneybank.dto.request.CreateTransferProposalCommand;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.exception.NotFoundException;
import com.hieu.moneybank.service.command.AccountCommandService;
import com.hieu.moneybank.service.command.TransferProposalCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * Handler tạo Transfer Proposal (U-06 bước 1/2).
 *
 * <h3>Luồng xử lý</h3>
 * <ol>
 *   <li>Validate accounts tồn tại và ACTIVE.</li>
 *   <li>Kiểm tra account nguồn thuộc về người khởi tạo (từ JWT) — nếu không: 403.</li>
 *   <li>Validate currency khớp giữa lệnh và tài khoản nguồn.</li>
 *   <li>Kiểm tra domain invariants qua TransferProposal constructor.</li>
 *   <li>Lưu proposal với status PENDING.</li>
 * </ol>
 *
 * <p>Proposal không idempotent: mỗi lần gọi tạo một proposal mới (tính nhất quán chặt
 * chỉ cần ở bước chuyển tiền). Handler không mở transaction ngoài: các lần đọc không
 * cần transaction, và việc lưu nằm trong transaction ngắn của
 * {@link TransferProposalCommandService#save}.
 *
 * <p>Corebank <b>chưa</b> được gọi ở bước này.
 * Chỉ là bước "đặt lệnh" — Corebank sẽ được gọi tại {@link ConfirmTransferProposalCommandHandler}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateTransferProposalCommandHandler
        implements CommandHandler<CreateTransferProposalCommand, TransferProposalResponseDTO> {

    private final AccountCommandService accountCommandService;
    private final TransferProposalCommandService proposalService;

    @Override
    public TransferProposalResponseDTO handle(CreateTransferProposalCommand command) {

        // 1. Validate accounts tồn tại
        Account source = accountCommandService.findById(command.getSourceAccountId())
            .orElseThrow(() -> new NotFoundException(
                "Tài khoản nguồn không tồn tại: " + command.getSourceAccountId()));

        // 2. Ownership: account nguồn phải thuộc người khởi tạo (customerId từ JWT)
        if (!source.getCustomer().getCifNumber().equals(command.getInitiatorCustomerId())) {
            log.warn("eventName=PROPOSAL_SOURCE_NOT_OWNED sourceAccountId={}", source.getId());
            throw new AccessDeniedException("Tài khoản nguồn không thuộc khách hàng đang đăng nhập");
        }

        Account destination = accountCommandService.findById(command.getDestinationAccountId())
            .orElseThrow(() -> new NotFoundException(
                "Tài khoản đích không tồn tại: " + command.getDestinationAccountId()));

        // 3. Validate accounts đang ACTIVE
        validateAccountActive(source,      "Tài khoản nguồn");
        validateAccountActive(destination, "Tài khoản đích");

        // 4. Validate currency khớp với tài khoản nguồn
        if (!source.getCurrency().equalsIgnoreCase(command.getCurrency())) {
            throw new BusinessException(
                "Currency của lệnh (" + command.getCurrency() +
                ") không khớp với tài khoản nguồn (" + source.getCurrency() + ")");
        }

        // 5. Tạo proposal (domain invariants được kiểm tra bên trong constructor)
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

    private void validateAccountActive(Account account, String label) {
        switch (account.getStatus()) {
            case ACTIVE -> { /* ok */ }
            case BLOCKED -> throw new BusinessException(label + " đang bị khóa");
            case CLOSED  -> throw new BusinessException(label + " đã đóng");
        }
    }
}
