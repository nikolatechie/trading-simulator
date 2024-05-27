package com.nikolagrujic.tradingsimulator.service;

import com.nikolagrujic.tradingsimulator.constants.Constants;
import com.nikolagrujic.tradingsimulator.constants.Constants.StockExchange;
import com.nikolagrujic.tradingsimulator.exception.StockRecommendationException;
import com.nikolagrujic.tradingsimulator.model.StockHolding;
import com.nikolagrujic.tradingsimulator.model.Transaction;
import com.nikolagrujic.tradingsimulator.repository.StockHoldingRepository;
import com.nikolagrujic.tradingsimulator.dto.PriceResponse;
import com.nikolagrujic.tradingsimulator.model.StockInfo;
import com.nikolagrujic.tradingsimulator.dto.StocksListResponse;
import com.nikolagrujic.tradingsimulator.dto.TradeOrder;
import com.nikolagrujic.tradingsimulator.repository.StockInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class StockService {
    @Value("${rapid.api.key}")
    private String apiKey;
    @Value("${twelvedata.api.host}")
    private String apiHost;
    private static final String STOCKS_ENDPOINT = "https://twelve-data1.p.rapidapi.com/stocks";
    private static final String PRICE_ENDPOINT = "https://twelve-data1.p.rapidapi.com/price";
    private final RestTemplate restTemplate = new RestTemplate();
    private final TransactionService transactionService;
    private final StockInfoRepository stockInfoRepository;
    private final StockHoldingRepository stockHoldingRepository;
    private static final BigDecimal PRICE_TICK = new BigDecimal("0.01");
    private static final long PULL_STOCK_PRICES_PERIOD_MILLISECONDS = 30 * 60 * 1000;
    private static final int STOCK_RECOMMENDATION_DAYS = 5;
    private static final Logger LOGGER = LoggerFactory.getLogger(StockService.class);

    @Autowired
    public StockService(
        TransactionService transactionService,
        StockInfoRepository stockInfoRepository,
        StockHoldingRepository stockHoldingRepository
    ) {
        this.transactionService = transactionService;
        this.stockInfoRepository = stockInfoRepository;
        this.stockHoldingRepository = stockHoldingRepository;
    }

    public boolean existsBySymbol(String symbol) {
        return stockInfoRepository.existsBySymbol(symbol);
    }

    public Page<StockInfo> getListOfStockInfo(String search, Pageable pageable) {
        if (search == null || search.length() < 2) return stockInfoRepository.findAll(pageable);
        return stockInfoRepository.findAll(search, pageable);
    }

    public StockInfo getRecommendedStock() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(STOCK_RECOMMENDATION_DAYS);
        List<Transaction> transactions = transactionService.getRecentTransactions(startDate);
        Map<String, BigDecimal> values = new HashMap<>();
        String symbol = null;
        BigDecimal cashValue = BigDecimal.ZERO;
        for (Transaction transaction: transactions) {
            if (values.containsKey(transaction.getSymbol())) {
                values.put(transaction.getSymbol(), values.get(transaction.getSymbol()).add(transaction.getTradePrice()));
            } else {
                values.put(transaction.getSymbol(), transaction.getTradePrice());
            }
            BigDecimal currPrice = values.get(transaction.getSymbol());
            if (currPrice.compareTo(cashValue) > 0) {
                cashValue = currPrice;
                symbol = transaction.getSymbol();
            }
        }
        if (symbol == null) {
            throw new StockRecommendationException("No recent transactions found.");
        }
        return stockInfoRepository.getBySymbol(symbol);
    }

    private StockInfo formatStockInfo(StockInfo stockInfo) {
        stockInfo.setCountry(stockInfo.getCountry().trim());
        stockInfo.setName(stockInfo.getName().trim());
        stockInfo.setCurrency(stockInfo.getCurrency().trim().toUpperCase());
        stockInfo.setSymbol(stockInfo.getSymbol().trim().toUpperCase());
        stockInfo.setExchange(stockInfo.getExchange().trim().toUpperCase());
        stockInfo.setMicCode(stockInfo.getMicCode().trim().toUpperCase());
        return stockInfo;
    }

    @Async
    @Scheduled(cron = "0 0 0 1 * ?") // Runs at midnight on the 1st of every month
    public void startRetrievingStocks() {
        for (StockExchange stockExchange : StockExchange.values()) {
            try {
                LOGGER.info("Retrieving a list of stocks (exchange = {})", stockExchange);
                HttpHeaders headers = new HttpHeaders();
                headers.add("X-RapidAPI-Key", apiKey);
                headers.add("X-RapidAPI-Host", apiHost);
                headers.setContentType(MediaType.APPLICATION_JSON);
                RequestEntity<?> requestEntity = new RequestEntity<>(
                    headers,
                    HttpMethod.GET,
                    URI.create(STOCKS_ENDPOINT + "?exchange=" + stockExchange)
                );
                ResponseEntity<StocksListResponse> responseEntity =
                        restTemplate.exchange(requestEntity, StocksListResponse.class);

                if (responseEntity.hasBody() && Objects.requireNonNull(responseEntity.getBody()).getData() != null) {
                    List<StockInfo> stockList = responseEntity.getBody().getData();
                    LOGGER.info("Successfully retrieved a list of {} stocks.", stockList.size());
                    int invalidStocks = 0;
                    for (StockInfo stockInfo : stockList) {
                        try {
                            if (!stockInfoRepository.existsBySymbol(stockInfo.getSymbol())) {
                                stockInfo.setLastUpdated(LocalDateTime.now());
                                stockInfoRepository.save(formatStockInfo(stockInfo));
                            }
                        } catch (Exception e) {
                            ++invalidStocks;
                        }
                    }
                    if (invalidStocks > 0)
                        LOGGER.warn("Number of invalid stocks: {}", invalidStocks);
                }
            } catch (RestClientException e) {
                LOGGER.error("Couldn't retrieve stocks data: {}", e.getMessage());
            }
        }
    }

    private BigDecimal fetchPrice(String symbol) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-RapidAPI-Key", apiKey);
            headers.add("X-RapidAPI-Host", apiHost);
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<?> requestEntity = new RequestEntity<>(
                    headers,
                    HttpMethod.GET,
                    URI.create(PRICE_ENDPOINT + "?symbol=" + symbol)
            );
            ResponseEntity<PriceResponse> responseEntity =
                    restTemplate.exchange(requestEntity, PriceResponse.class);

            if (responseEntity.hasBody() && responseEntity.getBody() != null) {
                return new BigDecimal(responseEntity.getBody().getPrice());
            }
        } catch (RestClientException e) {
            LOGGER.error("Couldn't retrieve the stock price: {}", e.getMessage());
        }
        throw new Exception("Couldn't fetch the current stock price.");
    }

    public BigDecimal fetchAndGetTotalPrice(TradeOrder tradeOrder) throws Exception {
        BigDecimal price = fetchPrice(tradeOrder.getSymbol());
        updateCurrentPrice(tradeOrder.getSymbol(), price);

        if (tradeOrder.getAction().equals(Constants.OrderAction.Buy)) {
            price = price.add(PRICE_TICK);
        } else if (tradeOrder.getAction().equals(Constants.OrderAction.Sell)) {
            price = price.subtract(PRICE_TICK);
        }

        return price.multiply(BigDecimal.valueOf(tradeOrder.getQuantity()));
    }

    public BigDecimal getCurrentPrice(String symbol) {
        return stockInfoRepository.getPriceBySymbol(symbol);
    }

    public void updateCurrentPrice(String symbol, BigDecimal price) {
        LOGGER.info("[{}] Updating current price to {}", symbol, price);
        stockInfoRepository.updateCurrentPrice(symbol, price, LocalDateTime.now());
    }

    private boolean shouldUpdatePrice(String symbol) {
        LocalDateTime lastUpdated = stockInfoRepository.getUpdateDateTimeBySymbol(symbol);
        if (lastUpdated == null) return false;
        return lastUpdated.isBefore(LocalDateTime.now().minusHours(1));
    }

    @Async
    @Scheduled(
        initialDelay = Constants.REQUEST_DELAY_MILLISECONDS,
        fixedDelay = PULL_STOCK_PRICES_PERIOD_MILLISECONDS
    )
    public void updateStockPrices() {
        List<StockHolding> stocks = stockHoldingRepository.findDistinctBySymbolNotNull();
        int countUpdated = 0;
        for (int i = 0; i < stocks.size(); ++i) {
            String symbol = stocks.get(i).getSymbol();
            if (shouldUpdatePrice(symbol)) {
                try {
                    BigDecimal price = fetchPrice(symbol);
                    updateCurrentPrice(stocks.get(i).getSymbol(), price);
                    ++countUpdated;
                } catch (Exception e) {
                    --i; // Try again
                }
            }
        }
        String strSuffix = (countUpdated == 1) ? "stock." : "stocks.";
        LOGGER.info("Updated current stock prices for {} {}", countUpdated, strSuffix);
    }
}