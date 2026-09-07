package com.hieu.corebank.config;

import com.hieu.corebank.domain.Account;
import com.hieu.corebank.domain.Customer;
import com.hieu.corebank.repository.AccountRepository;
import com.hieu.corebank.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.math.BigDecimal;

@Configuration
public class DemoDataInitializer {
    @Bean
    CommandLineRunner demoData(AccountRepository accounts, CustomerRepository customers) {
        return args -> {
            if (accounts.count() == 0) {
                Customer cifOne = customers.save(new Customer("CIF00000001"));
                Customer cifTwo = customers.save(new Customer("CIF00000002"));
                accounts.save(new Account("100000000001", cifOne, "VND", new BigDecimal("10000000.00")));
                accounts.save(new Account("100000000002", cifOne, "VND", new BigDecimal("5000000.00")));
                accounts.save(new Account("100000000003", cifTwo, "VND", new BigDecimal("5000000.00")));
            }
        };
    }
}
