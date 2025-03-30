package com.microfinance.code.repository;

import com.microfinance.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.permissions WHERE u.email = :email")
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.permissions WHERE u.userId = :userId")
    Optional<User> findByUserId(String userId);
    @Query("SELECT COUNT(u) FROM User u WHERE u.branch.id = :branchId AND u.active = true")
    Long countActiveUsersByBranch(@Param("branchId") Integer branchId);
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    long countByActiveTrue();
    List<User> findByBranchId(Integer branchId);
}
