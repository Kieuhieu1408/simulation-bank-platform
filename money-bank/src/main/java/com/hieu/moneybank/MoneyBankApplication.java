package com.hieu.moneybank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MoneyBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyBankApplication.class, args);
    }

}
