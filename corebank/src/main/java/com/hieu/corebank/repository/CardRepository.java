package com.hieu.corebank.repository;

import com.hieu.corebank.domain.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CardRepository extends JpaRepository<BankCard, String> {
    boolean existsByCardNumber(String cardNumber);
    List<BankCard> findByAccountCustomerCifNumberOrderByCreatedAtDesc(String cifNumber);
}
