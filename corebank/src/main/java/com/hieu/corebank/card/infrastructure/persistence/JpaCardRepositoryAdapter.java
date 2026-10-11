package com.hieu.corebank.card.infrastructure.persistence;

import com.hieu.corebank.card.domain.BankCard;
import com.hieu.corebank.card.domain.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class JpaCardRepositoryAdapter implements CardRepository {

    private final CardJpaRepository jpaRepository;

    @Override
    public BankCard save(BankCard card) {
        CardEntity entity = CardEntity.fromDomain(card);
        CardEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<BankCard> findById(String id) {
        return jpaRepository.findById(id).map(CardEntity::toDomain);
    }

    @Override
    public boolean existsByCardNumber(String cardNumber) {
        return jpaRepository.existsByCardNumber(cardNumber);
    }

    @Override
    public List<BankCard> findByAccountIdInOrderByCreatedAtDesc(List<String> accountIds) {
        return jpaRepository.findByAccountIdInOrderByCreatedAtDesc(accountIds).stream()
                .map(CardEntity::toDomain)
                .toList();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
