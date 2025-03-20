package com.microfinance.code.repository;

import com.microfinance.code.model.HPRepaymentTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HPRepaymentTrackRepo extends JpaRepository<HPRepaymentTrack,Integer> {
}
