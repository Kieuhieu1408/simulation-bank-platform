package com.hieu.corebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hieu.corebank", "com.hieu.common"})
@EntityScan(basePackages = {"com.hieu.corebank", "com.hieu.common"})
@EnableJpaRepositories(basePackages = {"com.hieu.corebank", "com.hieu.common"})
public class CorebankApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorebankApplication.class, args);
    }

}
