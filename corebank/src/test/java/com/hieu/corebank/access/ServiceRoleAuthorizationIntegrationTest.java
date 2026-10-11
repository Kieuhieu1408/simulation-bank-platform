package com.hieu.corebank.access;

import com.hieu.common.constant.ActionType;
import com.hieu.corebank.access.infrastructure.persistence.rbac.PermissionEntity;
import com.hieu.corebank.access.infrastructure.persistence.rbac.PermissionJpaRepository;
import com.hieu.corebank.access.infrastructure.persistence.rbac.RoleEntity;
import com.hieu.corebank.access.infrastructure.persistence.rbac.RoleJpaRepository;
import com.hieu.corebank.access.security.CoreBankAuthorization;
import com.hieu.corebank.access.security.PermissionCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class ServiceRoleAuthorizationIntegrationTest {

    @Autowired
    private RoleJpaRepository roleRepository;

    @Autowired
    private PermissionJpaRepository permissionRepository;

    @Autowired
    private PermissionCacheService permissionCacheService;

    @Autowired
    private DummySecuredService dummySecuredService;

    @BeforeEach
    void setupRolesAndPermissions() {
        if (roleRepository.count() == 0) {
            PermissionEntity pTransferCreate = permissionRepository.save(new PermissionEntity("transfer_management", ActionType.CREATE, "Transfer"));
            PermissionEntity pCustomerCreate = permissionRepository.save(new PermissionEntity("customer_management", ActionType.CREATE, "Customer"));

            RoleEntity roleMoneybank = new RoleEntity("ROLE_SERVICE_MONEYBANK", "Service MoneyBank", true);
            roleMoneybank.getPermissions().add(pTransferCreate);
            roleRepository.save(roleMoneybank);

            RoleEntity roleProfile = new RoleEntity("ROLE_SERVICE_PROFILE", "Service Profile", true);
            roleProfile.getPermissions().add(pCustomerCreate);
            roleRepository.save(roleProfile);

            permissionCacheService.loadAllRolesToCache();
        }
    }

    @Test
    @DisplayName("D17: ROLE_SERVICE_MONEYBANK được cấp quyền transfer_management CREATE nhưng bị từ chối customer_management CREATE")
    void testServiceMoneybankPermissions() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("service_moneybank", "N/A",
                        List.of(new SimpleGrantedAuthority("ROLE_SERVICE_MONEYBANK")))
        );

        // Cho phép chuyển tiền
        String result = dummySecuredService.createTransfer();
        assertThat(result).isEqualTo("transfer_created");

        // Bị từ chối tạo customer
        assertThatThrownBy(() -> dummySecuredService.createCustomer())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User does not have permission");
    }

    @Test
    @DisplayName("D17: ROLE_SERVICE_PROFILE được cấp quyền customer_management CREATE nhưng bị từ chối transfer_management CREATE")
    void testServiceProfilePermissions() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("service_profile", "N/A",
                        List.of(new SimpleGrantedAuthority("ROLE_SERVICE_PROFILE")))
        );

        // Cho phép tạo customer
        String result = dummySecuredService.createCustomer();
        assertThat(result).isEqualTo("customer_created");

        // Bị từ chối chuyển tiền
        assertThatThrownBy(() -> dummySecuredService.createTransfer())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User does not have permission");
    }

    @Test
    @DisplayName("D17: Yêu cầu chưa xác thực ném lỗi AccessDeniedException")
    void testUnauthenticatedAccessDenied() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> dummySecuredService.createTransfer())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Service
    public static class DummySecuredService {

        @CoreBankAuthorization(menuCode = "transfer_management", action = ActionType.CREATE)
        public String createTransfer() {
            return "transfer_created";
        }

        @CoreBankAuthorization(menuCode = "customer_management", action = ActionType.CREATE)
        public String createCustomer() {
            return "customer_created";
        }
    }
}
