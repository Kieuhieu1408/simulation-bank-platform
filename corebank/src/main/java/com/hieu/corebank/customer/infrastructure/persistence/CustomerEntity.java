package com.hieu.corebank.customer.infrastructure.persistence;

import com.hieu.corebank.customer.domain.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_customer_cif", columnNames = "cif_number"),
        @UniqueConstraint(name = "uk_customer_national_id", columnNames = "national_id")
})
public class CustomerEntity {

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

    public static CustomerEntity fromDomain(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setCifNumber(customer.cifNumber());
        entity.setNationalId(customer.nationalId());
        entity.setFullName(customer.fullName());
        entity.setPhoneNumber(customer.phoneNumber());
        entity.setEmail(customer.email());
        entity.setCreatedAt(customer.createdAt() != null ? customer.createdAt() : Instant.now());
        return entity;
    }

    public Customer toDomain() {
        return new Customer(
                this.cifNumber,
                this.nationalId,
                this.fullName,
                this.phoneNumber,
                this.email,
                this.createdAt
        );
    }
}
