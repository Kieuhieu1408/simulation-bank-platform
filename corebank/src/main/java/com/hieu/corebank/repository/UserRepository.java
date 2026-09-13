package com.hieu.corebank.repository;

import com.hieu.corebank.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
