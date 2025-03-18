package com.microfinance.code.repository;

import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.SMELoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SMELoanRepo extends JpaRepository<SMELoan,Integer> {
    Optional<SMELoan> findByLoanId(String loanId);

    @Query("SELECT s FROM SMELoan s LEFT JOIN FETCH s.currentAccount WHERE s.id = :id")
    Optional<SMELoan> findByIdWithQuery(@Param("id") Integer id);

    @Query("SELECT s.currentAccount FROM SMELoan s WHERE s.id = :smeLoanId")
    Optional<CurrentAccount> findCurrentAccountBySmeLoanId(@Param("smeLoanId") Integer smeLoanId);

    List<SMELoan> findByEntryUser_Branch_Id(Integer branchId);

}
