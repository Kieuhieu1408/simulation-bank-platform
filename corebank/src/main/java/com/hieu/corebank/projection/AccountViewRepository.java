package com.hieu.corebank.projection;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountViewRepository extends JpaRepository<AccountView, String> {
    List<AccountView> findByCustomerIdOrderByLastUpdatedAtDesc(String customerId);
}