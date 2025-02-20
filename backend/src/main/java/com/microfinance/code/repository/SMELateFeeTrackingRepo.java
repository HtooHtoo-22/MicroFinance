package com.microfinance.code.repository;

import com.microfinance.code.model.SMELateFeeTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SMELateFeeTrackingRepo  extends JpaRepository<SMELateFeeTracking,Integer> {
}
