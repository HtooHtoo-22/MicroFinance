package com.microfinance.code.repository;

import com.microfinance.code.model.HPLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HPLoanRepo extends JpaRepository<HPLoan,Integer> {
}
