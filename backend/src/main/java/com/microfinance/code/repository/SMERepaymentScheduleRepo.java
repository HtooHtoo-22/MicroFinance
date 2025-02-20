package com.microfinance.code.repository;

import com.microfinance.code.model.SMERepaymentSchedule;
import com.microfinance.code.status.RepaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SMERepaymentScheduleRepo extends JpaRepository<SMERepaymentSchedule,Integer> {
    List<SMERepaymentSchedule> findByDueDate(LocalDate dueDate);
    List<SMERepaymentSchedule> findByDueDateOrGracePeriodEndDate(LocalDate dueDate , LocalDate gracePeriodEndDate);
    List<SMERepaymentSchedule> findByStatusInAndLateFeeStatus(List<RepaymentStatus> statuses, boolean lateFeeStatus);

    @Query("SELECT s FROM SMERepaymentSchedule s WHERE s.smeLoan.id = :smeLoanId AND s.status IN :statuses")
    List<SMERepaymentSchedule> findBySmeLoanIdAndStatusIn(
            @Param("smeLoanId") Integer smeLoanId,
            @Param("statuses") List<RepaymentStatus> statuses);

}
