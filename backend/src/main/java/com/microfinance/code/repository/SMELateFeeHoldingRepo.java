package com.microfinance.code.repository;

import com.microfinance.code.model.SMELateFeeHolding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SMELateFeeHoldingRepo extends JpaRepository<SMELateFeeHolding,Integer> {

    Optional<SMELateFeeHolding> findBySmeLoan_Id(Integer smeLoanId);
}
