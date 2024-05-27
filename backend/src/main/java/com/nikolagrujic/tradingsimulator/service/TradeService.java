package com.nikolagrujic.tradingsimulator.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nikolagrujic.tradingsimulator.constants.Constants;
import com.nikolagrujic.tradingsimulator.exception.InvalidOrderException;
import com.nikolagrujic.tradingsimulator.model.Transaction;
import com.nikolagrujic.tradingsimulator.service.order.execution.MarketOrderExecutionStrategy;
import com.nikolagrujic.tradingsimulator.service.order.execution.OrderExecutionStrategy;
import com.nikolagrujic.tradingsimulator.model.Portfolio;
import com.nikolagrujic.tradingsimulator.dto.TradeOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TradeService {
    private final PortfolioService portfolioService;
    private final ApplicationContext applicationContext;
    private final TransactionService transactionService;
    private final UserService userService;
    private final StockService stockService;
    private OrderExecutionStrategy orderExecutionStrategy;
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeService.class);

    @Autowired
    public TradeService(
            PortfolioService portfolioService,
            ApplicationContext applicationContext,
            TransactionService transactionService,
            UserService userService,
            StockService stockService) {
        this.portfolioService = portfolioService;
        this.applicationContext = applicationContext;
        this.transactionService = transactionService;
        this.userService = userService;
        this.stockService = stockService;
    }

    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Transactional
    public ObjectNode placeOrder(TradeOrder tradeOrder) throws Exception {
        checkMarketIsOpen();
        String email = getUserEmail();
        LOGGER.info("[{}] Placing a trade order: {}", email, tradeOrder.toString());
        boolean locked = setPortfolioLock(email, true); // Lock portfolio
        if (!locked) throw new Exception("Another order is currently in progress...");
        ObjectNode result = startOrderTransaction(email, tradeOrder);
        setPortfolioLock(email, false); // Unlock portfolio
        return result;
    }

    private void checkMarketIsOpen() throws InvalidOrderException {
        // Specify the market time zone
        ZoneId marketTimeZone = ZoneId.of("America/New_York");

        // Convert the local date and time to the New York time zone
        LocalDateTime dateTime = LocalDateTime.now(marketTimeZone);

        if (dateTime.getDayOfWeek().equals(DayOfWeek.SATURDAY) || dateTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            throw new InvalidOrderException("The market is closed. It will open on Monday at 9:30am.");
        }
        if (dateTime.getHour() < 9 || (dateTime.getHour() == 9 && dateTime.getMinute() < 30)) {
            LocalDateTime openDateTime = LocalDateTime.of(
                dateTime.getYear(),
                dateTime.getMonth(),
                dateTime.getDayOfMonth(),
                9,
                30
            );
            Duration duration = Duration.between(dateTime, openDateTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            throw new InvalidOrderException(
                "The market is closed. It will open in " + hours + "h " + minutes + "min."
            );
        }
        if (dateTime.getHour() >= 16) {
            String openingDay = dateTime.getDayOfWeek().equals(DayOfWeek.FRIDAY) ? "on Monday" : "tomorrow";
            throw new InvalidOrderException(
                "The market is closed. It will open " + openingDay + " at 9:30am."
            );
        }
        // At this point, the market is open
    }

    private ObjectNode startOrderTransaction(String email, TradeOrder tradeOrder) throws Exception {
        checkOrder(tradeOrder);
        // Order is valid - proceed
        configureExecutionStrategy(tradeOrder);
        BigDecimal totalPrice = stockService.fetchAndGetTotalPrice(tradeOrder); // Get stock price
        createTransaction(email, tradeOrder, totalPrice);
        return this.orderExecutionStrategy.executeOrder(email, tradeOrder, totalPrice);
    }

    private void createTransaction(String email, TradeOrder tradeOrder, BigDecimal price) {
        LOGGER.info("[{}] Recording a new transaction: {}", email, tradeOrder.toString());
        Transaction transaction = new Transaction();
        transaction.setAction(tradeOrder.getAction());
        transaction.setSymbol(tradeOrder.getSymbol());
        transaction.setName(tradeOrder.getName());
        transaction.setType(tradeOrder.getType());
        transaction.setQuantity(tradeOrder.getQuantity());
        transaction.setDuration(tradeOrder.getDuration());
        transaction.setUser(userService.findByEmail(email));
        transaction.setTradePrice(price);
        transaction.setDateTime(LocalDateTime.now());
        transactionService.save(transaction);
    }

    boolean setPortfolioLock(String email, boolean locked) throws Exception {
        for (int i = 0; i < 5; ++i) {
            Portfolio portfolio = portfolioService.getByUserEmailAndLocked(email, !locked);
            portfolio.setLocked(locked);
            portfolio = portfolioService.save(portfolio);
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
        if (!stockService.existsBySymbol(tradeOrder.getSymbol())) {
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