package com.hieu.corebank.access.infrastructure.persistence.rbac;

import com.hieu.common.constant.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByRoleCode(String roleCode);

    @Query("SELECT COUNT(r) > 0 FROM RoleEntity r " +
           "JOIN r.permissions p " +
           "WHERE r.roleCode IN :roles " +
           "AND p.menuCode = :menuCode " +
           "AND p.action = :action " +
           "AND r.status = true")
    boolean hasPermission(@Param("roles") List<String> roles,
                          @Param("menuCode") String menuCode,
                          @Param("action") ActionType action);
}
