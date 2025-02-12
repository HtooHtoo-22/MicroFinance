package com.microfinance.code.repository;

import com.microfinance.code.model.SMELoan;
import com.microfinance.code.model.SMELoanHasCollateral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SMELoanHasCollateralRepo extends JpaRepository<SMELoanHasCollateral,Integer> {
}
