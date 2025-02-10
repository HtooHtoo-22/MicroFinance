package com.microfinance.code.repository;

import com.microfinance.code.model.CIF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CIFRepo extends JpaRepository<CIF,Integer> {
}
