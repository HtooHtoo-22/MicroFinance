package com.microfinance.code.repository;

import com.microfinance.code.model.HPODRepaymentTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HPODRepaymentTrackRepo extends JpaRepository<HPODRepaymentTrack,Integer> {
}
