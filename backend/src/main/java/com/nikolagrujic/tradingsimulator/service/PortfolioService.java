package com.nikolagrujic.tradingsimulator.service;

import com.nikolagrujic.tradingsimulator.constants.Constants;
import com.nikolagrujic.tradingsimulator.exception.HistoryExistsException;
import com.nikolagrujic.tradingsimulator.model.Portfolio;
import com.nikolagrujic.tradingsimulator.model.PortfolioHistory;
import com.nikolagrujic.tradingsimulator.model.StockHolding;
import com.nikolagrujic.tradingsimulator.model.User;
import com.nikolagrujic.tradingsimulator.repository.PortfolioHistoryRepository;
import com.nikolagrujic.tradingsimulator.repository.PortfolioRepository;
import com.nikolagrujic.tradingsimulator.repository.StockHoldingRepository;
import com.nikolagrujic.tradingsimulator.response.PortfolioOverview;
import com.nikolagrujic.tradingsimulator.response.PortfolioStatsResponse;
import com.nikolagrujic.tradingsimulator.response.TodayChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioHistoryRepository historyRepository;
    private final StockHoldingRepository stockHoldingRepository;
    private final StockService stockService;
    private UserService userService;
    private static final long SAVE_PORTFOLIO_HISTORY_PERIOD_MILLISECONDS = 24 * 60 * 60 * 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioService.class);

    @Autowired
    public PortfolioService(
        PortfolioRepository portfolioRepository,
        StockHoldingRepository stockHoldingRepository,
        StockService stockService,
        PortfolioHistoryRepository historyRepository
    ) {
        this.portfolioRepository = portfolioRepository;
        this.stockHoldingRepository = stockHoldingRepository;
        this.stockService = stockService;
        this.historyRepository = historyRepository;
    }

    /**
     * Used to get rid of circular dependency.
     * @param userService UserService object that's being set from UserService
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // Returns cash balance, today's change, and annual return
    public PortfolioOverview getOverview() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        PortfolioOverview portfolioOverview = new PortfolioOverview();
        BigDecimal cash = getAvailableCash(email);
        BigDecimal stockVal = getPortfolioStats(email).getTotalValue();
        portfolioOverview.setCash(cash);
        portfolioOverview.setTodayChange(calculateTodayChange(email, cash.add(stockVal)));
        portfolioOverview.setAnnualReturn(calculateAnnualReturn(cash.add(stockVal)));
        return portfolioOverview;
    }

    private TodayChange calculateTodayChange(String email, BigDecimal todayTotalVal) {
        BigDecimal yesterdayTotalVal = BigDecimal.ZERO;
        double percentageChange = 0.0;
        PortfolioHistory yesterdayPortfolio = historyRepository
                .findByPortfolio_User_EmailAndDate(email, LocalDate.now().minusDays(1));

        if (yesterdayPortfolio != null) {
            yesterdayTotalVal = yesterdayPortfolio.getTotalValue();
            percentageChange = todayTotalVal.subtract(yesterdayTotalVal).doubleValue();
            if (!yesterdayTotalVal.equals(BigDecimal.ZERO)) {
                percentageChange = (percentageChange / yesterdayTotalVal.doubleValue()) * 100;
            } else {
                percentageChange = 0.0;
            }
        }

        return new TodayChange(
            todayTotalVal.subtract(yesterdayTotalVal),
            percentageChange
        );
    }

    private double calculateAnnualReturn(BigDecimal totalVal) {
        LocalDate verificationDate = userService.getVerificationDate();
        double annualReturn =
                ((totalVal.doubleValue() - Constants.STARTING_CASH_BALANCE) / Constants.STARTING_CASH_BALANCE) * 100;

        if (LocalDate.now().minusDays(365).isBefore(verificationDate)) {
            long daysBetween = ChronoUnit.DAYS.between(verificationDate, LocalDate.now());
            annualReturn *= (365.0 / daysBetween);
        }

        return annualReturn;
    }

    public PortfolioStatsResponse getPortfolioStats(String email) {
        List<StockHolding> stockHoldings = getHoldings(email);
        BigDecimal totalVal = BigDecimal.ZERO, totalSpent = BigDecimal.ZERO;
        for (StockHolding stockHolding: stockHoldings) {
            totalVal = totalVal.add(
                BigDecimal.valueOf(stockHolding.getQuantity()).multiply(stockHolding.getCurrentPrice())
            );
            totalSpent = totalSpent.add(stockHolding.getPurchasePrice());
        }
        return new PortfolioStatsResponse(totalVal, totalVal.subtract(totalSpent));
    }

    private List<StockHolding> getHoldings(String email) {
        LOGGER.info("Retrieving portfolio stats: {}", email);
        List<StockHolding> stockHoldings = stockHoldingRepository.getAllByPortfolio_User_Email(email);
        for (StockHolding stockHolding: stockHoldings) {
            stockHolding.setCurrentPrice(stockService.getCurrentPrice(stockHolding.getSymbol()));
        }
        return stockHoldings;
    }

    public Page<StockHolding> getHoldingsByEmail(Pageable pageable, String email) {
        Page<StockHolding> stockHoldings = stockHoldingRepository.getAllByPortfolio_User_Email(pageable, email);
        for (StockHolding stockHolding: stockHoldings) {
            stockHolding.setCurrentPrice(stockService.getCurrentPrice(stockHolding.getSymbol()));
        }
        return stockHoldings;
    }

    public void createUserPortfolio(User user) {
        Portfolio portfolio = new Portfolio();
        portfolio.setUser(user);
        portfolioRepository.save(portfolio);
    }

    public List<PortfolioHistory> getPortfolioHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return historyRepository.findByPortfolio_User_Email(email);
    }

    public BigDecimal getAvailableCash(String userEmail) {
        // Retrieve available cash for the current user
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

    private void checkIfExistsHistory(Long id, LocalDate date) throws HistoryExistsException {
        PortfolioHistory history = historyRepository.findByPortfolio_IdAndDate(id, date);
        if (history != null) {
            throw new HistoryExistsException(
                "History entry with the same date already exists."
            );
        }
    }

    @Async
    @Scheduled(
        initialDelay = Constants.REQUEST_DELAY_MILLISECONDS,
        fixedDelay = SAVE_PORTFOLIO_HISTORY_PERIOD_MILLISECONDS
    )
    // Saves the total value of portfolios each day to track users' performance.
    public void savePortfolioHistory() {
        List<Portfolio> portfolios = portfolioRepository.findAll();
        LOGGER.info("Saving portfolio history for {} users.", portfolios.size());

        for (Portfolio portfolio: portfolios) {
            try {
                checkIfExistsHistory(portfolio.getId(), LocalDate.now());
                String email = portfolio.getUser().getEmail();
                BigDecimal cash = getAvailableCash(email);
                BigDecimal stockValue = getPortfolioStats(email).getTotalValue();
                PortfolioHistory history = new PortfolioHistory();
                history.setPortfolio(portfolio);
                history.setTotalValue(cash.add(stockValue));
                history.setDate(LocalDate.now());
                historyRepository.save(history);
            } catch (Exception e) {
                LOGGER.warn("[{}] Failed to save a new history entry: {}",
                    portfolio.getUser().getEmail(),
                    e.getMessage()
                );
            }
        }
    }
}