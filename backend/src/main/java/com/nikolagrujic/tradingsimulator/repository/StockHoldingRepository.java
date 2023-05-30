package com.nikolagrujic.tradingsimulator.repository;

import com.nikolagrujic.tradingsimulator.model.StockHolding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockHoldingRepository extends JpaRepository<StockHolding,Long> {
    Page<StockHolding> getAllByPortfolio_User_Email(Pageable pageable, @Param("email") String email);
}