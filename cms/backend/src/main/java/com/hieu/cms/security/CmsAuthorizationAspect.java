package com.hieu.cms.security;

import com.hieu.cms.repository.CmsAuthorizationRepository;
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

@Aspect
@Component
@RequiredArgsConstructor
public class CmsAuthorizationAspect {

    private final CmsAuthorizationRepository authorizationRepository;

    @Before("@annotation(cmsAuthorization)")
    public void checkPermission(JoinPoint joinPoint, CmsAuthorization cmsAuthorization) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        // Strip the ROLE_ prefix so values match the raw roleCode column in cms_role
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .collect(Collectors.toList());

        if (roles.isEmpty()) {
            throw new AccessDeniedException("User has no roles");
        }

        boolean hasPermission = authorizationRepository.hasPermission(roles, cmsAuthorization.menuCode(), cmsAuthorization.action());

        if (!hasPermission) {
            throw new AccessDeniedException(String.format(
                    "User does not have permission %s on %s", 
                    cmsAuthorization.action(), 
                    cmsAuthorization.menuCode()
            ));
        }
    }
}
