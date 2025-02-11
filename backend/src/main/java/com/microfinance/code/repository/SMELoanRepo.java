package com.microfinance.code.repository;

import com.microfinance.code.model.SMELoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SMELoanRepo extends JpaRepository<SMELoan,Integer> {
}
