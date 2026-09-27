package com.hieu.cms.repository;

import com.hieu.cms.entity.CmsRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CmsAuthorizationRepository extends JpaRepository<CmsRole, UUID> {

    @Query("SELECT COUNT(r) > 0 FROM CmsRole r " +
           "JOIN CmsRolePermission rp ON r.id = rp.roleId " +
           "JOIN CmsPermission p ON p.id = rp.permissionId " +
           "WHERE r.roleCode IN :roles " +
           "AND p.menuCode = :menuCode " +
           "AND p.actionCode = :actionCode " +
           "AND rp.isGranted = true " +
           "AND r.status = 'ACTIVE'")
    boolean hasPermission(@Param("roles") List<String> roles, 
                          @Param("menuCode") String menuCode, 
                          @Param("actionCode") String actionCode);
}
