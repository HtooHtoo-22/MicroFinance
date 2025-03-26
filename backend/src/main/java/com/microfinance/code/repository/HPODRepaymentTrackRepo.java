package com.microfinance.code.repository;

import com.microfinance.code.model.HPODRepaymentTrack;
import com.microfinance.code.model.SMEODRepaymentTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HPODRepaymentTrackRepo extends JpaRepository<HPODRepaymentTrack,Integer> {
    List<HPODRepaymentTrack> findByHpRepaymentSchedule_HpLoan_Id(Integer hpLoanId);

}
