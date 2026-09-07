package com.hieu.moneybank.shared.outbox;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    /**
     * Lấy các event chưa publish và đã tới hạn thử lại, cũ nhất trước.
     *
     * <p>Sắp theo {@code occurredAt} để giữ thứ tự phát gần với thứ tự phát sinh.
     * Đây không phải bảo đảm thứ tự tuyệt đối: khi có nhiều worker hoặc nhiều
     * partition, thứ tự tổng thể không được đảm bảo, nên consumer không được dựa
     * vào thứ tự để suy ra trạng thái.
     */
    @Query("""
            select e from OutboxEvent e
            where e.publishedAt is null
              and e.nextAttemptAt <= :now
            order by e.occurredAt asc, e.id asc
            """)
    List<OutboxEvent> findPending(@Param("now") Instant now, Limit limit);

    Optional<OutboxEvent> findByEventId(String eventId);

    long countByPublishedAtIsNull();
}
