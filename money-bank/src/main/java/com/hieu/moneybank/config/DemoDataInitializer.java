package com.hieu.moneybank.config;

import com.hieu.common.cqrs.Dispatcher;
import com.hieu.moneybank.constant.ActionType;
import com.hieu.moneybank.constant.UserStatus;
import com.hieu.moneybank.domain.Permission;
import com.hieu.moneybank.domain.Role;
import com.hieu.moneybank.domain.User;
import com.hieu.moneybank.dto.request.AccountCreateRequestDTO;
import com.hieu.moneybank.dto.request.CustomerCreateRequestDTO;
import com.hieu.moneybank.repository.AccountRepository;
import com.hieu.moneybank.repository.PermissionRepository;
import com.hieu.moneybank.repository.RoleRepository;
import com.hieu.moneybank.repository.UserRepository;
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
            AccountRepository accountRepository, 
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            Dispatcher dispatcher) {
        
        return args -> {
            // Seed RBAC data
            if (userRepository.count() == 0) {
                // 1. Create Permissions
                Permission pTransferCreate = new Permission("Transfer", ActionType.CREATE, "Create transfer transaction");
                Permission pTransferRead = new Permission("Transfer", ActionType.READ, "Read transfer transactions");
                Permission pAccountRead = new Permission("Account", ActionType.READ, "Read account details");
                
                permissionRepository.saveAll(Set.of(pTransferCreate, pTransferRead, pAccountRead));

                // 2. Create Roles
                Role roleTeller = new Role("TELLER", "Teller", true);
                roleTeller.getPermissions().addAll(Set.of(pTransferCreate, pTransferRead, pAccountRead));
                
                Role roleAdmin = new Role("ADMIN", "System Admin", true);
                roleAdmin.getPermissions().addAll(Set.of(pTransferCreate, pTransferRead, pAccountRead));

                roleRepository.saveAll(Set.of(roleTeller, roleAdmin));

                // 3. Create Users
                User userTeller = new User("teller1", "encoded_password_1", UserStatus.ACTIVE);
                userTeller.getRoles().add(roleTeller);

                User userAdmin = new User("admin1", "encoded_password_2", UserStatus.ACTIVE);
                userAdmin.getRoles().add(roleAdmin);

                userRepository.saveAll(Set.of(userTeller, userAdmin));
            }

            // Seed Business data
            if (accountRepository.count() == 0) {
                dispatcher.dispatch(new CustomerCreateRequestDTO("CIF00000001"));
                dispatcher.dispatch(new CustomerCreateRequestDTO("CIF00000002"));

                dispatcher.dispatch(new AccountCreateRequestDTO("CIF00000001", "VND", new BigDecimal("10000000.00")));
                dispatcher.dispatch(new AccountCreateRequestDTO("CIF00000001", "VND", new BigDecimal("5000000.00")));
                dispatcher.dispatch(new AccountCreateRequestDTO("CIF00000002", "VND", new BigDecimal("5000000.00")));
            }
        };
    }
}
