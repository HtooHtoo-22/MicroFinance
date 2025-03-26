package com.microfinance.code.repository;

import com.microfinance.code.model.CIF;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.Dealer;
import com.microfinance.code.status.CIFStatus;
import com.microfinance.code.status.DEALER;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealerRepo extends JpaRepository<Dealer, Integer> {
    Optional<Dealer> findByEmail(String email); // Required for email-based lookup
    Optional<Dealer>findByBusinessName(String businessName);
    Optional<Dealer> findByCurrentAccountId(Integer currentAccountId);
    List<Dealer> findByStatusforDelar(DEALER dealer);

    @Query("SELECT d.currentAccount FROM Dealer d WHERE d.id = :dealerId")
    Optional<CurrentAccount> findCurrentAccountByDealerId(Integer dealerId);
}
