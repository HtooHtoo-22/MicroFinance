package com.microfinance.code.repository;


import com.microfinance.code.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> {
    List<Product> findByDealerId(Integer dealerId); // Custom JPA Query
}
