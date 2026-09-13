package com.hieu.corebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hieu.corebank", "com.hieu.common"})
public class CorebankApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorebankApplication.class, args);
    }

}
