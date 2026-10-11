package com.hieu.corebank.customer.domain;

import java.util.Optional;

/**
 * CustomerRepository — Port lưu trữ và truy xuất Customer.
 */
public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(String cifNumber);

    boolean existsByCifNumber(String cifNumber);

    boolean existsByNationalId(String nationalId);

    Optional<Customer> findByNationalId(String nationalId);

    long count();
}
