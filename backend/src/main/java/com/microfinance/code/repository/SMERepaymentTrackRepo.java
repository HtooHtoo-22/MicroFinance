package com.microfinance.code.repository;

import com.microfinance.code.model.SMERepaymentTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SMERepaymentTrackRepo extends JpaRepository<SMERepaymentTrack,Integer> {
    List<SMERepaymentTrack> findBySmeRepaymentSchedule_SmeLoan_Id(Integer smeLoanId);
}
