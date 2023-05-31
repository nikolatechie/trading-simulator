package com.nikolagrujic.tradingsimulator.repository;

import com.nikolagrujic.tradingsimulator.model.StockHolding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockHoldingRepository extends JpaRepository<StockHolding,Long> {
    List<StockHolding> getAllByPortfolio_User_Email(String email); // No pagination
    Page<StockHolding> getAllByPortfolio_User_Email(Pageable pageable, String email); // Pagination
    List<StockHolding> findDistinctBySymbolNotNull();
}