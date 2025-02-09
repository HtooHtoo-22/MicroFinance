package com.microfinance.code.repository;

import com.microfinance.code.model.CollateralType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollateralTypeRepo extends JpaRepository<CollateralType,Integer> {
    List<CollateralType> findByStatusTrue();
    List<CollateralType> findByStatus(boolean status);
}
