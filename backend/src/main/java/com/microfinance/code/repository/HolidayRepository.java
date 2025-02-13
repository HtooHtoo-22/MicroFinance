package com.microfinance.code.repository;

import com.microfinance.code.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Integer> {

    boolean existsByHolidayDateAndName(LocalDate holidayDate, String name);
}

