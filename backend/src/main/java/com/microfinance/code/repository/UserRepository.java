package com.microfinance.code.repository;

import com.microfinance.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<Object> findByEmail(String userEmail);
}
