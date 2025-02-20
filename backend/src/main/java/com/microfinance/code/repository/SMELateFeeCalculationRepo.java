package com.microfinance.code.repository;

import com.microfinance.code.model.SMELateFeeCalculation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SMELateFeeCalculationRepo extends JpaRepository<SMELateFeeCalculation, Integer> {

    // Get total late fees grouped by SME Loan
    @Query("SELECT s.smeRepaymentSchedule.smeLoan.id, SUM(s.lateFees) " +
            "FROM SMELateFeeCalculation s " +
            "GROUP BY s.smeRepaymentSchedule.smeLoan.id")
    List<Object[]> findTotalLateFeesGroupedByLoan();

    // Retrieve all late fee records by SME Loan ID
    @Query("SELECT s FROM SMELateFeeCalculation s WHERE s.smeRepaymentSchedule.smeLoan.id = :smeLoanId")
    List<SMELateFeeCalculation> findBySmeLoanId(Integer smeLoanId);

    // Get distinct SME Loan IDs from the late fee calculation table
    @Query("SELECT DISTINCT s.smeRepaymentSchedule.smeLoan.id FROM SMELateFeeCalculation s")
    List<Integer> findDistinctSmeLoanIds();

    @Modifying
    @Transactional
    @Query("DELETE FROM SMELateFeeCalculation s WHERE s.smeRepaymentSchedule.smeLoan.id = :smeLoanId")
    void deleteBySmeLoanId(@Param("smeLoanId") Integer smeLoanId);


}
