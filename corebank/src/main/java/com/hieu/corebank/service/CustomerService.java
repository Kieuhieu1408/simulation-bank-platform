package com.hieu.corebank.service;

import com.hieu.corebank.api.dto.CustomerDtos;
import com.hieu.corebank.domain.Customer;
import com.hieu.corebank.exception.BusinessException;
import com.hieu.corebank.exception.NotFoundException;
import com.hieu.corebank.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {
    private final CustomerRepository customers;

    public CustomerService(CustomerRepository customers) { this.customers = customers; }

    @Transactional
    public Customer create(CustomerDtos.CreateRequest request) {
        String cifNumber = request.cifNumber().toUpperCase();
        if (customers.existsByCifNumber(cifNumber)) throw new BusinessException("CIF already exists: " + cifNumber);
        return customers.save(new Customer(cifNumber));
    }

    @Transactional(readOnly = true)
    public Customer get(String cifNumber) {
        return customers.findById(cifNumber.toUpperCase())
                .orElseThrow(() -> new NotFoundException("CIF not found: " + cifNumber));
    }
}
