package com.hieu.corebank.transfer.application.port;

import com.hieu.corebank.shared.domain.AccountId;
import com.hieu.corebank.shared.domain.Money;
import com.hieu.corebank.shared.domain.TransferId;

/**
 * Port cho phép Transfer BC giao tiếp với Account BC.
 *
 * <p>Tuân thủ nguyên tắc Clean Architecture & DDD:
 * Transfer BC chỉ phụ thuộc vào Port interface này, không import Entity hoặc Infrastructure của Account.
 */
public interface AccountGateway {

    void reserveFunds(AccountId accountId, TransferId transferId, Money amount);

    void creditFunds(AccountId accountId, TransferId transferId, Money amount);

    void settleReservation(AccountId accountId, TransferId transferId);

    void releaseReservation(AccountId accountId, TransferId transferId, String reason);
}
