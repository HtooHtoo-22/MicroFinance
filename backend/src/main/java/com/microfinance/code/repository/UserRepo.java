package com.microfinance.code.repository;

import com.microfinance.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u.userId FROM User u ORDER BY u.id DESC LIMIT 1")
    String findLastUserId();
    Optional<User> findByUserId(String userId);


    @Query("SELECT COUNT(u) FROM User u WHERE u.branch.id = :branchId AND u.active = true")
    Long countActiveUsersByBranch(@Param("branchId") Integer branchId);
}