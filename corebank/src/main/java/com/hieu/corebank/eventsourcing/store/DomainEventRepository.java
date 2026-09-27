package com.hieu.corebank.eventsourcing.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DomainEventRepository extends JpaRepository<DomainEventRecord, String> {

    List<DomainEventRecord> findByAggregateIdAndVersionGreaterThanOrderByVersionAsc(
            String aggregateId, long version);
}