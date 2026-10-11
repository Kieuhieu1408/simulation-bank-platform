package com.hieu.corebank.customer.infrastructure.persistence;

import com.hieu.corebank.customer.domain.CifGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

/**
 * SequenceCifGenerator — Hiện thực CifGenerator port bằng Database Sequence (D14, AC-CB-01).
 * Sinh mã CIF chuẩn 8 số: 00000001 - 99999999.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SequenceCifGenerator implements CifGenerator {

    private final JdbcClient jdbc;

    @Override
    public String nextCif() {
        Long seq = jdbc.sql("SELECT cif_seq.NEXTVAL FROM DUAL")
                .query(Long.class)
                .single();
        String cif = String.format("%08d", seq);
        log.debug("Generated CIF: {}", cif);
        return cif;
    }
}
