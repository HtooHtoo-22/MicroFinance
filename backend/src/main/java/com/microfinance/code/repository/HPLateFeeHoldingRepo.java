package com.microfinance.code.repository;

import com.microfinance.code.model.HPLateFeeHolding;
import com.microfinance.code.model.SMELateFeeHolding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HPLateFeeHoldingRepo extends JpaRepository<HPLateFeeHolding,Integer> {
    Optional<HPLateFeeHolding> findByHpLoan_Id(Integer hpLoanId);
}
