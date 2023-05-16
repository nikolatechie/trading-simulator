package com.nikolagrujic.tradingsimulator.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nikolagrujic.tradingsimulator.constants.Constants;
import com.nikolagrujic.tradingsimulator.exception.InvalidOrderException;
import com.nikolagrujic.tradingsimulator.service.order.execution.MarketOrderExecutionStrategy;
import com.nikolagrujic.tradingsimulator.service.order.execution.OrderExecutionStrategy;
import com.nikolagrujic.tradingsimulator.model.Portfolio;
import com.nikolagrujic.tradingsimulator.model.TradeOrder;
import com.nikolagrujic.tradingsimulator.repository.PortfolioRepository;
import com.nikolagrujic.tradingsimulator.repository.StockInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class TradeService {
    private final StockInfoRepository stockInfoRepository;
    private final PortfolioRepository portfolioRepository;
    private final ApplicationContext applicationContext;
    private OrderExecutionStrategy orderExecutionStrategy;
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeService.class);

    @Autowired
    public TradeService(
            StockInfoRepository stockInfoRepository,
            PortfolioRepository portfolioRepository,
            ApplicationContext applicationContext) {
        this.stockInfoRepository = stockInfoRepository;
        this.portfolioRepository = portfolioRepository;
        this.applicationContext = applicationContext;
    }

    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public ObjectNode placeOrder(TradeOrder tradeOrder) throws Exception {
        String email = getUserEmail();
        boolean locked = setPortfolioLock(email, true); // Lock portfolio
        if (!locked) throw new Exception("Another order is currently in progress...");
        ObjectNode result;
        try {
            result = startOrderTransaction(email, tradeOrder);
        } catch (Exception e) {
            setPortfolioLock(email, false); // Unlock portfolio
            throw e;
        }
        setPortfolioLock(email, false); // Unlock portfolio
        return result;
    }

    @Transactional
    ObjectNode startOrderTransaction(String email, TradeOrder tradeOrder) throws Exception {
        checkOrder(tradeOrder);
        // Order is valid - proceed
        configureExecutionStrategy(tradeOrder);
        return this.orderExecutionStrategy.executeOrder(email, tradeOrder);
    }

    boolean setPortfolioLock(String email, boolean locked) throws Exception {
        for (int i = 0; i < 5; ++i) {
            Portfolio portfolio = portfolioRepository.getByUser_EmailAndLocked(email, !locked);
            portfolio.setLocked(locked);
            portfolio = portfolioRepository.save(portfolio);
            if (portfolio.isLocked() != locked) {
                Thread.sleep(400);
                continue;
            }
            LOGGER.info("[{}] Portfolio lock has been successfully set to {}", email, locked);
            return true;
        }
        LOGGER.warn("[{}] Couldn't set portfolio lock to {}", email, locked);
        return false;
    }

    private void configureExecutionStrategy(TradeOrder tradeOrder) throws InvalidOrderException {
        if (tradeOrder.getType().equals(Constants.OrderType.Market)) {
            this.orderExecutionStrategy = applicationContext.getBean(MarketOrderExecutionStrategy.class);
        } else {
            // Only Market orders are supported currently
            throw new InvalidOrderException(tradeOrder.getType() + " is an invalid type.");
        }
    }

    // Throws an exception if order is invalid
    private void checkOrder(TradeOrder tradeOrder) throws InvalidOrderException {
        if (!stockInfoRepository.existsBySymbol(tradeOrder.getSymbol())) {
            throw new InvalidOrderException(tradeOrder.getSymbol() + " is not a valid symbol.");
        }
        if (tradeOrder.getQuantity() < 1) {
            throw new InvalidOrderException(tradeOrder.getQuantity() + " is not a valid quantity.");
        }
        if (tradeOrder.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOrderException("Specified price can't be a negative number.");
        }
        // Enum-type fields are checked before controller method
    }
}