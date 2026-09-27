package com.hieu.corebank.eventsourcing.snapshot;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotRepository extends JpaRepository<AggregateSnapshot, String> {
}