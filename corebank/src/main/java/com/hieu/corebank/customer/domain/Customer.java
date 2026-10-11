package com.hieu.corebank.customer.domain;

import com.hieu.corebank.shared.domain.DomainException;
import com.hieu.corebank.shared.domain.NationalId;

import java.time.Instant;
import java.util.Objects;

/**
 * Customer — Domain Entity đại diện cho Hồ sơ Khách hàng (100% thuần khiết).
 * Không phụ thuộc JPA, Hibernate hay Spring.
 */
public class Customer {

    private final String cifNumber;
    private final String nationalId;
    private final String fullName;
    private final String phoneNumber;
    private final String email;
    private final Instant createdAt;

    public Customer(String cifNumber, String nationalId, String fullName, String phoneNumber, String email, Instant createdAt) {
        if (cifNumber == null || cifNumber.trim().isEmpty()) {
            throw new DomainException("CIF number cannot be null or empty");
        }
        this.cifNumber = cifNumber.trim().toUpperCase();
        this.nationalId = nationalId != null ? nationalId.trim() : null;
        this.fullName = fullName != null ? fullName.trim() : null;
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : null;
        this.email = email != null ? email.trim() : null;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static Customer create(String cifNumber, String nationalId, String fullName, String phoneNumber, String email) {
        return new Customer(cifNumber, nationalId, fullName, phoneNumber, email, Instant.now());
    }

    public static Customer create(String cifNumber) {
        return new Customer(cifNumber, null, null, null, null, Instant.now());
    }

    public String cifNumber() {
        return cifNumber;
    }

    public String nationalId() {
        return nationalId;
    }

    public String fullName() {
        return fullName;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public String email() {
        return email;
    }

    public Instant createdAt() {
        return createdAt;
    }

    /**
     * Che mờ CCCD/Hộ chiếu tuân thủ Nghị định 13/2023/NĐ-CP (AC-CB-11, D13).
     */
    public String maskedNationalId() {
        if (nationalId == null || nationalId.isBlank()) {
            return null;
        }
        return NationalId.of(nationalId).mask();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(cifNumber, customer.cifNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cifNumber);
    }
}
