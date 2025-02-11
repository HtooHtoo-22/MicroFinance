package com.microfinance.code.repository;

import com.microfinance.code.model.Collateral;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollateralRepo extends JpaRepository<Collateral,Integer> {
}
