package com.hieu.corebank.card.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardJpaRepository extends JpaRepository<CardEntity, String> {
    boolean existsByCardNumber(String cardNumber);
    List<CardEntity> findByAccountIdInOrderByCreatedAtDesc(List<String> accountIds);
}
