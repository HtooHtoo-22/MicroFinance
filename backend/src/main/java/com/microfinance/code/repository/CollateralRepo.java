package com.microfinance.code.repository;

import com.microfinance.code.model.Collateral;
import com.microfinance.code.model.SMELoanHasCollateral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollateralRepo extends JpaRepository<Collateral,Integer> {
}
