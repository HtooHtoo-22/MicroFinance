package com.microfinance.code.repository;

import com.microfinance.code.model.CIF;
import com.microfinance.code.status.CIFStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CIFRepo extends JpaRepository<CIF ,Integer>{
    List<CIF> findByStatus(CIFStatus status);
}
