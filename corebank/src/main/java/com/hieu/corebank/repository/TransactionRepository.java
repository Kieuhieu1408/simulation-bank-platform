package com.hieu.corebank.repository;

import com.hieu.corebank.domain.BankTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<BankTransaction, String> {
    Optional<BankTransaction> findByIdempotencyKey(String idempotencyKey);

    @Query("""
        select t from BankTransaction t
        where (t.sourceAccount.id = :accountId or t.destinationAccount.id = :accountId)
          and t.createdAt >= :from and t.createdAt <= :to
        """)
    Page<BankTransaction> findHistory(@Param("accountId") String accountId,
                                      @Param("from") Instant from,
                                      @Param("to") Instant to,
                                      Pageable pageable);
}
