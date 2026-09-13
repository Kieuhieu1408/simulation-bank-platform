package com.hieu.corebank.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "customers", uniqueConstraints = @UniqueConstraint(name = "uk_customer_cif", columnNames = "cif_number"))
public class Customer {

    @Id
    @Column(name = "cif_number", nullable = false, length = 20)
    private String cifNumber;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Customer() {}

    public Customer(String cifNumber) {
        this.cifNumber = cifNumber;
        this.createdAt = Instant.now();
    }
}
