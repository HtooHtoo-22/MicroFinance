package com.microfinance.code.repository;

import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.SMELoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Integer> {
    Optional<CurrentAccount> findByAccountId(String accountId);
    List<CurrentAccount> findByCif_Id(Integer cifId);
    @Query("SELECT COUNT(ca) FROM CurrentAccount ca WHERE ca.cif.branch.id = :branchId")
    long countByBranchId(@Param("branchId") Integer branchId);
    boolean existsByCifId(Integer cifId); // Add this method

}