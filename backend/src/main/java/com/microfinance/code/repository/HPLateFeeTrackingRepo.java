package com.microfinance.code.repository;

import com.microfinance.code.model.HPLateFeeTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HPLateFeeTrackingRepo extends JpaRepository<HPLateFeeTracking,Integer> {
}
