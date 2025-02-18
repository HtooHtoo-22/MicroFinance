package com.microfinance.code.repository;

import com.microfinance.code.model.SMERepaymentSchedule;
import com.microfinance.code.status.RepaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SMERepaymentScheduleRepo extends JpaRepository<SMERepaymentSchedule,Integer> {
    List<SMERepaymentSchedule> findByDueDate(LocalDate dueDate);
    List<SMERepaymentSchedule> findByDueDateOrGracePeriodEndDate(LocalDate dueDate , LocalDate gracePeriodEndDate);
    List<SMERepaymentSchedule> findByStatusIn(List<RepaymentStatus> statuses);
}
