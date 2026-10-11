package com.hieu.corebank.shared.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class cho tất cả Aggregate Root trong kiến trúc Event Sourcing.
 *
 * @param <ID> Kiểu dữ liệu định danh của Aggregate
 */
public abstract class AggregateRoot<ID> {

    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();
    protected long version = 0L;

    public abstract ID id();

    public long version() {
        return version;
    }

    public List<DomainEvent> uncommittedEvents() {
        return Collections.unmodifiableList(uncommittedEvents);
    }

    public void markCommitted() {
        uncommittedEvents.clear();
    }

    protected void applyNew(DomainEvent event) {
        apply(event);
        this.version = event.version();
        this.uncommittedEvents.add(event);
    }

    protected abstract void apply(DomainEvent event);

    protected void setVersion(long version) {
        this.version = version;
    }

    protected long nextVersion() {
        return this.version + 1L;
    }
}
