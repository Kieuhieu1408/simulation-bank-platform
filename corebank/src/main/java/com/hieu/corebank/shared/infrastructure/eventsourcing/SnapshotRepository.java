package com.hieu.corebank.shared.infrastructure.eventsourcing;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotRepository extends JpaRepository<AggregateSnapshot, String> {
}
