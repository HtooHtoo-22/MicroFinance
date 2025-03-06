package com.microfinance.code.repository;

import com.microfinance.code.model.HPLateFeeCalculation;
import com.microfinance.code.model.HPLoan;
import com.microfinance.code.model.HPSchedule;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HPLateFeeCalculationRepo extends JpaRepository<HPLateFeeCalculation,Integer> {
    // Get total late fees grouped by HP Loan ID
    @Query("SELECT h.hpRepaymentSchedule.hpLoan.id, SUM(h.interestLateFee + h.principalLateFee) " +
            "FROM HPLateFeeCalculation h " +
            "GROUP BY h.hpRepaymentSchedule.hpLoan.id")
    List<Object[]> findTotalLateFeesGroupedByLoan();

    // Retrieve all late fee records by HP Loan ID
    @Query("SELECT h FROM HPLateFeeCalculation h WHERE h.hpRepaymentSchedule.hpLoan.id = :hpLoanId")
    List<HPLateFeeCalculation> findByHpLoanId(@Param("hpLoanId") Integer hpLoanId);

    // Get distinct HP Loan IDs from the late fee calculation table
    @Query("SELECT DISTINCT h.hpRepaymentSchedule.hpLoan.id FROM HPLateFeeCalculation h")
    List<Integer> findDistinctHpLoanIds();

    // Delete all late fee records by HP Loan ID
    @Modifying
    @Transactional
    @Query("DELETE FROM HPLateFeeCalculation h WHERE h.hpRepaymentSchedule.hpLoan.id = :hpLoanId")
    void deleteByHpLoanId(@Param("hpLoanId") Integer hpLoanId);

    // Delete old late fees where lateDays < 91
    @Modifying
    @Query("DELETE FROM HPLateFeeCalculation h WHERE h.lateDays < 91 AND h.hpRepaymentSchedule.hpLoan = :hpLoan")
    void deleteOldLateFeesBySchedule(@Param("hpLoan") HPLoan hpLoan);

    Optional<HPLateFeeCalculation> findByHpRepaymentSchedule(HPSchedule schedule);
}
