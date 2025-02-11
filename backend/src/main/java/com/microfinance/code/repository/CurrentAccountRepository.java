package com.microfinance.code.repository;

import com.microfinance.code.model.CurrentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Integer> {

    @Query(value = "SELECT MAX(CAST(SUBSTRING(account_id, 7) AS UNSIGNED)) FROM current_account WHERE cif_id = :cifId", nativeQuery = true)
    Integer findMaxAccountNumberByCifId(@Param("cifId") Integer cifId);

    Optional<CurrentAccount> findByAccountId(String accountId);
}