package com.hieu.corebank.customer.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, String> {
    boolean existsByCifNumber(String cifNumber);
    boolean existsByNationalId(String nationalId);
    Optional<CustomerEntity> findByNationalId(String nationalId);
}
