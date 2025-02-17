package com.microfinance.code.repository;

import com.microfinance.code.model.CIF;
import com.microfinance.code.model.Dealer;
import com.microfinance.code.status.CIFStatus;
import com.microfinance.code.status.DealerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealerRepo extends JpaRepository<Dealer, Integer> {
    Optional<Dealer> findByEmail(String email);
    Optional<Dealer>findByBusinessName(String businessName);
    Optional<Dealer> findByCurrentAccountId(Integer currentAccountId);
    List<Dealer> findByStatus(DealerStatus status);
}
