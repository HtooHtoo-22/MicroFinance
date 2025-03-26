package com.microfinance.code.repository;

import com.microfinance.code.model.HPLateFeeTracking;
import com.microfinance.code.model.SMELateFeeTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HPLateFeeTrackingRepo extends JpaRepository<HPLateFeeTracking,Integer> {
    List<HPLateFeeTracking> findByHpLoanId(Integer loanId);
}
