package com.hieu.corebank.access.security;

import com.hieu.common.constant.ActionType;
import com.hieu.corebank.access.infrastructure.persistence.rbac.PermissionEntity;
import com.hieu.corebank.access.infrastructure.persistence.rbac.RoleEntity;
import com.hieu.corebank.access.infrastructure.persistence.rbac.RoleJpaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionCacheService {

    private final StringRedisTemplate redisTemplate;
    private final RoleJpaRepository roleRepository;

    private static final String ROLE_PERMISSIONS_PREFIX = "role:%s:permissions";

    /**
     * Check if a role has the required permission via Redis.
     * Uses SISMEMBER for O(1) performance.
     * Tự động fallback sang DB nếu Redis không khả dụng hoặc cache miss.
     */
    public boolean hasPermission(String roleCode, String menuCode, ActionType action) {
        String key = String.format(ROLE_PERMISSIONS_PREFIX, roleCode);
        String permissionValue = String.format("%s:%s", menuCode, action.name());

        try {
            Boolean hasKey = redisTemplate.hasKey(key);
            if (Boolean.TRUE.equals(hasKey)) {
                Boolean isMember = redisTemplate.opsForSet().isMember(key, permissionValue);
                return Boolean.TRUE.equals(isMember);
            }

            // Cache miss: nạp permissions từ DB vào Redis
            log.debug("Cache miss for role {}. Reloading permissions.", roleCode);
            reloadRolePermissions(roleCode);
            Boolean isMember = redisTemplate.opsForSet().isMember(key, permissionValue);
            return Boolean.TRUE.equals(isMember);
        } catch (Exception ex) {
            log.warn("Redis unavailable for checking role={}, falling back to DB: {}", roleCode, ex.getMessage());
            return checkPermissionFromDb(roleCode, menuCode, action);
        }
    }

    private boolean checkPermissionFromDb(String roleCode, String menuCode, ActionType action) {
        return roleRepository.findByRoleCode(roleCode)
                .filter(RoleEntity::getStatus)
                .map(role -> role.getPermissions().stream()
                        .anyMatch(p -> p.getMenuCode().equalsIgnoreCase(menuCode) && p.getAction() == action))
                .orElse(false);
    }

    /**
     * Helper to check multiple roles. Returns true if ANY role has the permission.
     */
    public boolean hasAnyPermission(List<String> roleCodes, String menuCode, ActionType action) {
        for (String roleCode : roleCodes) {
            if (hasPermission(roleCode, menuCode, action)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reloads all active roles and their permissions into Redis.
     */
    @PostConstruct
    public void loadAllRolesToCache() {
        try {
            log.info("Loading role permissions into Redis cache...");
            List<RoleEntity> activeRoles = roleRepository.findAll().stream()
                    .filter(RoleEntity::getStatus)
                    .toList();

            for (RoleEntity role : activeRoles) {
                saveRolePermissionsToCache(role);
            }
            log.info("Loaded {} roles into Redis cache.", activeRoles.size());
        } catch (Exception ex) {
            log.warn("Redis unavailable during startup, RBAC will fallback to DB: {}", ex.getMessage());
        }
    }

    /**
     * Syncs a specific role to cache. Useful when updating a role in DB.
     */
    public void reloadRolePermissions(String roleCode) {
        try {
            roleRepository.findByRoleCode(roleCode)
                    .filter(RoleEntity::getStatus)
                    .ifPresent(this::saveRolePermissionsToCache);
        } catch (Exception ex) {
            log.warn("Failed to reload permissions for role {}: {}", roleCode, ex.getMessage());
        }
    }

    private void saveRolePermissionsToCache(RoleEntity role) {
        String key = String.format(ROLE_PERMISSIONS_PREFIX, role.getRoleCode());

        // Delete old cache for this role
        redisTemplate.delete(key);

        // Convert permissions to SET values
        List<String> permissionValues = role.getPermissions().stream()
                .map(p -> String.format("%s:%s", p.getMenuCode(), p.getAction().name()))
                .toList();

        if (!permissionValues.isEmpty()) {
            redisTemplate.opsForSet().add(key, permissionValues.toArray(new String[0]));
        }
    }
}
