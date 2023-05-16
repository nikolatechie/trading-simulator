package com.nikolagrujic.tradingsimulator.service;

import com.nikolagrujic.tradingsimulator.model.Portfolio;
import com.nikolagrujic.tradingsimulator.model.StockHolding;
import com.nikolagrujic.tradingsimulator.model.User;
import com.nikolagrujic.tradingsimulator.repository.PortfolioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;

    public void createUserPortfolio(User user) {
        Portfolio portfolio = new Portfolio();
        portfolio.setUser(user);
        portfolioRepository.save(portfolio);
    }

    public BigDecimal getAvailableCash() {
        // Retrieve available cash for the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return portfolioRepository.getByUser_Email(userEmail).getCash();
    }

    public int getQuantity(String symbol) {
        // Retrieve quantity of the specified stock that user has
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Portfolio portfolio = portfolioRepository.getByUser_Email(userEmail);
        for (StockHolding stockHolding: portfolio.getStockHoldings()) {
            if (stockHolding.getSymbol().equals(symbol)) {
                return stockHolding.getQuantity();
            }
        }
        return 0;
    }
}