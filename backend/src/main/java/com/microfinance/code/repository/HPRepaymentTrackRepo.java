package com.microfinance.code.repository;

import com.microfinance.code.model.HPRepaymentTrack;
import com.microfinance.code.model.SMERepaymentTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HPRepaymentTrackRepo extends JpaRepository<HPRepaymentTrack,Integer> {
    List<HPRepaymentTrack> findByHpSchedule_HpLoan_Id(Integer hpLoanId);
}
