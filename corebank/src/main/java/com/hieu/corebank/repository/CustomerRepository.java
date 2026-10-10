package com.hieu.corebank.repository;

import com.hieu.corebank.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    boolean existsByCifNumber(String cifNumber);
    boolean existsByNationalId(String nationalId);
    Optional<Customer> findByNationalId(String nationalId);
}
