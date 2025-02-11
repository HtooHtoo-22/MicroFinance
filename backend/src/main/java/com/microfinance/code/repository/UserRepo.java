package com.microfinance.code.repository;

import com.microfinance.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);  // Find user by email

    boolean existsByEmail(String email);

}
