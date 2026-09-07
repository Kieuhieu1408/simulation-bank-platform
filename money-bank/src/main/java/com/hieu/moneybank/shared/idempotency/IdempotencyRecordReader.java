package com.hieu.moneybank.shared.idempotency;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Đọc bản ghi claim hiện có trong một transaction mới (TX-B theo
 * {@code data-and-consistency-design.md} mục 2.2).
 *
 * <p>{@code REQUIRES_NEW} là bắt buộc: sau khi insert thất bại vì trùng khóa,
 * transaction cũ đã bị đánh dấu rollback. Đọc lại trong transaction đó sẽ ném
 * lỗi thay vì trả dữ liệu, nên phải mở transaction sạch mới thấy được bản ghi mà
 * request thắng đã commit.
 */
@Component
public class IdempotencyRecordReader {

    private final IdempotencyRecordRepository repository;

    public IdempotencyRecordReader(IdempotencyRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Optional<IdempotencyRecord> findExisting(String customerId,
                                                    IdempotencyOperation operation,
                                                    String idempotencyKey) {
        return repository.findByCustomerIdAndOperationAndIdempotencyKey(customerId, operation, idempotencyKey);
    }
}
