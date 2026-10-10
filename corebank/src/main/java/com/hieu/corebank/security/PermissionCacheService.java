package com.hieu.corebank.security;

import com.hieu.common.constant.ActionType;
import com.hieu.corebank.domain.Permission;
import com.hieu.corebank.domain.Role;
import com.hieu.corebank.repository.RoleRepository;
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
    private final RoleRepository roleRepository;

    private static final String ROLE_PERMISSIONS_PREFIX = "role:%s:permissions";

    /**
     * Check if a role has the required permission via Redis.
     * Uses SISMEMBER for O(1) performance.
     */
    public boolean hasPermission(String roleCode, String menuCode, ActionType action) {
        String key = String.format(ROLE_PERMISSIONS_PREFIX, roleCode);
        String permissionValue = String.format("%s:%s", menuCode, action.name());
        
        Boolean isMember = redisTemplate.opsForSet().isMember(key, permissionValue);
        
        if (isMember == null || !isMember) {
            // Fallback check: if the key doesn't exist, it might have been evicted or not loaded yet.
            // Ideally, we ensure keys are always pre-loaded. If it's a guaranteed cache, 
            // returning false here is fine. Let's do a fallback just in case.
            if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
                log.warn("Cache miss for role {}. Reloading permissions.", roleCode);
                reloadRolePermissions(roleCode);
                isMember = redisTemplate.opsForSet().isMember(key, permissionValue);
            }
        }
        
        return Boolean.TRUE.equals(isMember);
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
        log.info("Loading role permissions into Redis cache...");
        List<Role> activeRoles = roleRepository.findAll().stream()
                .filter(Role::getStatus)
                .toList();

        for (Role role : activeRoles) {
            saveRolePermissionsToCache(role);
        }
        log.info("Loaded {} roles into Redis cache.", activeRoles.size());
    }

    /**
     * Syncs a specific role to cache. Useful when updating a role in DB.
     */
    public void reloadRolePermissions(String roleCode) {
        roleRepository.findByRoleCode(roleCode)
                .filter(Role::getStatus)
                .ifPresent(this::saveRolePermissionsToCache);
    }

    private void saveRolePermissionsToCache(Role role) {
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
