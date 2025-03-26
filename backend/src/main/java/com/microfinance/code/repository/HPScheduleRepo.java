package com.microfinance.code.repository;

import com.microfinance.code.model.HPLoan;
import com.microfinance.code.model.HPSchedule;
import com.microfinance.code.model.SMELoan;
import com.microfinance.code.model.SMERepaymentSchedule;
import com.microfinance.code.status.RepaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface HPScheduleRepo extends JpaRepository<HPSchedule,Integer> {
    List<HPSchedule> findByDueDate(LocalDate dueDate);

    @Query("SELECT h FROM HPSchedule h WHERE (h.dueDate = :date OR h.gracePeriodEndDate = :date) AND h.status IN :statuses")
    List<HPSchedule> findByDueDateOrGracePeriodEndDateAndStatusIn(
            @Param("date") LocalDate date,
            @Param("statuses") List<RepaymentStatus> statuses
    );


    List<HPSchedule> findByStatusInAndLateFeeStatus(List<RepaymentStatus> statuses, boolean lateFeeStatus);

    @Query("SELECT h FROM HPSchedule h WHERE h.hpLoan.id = :hpLoanId AND h.status IN :statuses")
    List<HPSchedule> findByHPLoanIdAndStatusIn(
            @Param("hpLoanId") Integer smeLoanId,
            @Param("statuses") List<RepaymentStatus> statuses);

    List<HPSchedule> findByHpLoanAndStatus(HPLoan hpLoan, RepaymentStatus status);

    List<HPSchedule> findByHpLoanId(Integer hpLoanId);
}
