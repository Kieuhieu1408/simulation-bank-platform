package com.hieu.corebank.card.domain;

import java.util.List;
import java.util.Optional;

/**
 * CardRepository — Port lưu trữ và truy xuất BankCard.
 */
public interface CardRepository {

    BankCard save(BankCard card);

    Optional<BankCard> findById(String id);

    boolean existsByCardNumber(String cardNumber);

    List<BankCard> findByAccountIdInOrderByCreatedAtDesc(List<String> accountIds);

    long count();
}
