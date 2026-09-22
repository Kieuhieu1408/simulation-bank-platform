package com.hieu.moneybank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hieu.moneybank", "com.hieu.common"})
@EntityScan(basePackages = {"com.hieu.moneybank", "com.hieu.common"})
@EnableJpaRepositories(basePackages = {"com.hieu.moneybank", "com.hieu.common"})
public class MoneyBankApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoneyBankApplication.class, args);
    }
}
