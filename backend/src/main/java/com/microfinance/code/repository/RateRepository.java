package com.microfinance.code.repository;

import com.microfinance.code.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RateRepository extends JpaRepository<Rate, Integer> {
    Rate findByRateType(String rateType);

    @Query("SELECT r.value FROM Rate r WHERE r.rateType = :rateType")
    Double findValueByRateType(@Param("rateType") String rateType);


}