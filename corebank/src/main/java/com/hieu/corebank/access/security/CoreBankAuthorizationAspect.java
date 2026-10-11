package com.hieu.corebank.access.security;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CoreBankAuthorizationAspect — Kiểm tra thẩm quyền dịch vụ theo @CoreBankAuthorization (D17).
 * Hỗ trợ cả vai trò con người (TELLER, ADMIN) và vai trò dịch vụ (ROLE_SERVICE_MONEYBANK, ROLE_SERVICE_PROFILE, ...).
 */
@Aspect
@Component
@RequiredArgsConstructor
public class CoreBankAuthorizationAspect {

    private final PermissionCacheService permissionCacheService;

    @Before("@annotation(coreBankAuthorization)")
    public void checkPermission(JoinPoint joinPoint, CoreBankAuthorization coreBankAuthorization) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        // Hỗ trợ đối soát cả định dạng có tiền tố ROLE_ và không tiền tố (D17 Service Roles)
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .flatMap(auth -> {
                    if (auth.startsWith("ROLE_")) {
                        return Stream.of(auth, auth.substring(5));
                    }
                    return Stream.of(auth, "ROLE_" + auth);
                })
                .distinct()
                .collect(Collectors.toList());

        if (roles.isEmpty()) {
            throw new AccessDeniedException("User has no roles");
        }

        boolean hasPermission = permissionCacheService.hasAnyPermission(roles, coreBankAuthorization.menuCode(), coreBankAuthorization.action());

        if (!hasPermission) {
            throw new AccessDeniedException(String.format(
                    "User does not have permission %s on %s",
                    coreBankAuthorization.action(),
                    coreBankAuthorization.menuCode()
            ));
        }
    }
}
