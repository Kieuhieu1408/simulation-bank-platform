package com.hieu.corebank.config;

import com.hieu.common.constant.ActionType;
import com.hieu.common.constant.UserStatus;
import com.hieu.common.cqrs.Dispatcher;
import com.hieu.corebank.domain.Permission;
import com.hieu.corebank.domain.Role;
import com.hieu.corebank.domain.User;
import com.hieu.corebank.dto.request.AccountCreateRequestDTO;
import com.hieu.corebank.dto.request.CustomerCreateRequestDTO;
import com.hieu.corebank.repository.CustomerRepository;
import com.hieu.corebank.repository.PermissionRepository;
import com.hieu.corebank.repository.RoleRepository;
import com.hieu.corebank.repository.UserRepository;
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
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            Dispatcher dispatcher) {
        
        return args -> {
            // Seed RBAC data
            if (userRepository.count() == 0) {
                // 1. Create Permissions matching @CoreBankAuthorization menuCodes
                Permission pTransferCreate = new Permission("transfer_management", ActionType.CREATE, "Create transfer transaction");
                Permission pTransferRead   = new Permission("transfer_management", ActionType.READ, "Read transfer transactions");
                Permission pAccountCreate  = new Permission("account_management", ActionType.CREATE, "Create bank account");
                Permission pAccountRead    = new Permission("account_management", ActionType.READ, "Read account details");
                Permission pCustomerCreate = new Permission("customer_management", ActionType.CREATE, "Create customer profile");
                Permission pCustomerRead   = new Permission("customer_management", ActionType.READ, "Read customer profile");
                Permission pCardCreate     = new Permission("card_management", ActionType.CREATE, "Issue bank card");
                Permission pCardRead       = new Permission("card_management", ActionType.READ, "Read bank cards");

                Set<Permission> allPerms = Set.of(
                        pTransferCreate, pTransferRead,
                        pAccountCreate, pAccountRead,
                        pCustomerCreate, pCustomerRead,
                        pCardCreate, pCardRead
                );
                permissionRepository.saveAll(allPerms);

                // 2. Create Roles (Human & Service Roles - D17)
                Role roleTeller = new Role("TELLER", "Teller", true);
                roleTeller.getPermissions().addAll(allPerms);

                Role roleAdmin = new Role("ADMIN", "System Admin", true);
                roleAdmin.getPermissions().addAll(allPerms);

                Role roleMoneybank = new Role("ROLE_SERVICE_MONEYBANK", "Service MoneyBank", true);
                roleMoneybank.getPermissions().addAll(Set.of(pTransferCreate, pTransferRead, pAccountRead, pCardRead, pCustomerRead));

                Role roleCms = new Role("ROLE_SERVICE_CMS", "Service CMS", true);
                roleCms.getPermissions().addAll(allPerms);

                Role roleProfile = new Role("ROLE_SERVICE_PROFILE", "Service Profile", true);
                roleProfile.getPermissions().addAll(Set.of(pCustomerCreate, pCustomerRead, pAccountCreate, pAccountRead));

                Role rolePaygate = new Role("ROLE_SERVICE_PAYGATE", "Service PayGate", true);
                rolePaygate.getPermissions().addAll(Set.of(pTransferCreate, pTransferRead));

                roleRepository.saveAll(Set.of(roleTeller, roleAdmin, roleMoneybank, roleCms, roleProfile, rolePaygate));

                // 3. Create Users
                User userTeller = new User("teller1", "encoded_password_1", UserStatus.ACTIVE);
                userTeller.getRoles().add(roleTeller);

                User userAdmin = new User("admin1", "encoded_password_2", UserStatus.ACTIVE);
                userAdmin.getRoles().add(roleAdmin);

                User serviceMoneybank = new User("service_moneybank", "encoded_password_mb", UserStatus.ACTIVE);
                serviceMoneybank.getRoles().add(roleMoneybank);

                userRepository.saveAll(Set.of(userTeller, userAdmin, serviceMoneybank));
            }

            // Seed Business data (Không dùng V1)
            if (customerRepository.count() == 0) {
                dispatcher.dispatch(new CustomerCreateRequestDTO("CIF00000001", "001099000001", "Nguyen Van A", "0901234567", "vana@demo.com"));
                dispatcher.dispatch(new CustomerCreateRequestDTO("CIF00000002", "001099000002", "Tran Thi B", "0901234568", "thib@demo.com"));

                dispatcher.dispatch(new AccountCreateRequestDTO("CIF00000001", "VND", new BigDecimal("10000000.00")));
                dispatcher.dispatch(new AccountCreateRequestDTO("CIF00000001", "VND", new BigDecimal("5000000.00")));
                dispatcher.dispatch(new AccountCreateRequestDTO("CIF00000002", "VND", new BigDecimal("5000000.00")));
            }
        };
    }
}
