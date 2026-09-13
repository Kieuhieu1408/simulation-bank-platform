package com.hieu.corebank;

import org.flywaydb.core.Flyway;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class CleanDb {
    public static void main(String[] args) throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:oracle:thin:@localhost:1521/FREEPDB1");
        config.setUsername("corebank");
        config.setPassword("corebank_demo");
        config.setDriverClassName("oracle.jdbc.OracleDriver");
        
        try (HikariDataSource ds = new HikariDataSource(config)) {
            Flyway flyway = Flyway.configure().cleanDisabled(false).dataSource(ds).load();
            flyway.clean();
            System.out.println("Flyway clean executed successfully!");
        }
    }
}
