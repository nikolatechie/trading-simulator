package com.nikolagrujic.tradingsimulator.service;

import com.nikolagrujic.tradingsimulator.model.Portfolio;
import com.nikolagrujic.tradingsimulator.model.StockHolding;
import com.nikolagrujic.tradingsimulator.model.User;
import com.nikolagrujic.tradingsimulator.repository.PortfolioRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioService.class);

    public void createUserPortfolio(User user) {
        Portfolio portfolio = new Portfolio();
        portfolio.setUser(user);
        portfolioRepository.save(portfolio);
    }

    public BigDecimal getAvailableCash() {
        // Retrieve available cash for the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        LOGGER.info("Retrieving available cash for user {}", userEmail);
        return portfolioRepository.getByUser_Email(userEmail).getCash();
    }

    public int getQuantity(String symbol) {
        // Retrieve quantity of the specified stock that user has
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        LOGGER.info("Retrieving quantity of {} for user {}", symbol, userEmail);
        Portfolio portfolio = portfolioRepository.getByUser_Email(userEmail);
        for (StockHolding stockHolding: portfolio.getStockHoldings()) {
            if (stockHolding.getSymbol().equals(symbol)) {
                return stockHolding.getQuantity();
            }
        }
        return 0;
    }

    public Portfolio getByUserEmail(String userEmail) {
        return portfolioRepository.getByUser_Email(userEmail);
    }

    public Portfolio getByUserEmailAndLocked(String email, boolean locked) {
        return portfolioRepository.getByUser_EmailAndLocked(email, locked);
    }

    public Portfolio save(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }
}