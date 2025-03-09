package com.microfinance.code.repository;

import com.microfinance.code.model.Dealer;
import com.microfinance.code.status.DEALER;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealerRepo extends JpaRepository<Dealer, Integer> {
    Optional<Dealer> findByEmail(String email);
    List<Dealer> findByStatusforDelar(DEALER status);
}
