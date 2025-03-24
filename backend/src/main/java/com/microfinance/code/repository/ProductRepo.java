package com.microfinance.code.repository;


import com.microfinance.code.model.Dealer;
import com.microfinance.code.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> {
    List<Product> findByDealerId(Integer dealerId); // Custom JPA Query
//    @Query("SELECT p FROM Product p JOIN p.dealer d WHERE d.branch.id = :branchId")
//    List<Product> findByDealerBranchId(@Param("branchId") Integer branchId);



    List<Product> findByDealer(Dealer dealer);
}
