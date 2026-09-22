package com.hieu.common.idempotency;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository của {@link IdempotencyRecord}.
 *
 * <p>Chỉ có tra cứu theo unique key. Cố tình không cung cấp phương thức "tìm rồi
 * quyết định có insert hay không" ở đây, vì đó chính là anti-pattern
 * check-then-act đã gây race {@code DV-F-04}. Việc đọc chỉ dùng sau khi insert
 * thất bại vì trùng khóa.
 */
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, Long> {

    Optional<IdempotencyRecord> findByCustomerIdAndOperationAndIdempotencyKey(
            String customerId,
            IdempotencyOperation operation,
            String idempotencyKey);
}
