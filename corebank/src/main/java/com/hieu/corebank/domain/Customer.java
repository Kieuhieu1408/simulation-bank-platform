package com.hieu.corebank.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_customer_cif", columnNames = "cif_number"),
        @UniqueConstraint(name = "uk_customer_national_id", columnNames = "national_id")
})
public class Customer {

    @Id
    @Column(name = "cif_number", nullable = false, length = 20)
    private String cifNumber;

    @Column(name = "national_id", length = 20)
    private String nationalId;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Customer() {}

    public Customer(String cifNumber) {
        this.cifNumber = cifNumber;
        this.createdAt = Instant.now();
    }

    public Customer(String cifNumber, String nationalId, String fullName, String phoneNumber, String email) {
        this.cifNumber = cifNumber;
        this.nationalId = nationalId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.createdAt = Instant.now();
    }
}
