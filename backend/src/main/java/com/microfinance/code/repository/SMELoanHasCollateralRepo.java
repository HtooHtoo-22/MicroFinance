package com.microfinance.code.repository;

import com.microfinance.code.model.Collateral;
import com.microfinance.code.model.SMELoan;
import com.microfinance.code.model.SMELoanHasCollateral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SMELoanHasCollateralRepo extends JpaRepository<SMELoanHasCollateral,Integer> {
    SMELoanHasCollateral findByCollateral(Collateral collateral);
    @Query("SELECT SUM(shc.usedValue) FROM SMELoanHasCollateral shc WHERE shc.collateral.id = :collateralId")
    BigDecimal findTotalUsedValueByCollateralId(@Param("collateralId") Integer collateralId);
    List<SMELoanHasCollateral> findBySmeLoan(SMELoan smeLoan);

    @Query("SELECT DISTINCT shc.smeLoan.loanId FROM SMELoanHasCollateral shc WHERE shc.collateral.id = :collateralId")
    List<String> getSMELoanIdListUsedByThisCollateral(@Param("collateralId") Integer collateralId);
}
