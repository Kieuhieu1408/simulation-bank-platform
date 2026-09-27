package com.hieu.corebank.eventsourcing.snapshot;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Bản chụp trạng thái của Aggregate tại một version nhất định.
 * Được tạo sau mỗi SNAPSHOT_THRESHOLD event để tối ưu tốc độ Replay.
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "aggregate_snapshots")
public class AggregateSnapshot {

    @Id
    @Column(name = "aggregate_id", length = 50, nullable = false)
    private String aggregateId;

    @Column(name = "aggregate_type", length = 50, nullable = false)
    private String aggregateType;

    @Column(name = "version", nullable = false)
    private long version;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public AggregateSnapshot(String aggregateId, String aggregateType,
                             long version, String payload) {
        this.aggregateId   = aggregateId;
        this.aggregateType = aggregateType;
        this.version       = version;
        this.payload       = payload;
        this.createdAt     = Instant.now();
    }
}