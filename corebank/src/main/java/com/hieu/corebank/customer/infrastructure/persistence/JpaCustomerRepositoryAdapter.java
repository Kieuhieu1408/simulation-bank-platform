package com.hieu.corebank.customer.infrastructure.persistence;

import com.hieu.corebank.customer.domain.Customer;
import com.hieu.corebank.customer.domain.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class JpaCustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = CustomerEntity.fromDomain(customer);
        CustomerEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Customer> findById(String cifNumber) {
        return jpaRepository.findById(cifNumber).map(CustomerEntity::toDomain);
    }

    @Override
    public boolean existsByCifNumber(String cifNumber) {
        return jpaRepository.existsByCifNumber(cifNumber);
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        return jpaRepository.existsByNationalId(nationalId);
    }

    @Override
    public Optional<Customer> findByNationalId(String nationalId) {
        return jpaRepository.findByNationalId(nationalId).map(CustomerEntity::toDomain);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
