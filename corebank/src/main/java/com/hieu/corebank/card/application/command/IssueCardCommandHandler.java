package com.hieu.corebank.card.application.command;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;
import com.hieu.corebank.card.application.port.AccountDirectory;
import com.hieu.corebank.card.domain.BankCard;
import com.hieu.corebank.card.domain.CardRepository;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.BusinessException;
import com.hieu.corebank.shared.domain.CardNumber;
import com.hieu.corebank.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * IssueCardCommandHandler — Use Case phát hành thẻ ngân hàng (Level 2 & Level 3 Validation).
 *
 * <p>Quy trình thẩm định:
 * <ul>
 *   <li>Level 2: Tài khoản liên kết phải tồn tại (qua AccountDirectory port).
 *   <li>Level 3: Tài khoản phải ở trạng thái ACTIVE (D5).
 *   <li>Sinh số thẻ 16 số hợp lệ theo thuật toán Luhn (CardNumber.generate).
 * </ul>
 */
@Slf4j
@Profile({ProfileConstants.COMMAND, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class IssueCardCommandHandler implements CommandHandler<IssueCardCommand, IssueCardResponseDTO> {

    private final CardRepository  cards;
    private final AccountDirectory accountDirectory;

    @Override
    @Transactional
    public IssueCardResponseDTO handle(IssueCardCommand command) {
        // Cấp 2: Kiểm tra tài khoản tồn tại qua Port
        AccountDirectory.AccountSummary account = accountDirectory.findById(command.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + command.accountId()));

        // Cấp 3: Tài khoản phải ACTIVE mới được phát hành thẻ (D5)
        if (!account.active()) {
            throw new BusinessException("Cannot issue card for inactive account: " + command.accountId());
        }

        String cardNumber = nextCardNumber();
        BankCard card = cards.save(BankCard.issue(
                cardNumber,
                account.accountId()
        ));

        log.info("Card issued: cardId={} maskedNumber={} accountId={}",
                card.getId(), card.mask(), account.accountId());

        return IssueCardResponseDTO.from(card, account.customerId());
    }

    private String nextCardNumber() {
        String value;
        do {
            value = CardNumber.generate("9704").value();
        } while (cards.existsByCardNumber(value));
        return value;
    }
}
