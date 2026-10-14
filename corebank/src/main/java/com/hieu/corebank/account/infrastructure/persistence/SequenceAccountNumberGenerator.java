package com.hieu.corebank.account.infrastructure.persistence;

import com.hieu.corebank.account.domain.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SequenceAccountNumberGenerator implements AccountNumberGenerator {

    private final JdbcClient jdbc;

    @Override
    public String nextAccountNumber() {
        long seq = jdbc.sql("SELECT account_number_seq.NEXTVAL FROM DUAL")
                .query(Long.class)
                .single();
        return "10" + String.format("%010d", seq);
    }
}
