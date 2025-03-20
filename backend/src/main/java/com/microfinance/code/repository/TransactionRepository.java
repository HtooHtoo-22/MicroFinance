package com.microfinance.code.repository;

import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByCurrentAccountIdAndDate(CurrentAccount currentAccount, LocalDateTime date);
    @Query("SELECT t FROM Transaction t WHERE t.currentAccountId = :accountId AND FUNCTION('DATE', t.date) = :date")
    List<Transaction> findByCurrentAccountIdAndDate(@Param("accountId") CurrentAccount accountId, @Param("date") LocalDate date);
    @Query("SELECT t FROM Transaction t WHERE t.currentAccountId.accountId = :currentAccountId")
    List<Transaction> findByCurrentAccountId(@Param("currentAccountId") String currentAccountId);

    List<Transaction> findByCurrentAccountIdIn(List<CurrentAccount> currentAccounts);

    Optional<Transaction> findById(Integer id);
}
