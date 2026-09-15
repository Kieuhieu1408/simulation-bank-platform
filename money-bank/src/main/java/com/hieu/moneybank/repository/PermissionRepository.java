package com.hieu.moneybank.repository;

import com.hieu.moneybank.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
