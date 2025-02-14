package com.microfinance.code.repository;

import com.microfinance.code.model.SMERepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SMERepaymentScheduleRepo extends JpaRepository<SMERepaymentSchedule,Integer> {
}
