package com.nikolagrujic.tradingsimulator.service.order.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nikolagrujic.tradingsimulator.constants.Constants;
import com.nikolagrujic.tradingsimulator.exception.InvalidOrderException;
import com.nikolagrujic.tradingsimulator.model.Portfolio;
import com.nikolagrujic.tradingsimulator.model.StockHolding;
import com.nikolagrujic.tradingsimulator.dto.TradeOrder;
import com.nikolagrujic.tradingsimulator.repository.StockHoldingRepository;
import com.nikolagrujic.tradingsimulator.service.PortfolioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class MarketOrderExecutionStrategy implements OrderExecutionStrategy {
    private final PortfolioService portfolioService;
    private final StockHoldingRepository stockHoldingRepository;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketOrderExecutionStrategy.class);

    @Autowired
    public MarketOrderExecutionStrategy(
            PortfolioService portfolioService,
            StockHoldingRepository stockHoldingRepository,
            ObjectMapper objectMapper) {
        this.portfolioService = portfolioService;
        this.stockHoldingRepository = stockHoldingRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode executeOrder(String email, TradeOrder tradeOrder, BigDecimal totalPrice) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (tradeOrder.getAction().equals(Constants.OrderAction.Buy))
            objectNode.put("cash", executeBuyOrder(email, tradeOrder, totalPrice));
        else if (tradeOrder.getAction().equals(Constants.OrderAction.Sell))
            objectNode.put("cash", executeSellOrder(email, tradeOrder, totalPrice));
        else
            throw new InvalidOrderException(tradeOrder.getAction() + " is an invalid action.");

        return objectNode;
    }

    private BigDecimal executeBuyOrder(String email, TradeOrder tradeOrder, BigDecimal totalPrice) {
        LOGGER.info("[{}] Executing BUY order: {}", email, tradeOrder.toString());
        // Check if user has enough cash
        Portfolio portfolio = portfolioService.getByUserEmail(email);
        if (portfolio.getCash().compareTo(totalPrice) < 0) {
            throw new InvalidOrderException("Insufficient funds!");
        }
        BigDecimal cashLeft = portfolio.getCash().subtract(totalPrice);
        portfolio.setCash(cashLeft);
        addToStockHoldings(totalPrice, portfolio, tradeOrder);
        return cashLeft;
    }

    private BigDecimal executeSellOrder(String email, TradeOrder tradeOrder, BigDecimal totalPrice) {
        LOGGER.info("[{}] Executing SELL order: {}", email, tradeOrder.toString());
        Portfolio portfolio = portfolioService.getByUserEmail(email);
        BigDecimal cashLeft = portfolio.getCash().add(totalPrice);
        portfolio.setCash(cashLeft);
        subtractFromStockHolding(totalPrice, portfolio, tradeOrder); // Check and update
        return cashLeft;
    }

    private void subtractFromStockHolding(BigDecimal totalPrice, Portfolio portfolio, TradeOrder tradeOrder) {
        for (StockHolding stockHolding: portfolio.getStockHoldings()) {
            if (stockHolding.getSymbol().equals(tradeOrder.getSymbol())) {
                if (tradeOrder.getQuantity() > stockHolding.getQuantity()) {
                    throw new InvalidOrderException("Insufficient quantity!");
                }
                stockHolding.setQuantity(stockHolding.getQuantity() - tradeOrder.getQuantity());
                stockHolding.setPurchasePrice(stockHolding.getPurchasePrice().subtract(totalPrice));
                stockHolding.setDateTime(LocalDateTime.now());
                if (stockHolding.getQuantity() == 0) {
                    portfolio.getStockHoldings().remove(stockHolding);
                    stockHoldingRepository.delete(stockHolding);
                }
                portfolioService.save(portfolio);
                return;
            }
        }
        throw new InvalidOrderException("You don't have any shares of this stock.");
    }

    private void addToStockHoldings(BigDecimal totalPrice, Portfolio portfolio, TradeOrder tradeOrder) {
        // Update stock holding if it already exists
        for (StockHolding stockHolding: portfolio.getStockHoldings()) {
            if (stockHolding.getSymbol().equals(tradeOrder.getSymbol())) {
                stockHolding.setQuantity(stockHolding.getQuantity() + tradeOrder.getQuantity());
                stockHolding.setPurchasePrice(stockHolding.getPurchasePrice().add(totalPrice));
                stockHolding.setDateTime(LocalDateTime.now());
                // Changes get saved automatically
                return;
            }
        }
        // Otherwise, create a new stock holding
        StockHolding stockHolding = createStockHolding(portfolio, tradeOrder, totalPrice);
        portfolio.getStockHoldings().add(stockHolding);
        portfolioService.save(portfolio);
    }

    private StockHolding createStockHolding(
        Portfolio portfolio,
        TradeOrder tradeOrder,
        BigDecimal price
    ) {
        StockHolding stockHolding = new StockHolding();
        stockHolding.setPortfolio(portfolio);
        stockHolding.setQuantity(tradeOrder.getQuantity());
        stockHolding.setPurchasePrice(price);
        stockHolding.setDuration(tradeOrder.getDuration());
        stockHolding.setSymbol(tradeOrder.getSymbol());
        stockHolding.setName(tradeOrder.getName());
        stockHolding.setType(tradeOrder.getType());
        stockHolding.setDateTime(LocalDateTime.now());
        return stockHolding;
    }
}