package com.hieu.corebank.config;

import com.hieu.common.constant.ActionType;
import com.hieu.common.constant.UserStatus;
import com.hieu.common.cqrs.Dispatcher;
import com.hieu.corebank.access.infrastructure.persistence.rbac.PermissionEntity;
import com.hieu.corebank.access.infrastructure.persistence.rbac.PermissionJpaRepository;
import com.hieu.corebank.access.infrastructure.persistence.rbac.RoleEntity;
import com.hieu.corebank.access.infrastructure.persistence.rbac.RoleJpaRepository;
import com.hieu.corebank.access.infrastructure.persistence.user.UserEntity;
import com.hieu.corebank.access.infrastructure.persistence.user.UserJpaRepository;
import com.hieu.corebank.account.application.command.CreateAccountCommand;
import com.hieu.corebank.customer.application.command.CreateCustomerCommand;
import com.hieu.corebank.customer.domain.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.Set;

@Configuration
@Profile("!test")
public class DemoDataInitializer {

    @Bean
    CommandLineRunner demoData(
            CustomerRepository customerRepository,
            UserJpaRepository userRepository,
            RoleJpaRepository roleRepository,
            PermissionJpaRepository permissionRepository,
            Dispatcher dispatcher) {

        return args -> {
            // Seed RBAC data
            if (userRepository.count() == 0) {
                // 1. Create Permissions matching @CoreBankAuthorization menuCodes
                PermissionEntity pTransferCreate = new PermissionEntity("transfer_management", ActionType.CREATE, "Create transfer transaction");
                PermissionEntity pTransferRead   = new PermissionEntity("transfer_management", ActionType.READ, "Read transfer transactions");
                PermissionEntity pAccountCreate  = new PermissionEntity("account_management", ActionType.CREATE, "Create bank account");
                PermissionEntity pAccountRead    = new PermissionEntity("account_management", ActionType.READ, "Read account details");
                PermissionEntity pCustomerCreate = new PermissionEntity("customer_management", ActionType.CREATE, "Create customer profile");
                PermissionEntity pCustomerRead   = new PermissionEntity("customer_management", ActionType.READ, "Read customer profile");
                PermissionEntity pCardCreate     = new PermissionEntity("card_management", ActionType.CREATE, "Issue bank card");
                PermissionEntity pCardRead       = new PermissionEntity("card_management", ActionType.READ, "Read bank cards");

                Set<PermissionEntity> allPerms = Set.of(
                        pTransferCreate, pTransferRead,
                        pAccountCreate, pAccountRead,
                        pCustomerCreate, pCustomerRead,
                        pCardCreate, pCardRead
                );
                permissionRepository.saveAll(allPerms);

                // 2. Create Roles (Human & Service Roles - D17)
                RoleEntity roleTeller = new RoleEntity("TELLER", "Teller", true);
                roleTeller.getPermissions().addAll(allPerms);

                RoleEntity roleAdmin = new RoleEntity("ADMIN", "System Admin", true);
                roleAdmin.getPermissions().addAll(allPerms);

                RoleEntity roleMoneybank = new RoleEntity("ROLE_SERVICE_MONEYBANK", "Service MoneyBank", true);
                roleMoneybank.getPermissions().addAll(Set.of(pTransferCreate, pTransferRead, pAccountRead, pCardRead, pCustomerRead));

                RoleEntity roleCms = new RoleEntity("ROLE_SERVICE_CMS", "Service CMS", true);
                roleCms.getPermissions().addAll(allPerms);

                RoleEntity roleProfile = new RoleEntity("ROLE_SERVICE_PROFILE", "Service Profile", true);
                roleProfile.getPermissions().addAll(Set.of(pCustomerCreate, pCustomerRead, pAccountCreate, pAccountRead));

                RoleEntity rolePaygate = new RoleEntity("ROLE_SERVICE_PAYGATE", "Service PayGate", true);
                rolePaygate.getPermissions().addAll(Set.of(pTransferCreate, pTransferRead));

                roleRepository.saveAll(Set.of(roleTeller, roleAdmin, roleMoneybank, roleCms, roleProfile, rolePaygate));

                // 3. Create Users
                UserEntity userTeller = new UserEntity("teller1", "encoded_password_1", UserStatus.ACTIVE);
                userTeller.getRoles().add(roleTeller);

                UserEntity userAdmin = new UserEntity("admin1", "encoded_password_2", UserStatus.ACTIVE);
                userAdmin.getRoles().add(roleAdmin);

                UserEntity serviceMoneybank = new UserEntity("service_moneybank", "encoded_password_mb", UserStatus.ACTIVE);
                serviceMoneybank.getRoles().add(roleMoneybank);

                userRepository.saveAll(Set.of(userTeller, userAdmin, serviceMoneybank));
            }

            // Seed Business data (Không dùng V1)
            if (customerRepository.count() == 0) {
                dispatcher.dispatch(new CreateCustomerCommand("CIF00000001", "001099000001", "Nguyen Van A", "0901234567", "vana@demo.com"));
                dispatcher.dispatch(new CreateCustomerCommand("CIF00000002", "001099000002", "Tran Thi B", "0901234568", "thib@demo.com"));

                dispatcher.dispatch(new CreateAccountCommand("CIF00000001", "VND", new BigDecimal("10000000.00")));
                dispatcher.dispatch(new CreateAccountCommand("CIF00000001", "VND", new BigDecimal("5000000.00")));
                dispatcher.dispatch(new CreateAccountCommand("CIF00000002", "VND", new BigDecimal("5000000.00")));
            }
        };
    }
}
