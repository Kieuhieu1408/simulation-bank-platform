package com.hieu.corebank.repository;

import com.hieu.corebank.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    boolean existsByCifNumber(String cifNumber);
}
