package com.microfinance.code.repository;

import com.microfinance.code.model.HPSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HPScheduleRepo extends JpaRepository<HPSchedule,Integer> {
}
