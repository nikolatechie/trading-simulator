package com.nikolagrujic.tradingsimulator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nikolagrujic.tradingsimulator.constants.Constants;
import com.nikolagrujic.tradingsimulator.exception.HistoryExistsException;
import com.nikolagrujic.tradingsimulator.exception.RankCalculationException;
import com.nikolagrujic.tradingsimulator.model.*;
import com.nikolagrujic.tradingsimulator.repository.PortfolioHistoryRepository;
import com.nikolagrujic.tradingsimulator.repository.PortfolioRepository;
import com.nikolagrujic.tradingsimulator.repository.StockHoldingRepository;
import com.nikolagrujic.tradingsimulator.dto.BestWorstStocksResponse;
import com.nikolagrujic.tradingsimulator.dto.PortfolioOverview;
import com.nikolagrujic.tradingsimulator.dto.PortfolioStatsResponse;
import com.nikolagrujic.tradingsimulator.dto.TodayChange;
import lombok.Setter;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioHistoryRepository historyRepository;
    private final StockHoldingRepository stockHoldingRepository;
    private final StockService stockService;
    @Setter
    private UserService userService;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioService.class);

    @Autowired
    public PortfolioService(
        PortfolioRepository portfolioRepository,
        StockHoldingRepository stockHoldingRepository,
        StockService stockService,
        PortfolioHistoryRepository historyRepository,
        ObjectMapper objectMapper
    ) {
        this.portfolioRepository = portfolioRepository;
        this.stockHoldingRepository = stockHoldingRepository;
        this.stockService = stockService;
        this.historyRepository = historyRepository;
        this.objectMapper = objectMapper;
    }

    // Returns cash balance, today's change, and annual return
    public PortfolioOverview getOverview(String email) {
        PortfolioOverview portfolioOverview = new PortfolioOverview();
        BigDecimal cash = getAvailableCash(email);
        BigDecimal stockVal = getPortfolioStats(email).getTotalValue();
        portfolioOverview.setCash(cash);
        portfolioOverview.setTodayChange(calculateTodayChange(email, cash.add(stockVal)));
        portfolioOverview.setAnnualReturn(calculateAnnualReturn(cash.add(stockVal)));
        return portfolioOverview;
    }

    private TodayChange calculateTodayChange(String email, BigDecimal todayTotalVal) {
        BigDecimal yesterdayTotalVal;
        double percentageChange;
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
        } else return new TodayChange(BigDecimal.ZERO, 0.0);

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
            long daysBetween = ChronoUnit.DAYS.between(verificationDate, LocalDate.now()) + 1;
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

    public ObjectNode getPortfolioRank(String email) throws RankCalculationException {
        try {
            Long rank = 0L, prevRank = -1L;
            BigDecimal userPortfolioVal = BigDecimal.ZERO;
            BigDecimal highestPortfolioVal = BigDecimal.ZERO;
            List<Portfolio> portfolios = portfolioRepository.findAll();
            for (Portfolio portfolio: portfolios) {
                List<PortfolioHistory> histories = portfolio.getHistory();
                histories.sort(Comparator.comparing(PortfolioHistory::getId).reversed());
                PortfolioHistory history = histories.get(0);
                if (portfolio.getUser().getEmail().equals(email)) {
                    rank = history.getPortfolioRank();
                    userPortfolioVal = history.getTotalValue();
                    if (histories.size() > 1) {
                        prevRank = histories.get(1).getPortfolioRank();
                    }
                }
                highestPortfolioVal = highestPortfolioVal.max(history.getTotalValue());
            }
            if (highestPortfolioVal.equals(userPortfolioVal)) {
                rank = 1L;
            }
            ObjectNode result = objectMapper.createObjectNode();
            result.put("rank", rank);
            if (prevRank != -1L) {
                result.put("prevRank", prevRank);
            }
            result.put("topPlayerTotalVal", highestPortfolioVal);
            result.put("totalUsers", portfolios.size());
            return result;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            throw new RankCalculationException(e.getMessage());
        }
    }

    /**
     * Returns the best and the worst performing stock the user owns.
     */
    public BestWorstStocksResponse getBestAndWorstStocks(String email) {
        List<StockHolding> holdings = stockHoldingRepository.getAllByPortfolio_User_Email(email);
        StockHolding best = null, worst = null;
        for (StockHolding stockHolding: holdings) {
            if (best == null) {
                best = stockHolding;
            }
            if (worst == null) {
                worst = stockHolding;
            }
            stockHolding.setCurrentPrice(stockService.getCurrentPrice(stockHolding.getSymbol()));
            BigDecimal gainOrLoss = new BigDecimal(stockHolding.getQuantity())
                    .multiply(stockHolding.getCurrentPrice())
                    .subtract(stockHolding.getPurchasePrice());
            BigDecimal bestGain = new BigDecimal(best.getQuantity())
                    .multiply(best.getCurrentPrice())
                    .subtract(best.getPurchasePrice());
            BigDecimal worstLoss = new BigDecimal(worst.getQuantity())
                    .multiply(worst.getCurrentPrice())
                    .subtract(worst.getPurchasePrice());
            if (gainOrLoss.compareTo(bestGain) > 0) {
                best = stockHolding;
            } else if (gainOrLoss.compareTo(worstLoss) < 0) {
                worst = stockHolding;
            }
        }
        return new BestWorstStocksResponse(best, worst);
    }

    private List<StockHolding> getHoldings(String email) {
        LOGGER.info("Retrieving stock holdings: {}", email);
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
        PortfolioHistory history = new PortfolioHistory();
        history.setTotalValue(new BigDecimal(Constants.STARTING_CASH_BALANCE));
        history.setPortfolio(portfolio);
        history.setDate(LocalDate.now());
        history.setPortfolioRank(calculateDefaultRank());
        historyRepository.save(history);
    }

    /**
     * Calculates default portfolio rank when user registers.
     */
    private Long calculateDefaultRank() {
        long countHigher = 0L;
        List<Portfolio> portfolios = portfolioRepository.findAll();
        for (Portfolio portfolio: portfolios) {
            // Calculate total portfolio values
            String email = portfolio.getUser().getEmail();
            BigDecimal cash = getAvailableCash(email);
            BigDecimal stockValue = getPortfolioStats(email).getTotalValue();
            if (cash.add(stockValue).compareTo(new BigDecimal(Constants.STARTING_CASH_BALANCE)) > 0) {
                ++countHigher;
            }
        }
        return countHigher + 1L;
    }

    public List<PortfolioHistory> getPortfolioHistory(String email) {
        return historyRepository.findByPortfolio_User_EmailOrderByDateAsc(email);
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

    private PortfolioHistory getHistory(Long id, LocalDate date) throws HistoryExistsException {
        return historyRepository.findByPortfolio_IdAndDate(id, date);
    }

    @Async
    @Scheduled(cron = "0 0 18 * * *", zone = "America/New_York")
    // Saves the total value of portfolios each day to track users' performance.
    public void savePortfolioHistory() {
        List<Portfolio> portfolios = portfolioRepository.findAll();
        LOGGER.info("Saving portfolio history for {} users.", portfolios.size());
        List<PortfolioHistory> histories = new ArrayList<>();
        for (Portfolio portfolio: portfolios) {
            try {
                String email = portfolio.getUser().getEmail();
                PortfolioHistory history = getHistory(portfolio.getId(), LocalDate.now());
                if (history == null) {
                    // Create new portfolio history
                    history = new PortfolioHistory();
                    history.setPortfolio(portfolio);
                    BigDecimal totalValue = getAvailableCash(email);
                    totalValue = totalValue.add(getPortfolioStats(email).getTotalValue());
                    history.setTotalValue(totalValue);
                }
                histories.add(history);
            } catch (Exception e) {
                LOGGER.warn("[{}] Failed to save a new history entry: {}",
                    portfolio.getUser().getEmail(),
                    e.getMessage()
                );
            }
        }
        histories.sort(Comparator.comparing(PortfolioHistory::getTotalValue).reversed());
        for (int i = 0; i < histories.size(); ++i) {
            PortfolioHistory history = histories.get(i);
            if (LocalDate.now().equals(history.getDate())) continue;
            try {
                history.setDate(LocalDate.now());
                history.setPortfolioRank(i + 1L);
                historyRepository.save(history);
            } catch (Exception e) {
                LOGGER.warn("[{}] Failed to save a new history entry: {}",
                    history.getPortfolio().getUser().getEmail(),
                    e.getMessage()
                );
            }
        }
    }
}