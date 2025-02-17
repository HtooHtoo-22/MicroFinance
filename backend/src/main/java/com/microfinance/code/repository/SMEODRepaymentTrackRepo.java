package com.microfinance.code.repository;

import com.microfinance.code.model.SMEODRepaymentTrack;
import com.microfinance.code.model.SMERepaymentSchedule;
import com.microfinance.code.status.RepaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SMEODRepaymentTrackRepo extends JpaRepository<SMEODRepaymentTrack,Integer> {

}
