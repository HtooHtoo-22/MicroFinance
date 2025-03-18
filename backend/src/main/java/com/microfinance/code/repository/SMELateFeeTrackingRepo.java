package com.microfinance.code.repository;

import com.microfinance.code.model.SMELateFeeTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SMELateFeeTrackingRepo  extends JpaRepository<SMELateFeeTracking,Integer> {
    List<SMELateFeeTracking> findBySmeLoanId(Integer loanId);
}
