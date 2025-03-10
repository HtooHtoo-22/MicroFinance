package com.microfinance.code.repository;

import com.microfinance.code.model.CIF;
import com.microfinance.code.status.CIFStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CIFRepo extends JpaRepository<CIF ,Integer>{

    @Query("SELECT c FROM CIF c JOIN FETCH c.branch JOIN FETCH c.user")
    List<CIF> findByStatus(CIFStatus status);
    boolean existsByNRC(String nrc);
    boolean existsByEmail(String email);
    boolean existsByNRCAndIdNot(String nrc, Integer id);
    boolean existsByEmailAndIdNot(String email, Integer id);
    List<CIF> findByBranchId(Integer branchId);
}
