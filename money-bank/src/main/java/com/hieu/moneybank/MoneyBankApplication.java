package com.hieu.moneybank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hieu.moneybank", "com.hieu.common"})
public class MoneyBankApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoneyBankApplication.class, args);
    }
}
