package com.microfinance.code.repository;

import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.HPLoan;
import com.microfinance.code.model.SMELoan;
import com.microfinance.code.status.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HPLoanRepo extends JpaRepository<HPLoan,Integer> {
    Optional<HPLoan> findByLoanId(String loanId);

    @Query("SELECT h FROM HPLoan h LEFT JOIN FETCH h.currentAccount WHERE h.id = :id")
    Optional<HPLoan> findByIdWithQuery(@Param("id") Integer id);

    @Query("SELECT h.currentAccount FROM HPLoan h WHERE h.id = :hpLoanId")
    Optional<CurrentAccount> findCurrentAccountByHpLoanId(@Param("hpLoanId") Integer hpLoanId);
    List<HPLoan> findAll();

    List<HPLoan> findByStatus(LoanStatus loanStatus);
    List<HPLoan> findByEntryUser_Branch_Id(Integer branchId);

    List<HPLoan> findByApprovedDateBetweenAndStatus(LocalDateTime localDateTime, LocalDateTime localDateTime1, LoanStatus loanStatus);
}
