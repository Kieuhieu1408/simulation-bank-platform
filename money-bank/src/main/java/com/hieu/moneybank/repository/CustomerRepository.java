package com.hieu.moneybank.repository;

import com.hieu.moneybank.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    boolean existsByCifNumber(String cifNumber);
}
