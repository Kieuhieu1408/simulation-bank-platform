package com.hieu.moneybank.repository;

import com.hieu.moneybank.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
