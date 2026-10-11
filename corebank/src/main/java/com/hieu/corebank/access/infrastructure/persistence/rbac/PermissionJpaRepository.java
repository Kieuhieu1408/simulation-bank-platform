package com.hieu.corebank.access.infrastructure.persistence.rbac;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, Long> {
}
