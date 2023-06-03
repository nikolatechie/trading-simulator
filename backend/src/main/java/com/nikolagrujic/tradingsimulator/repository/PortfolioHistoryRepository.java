package com.nikolagrujic.tradingsimulator.repository;

import com.nikolagrujic.tradingsimulator.model.PortfolioHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PortfolioHistoryRepository extends JpaRepository<PortfolioHistory,Long> {
    PortfolioHistory findByPortfolio_IdAndDate(Long portfolioId, LocalDate date);
    List<PortfolioHistory> findByPortfolio_User_EmailOrderByDateAsc(String email);
    PortfolioHistory findByPortfolio_User_EmailAndDate(String userEmail, LocalDate date);
}