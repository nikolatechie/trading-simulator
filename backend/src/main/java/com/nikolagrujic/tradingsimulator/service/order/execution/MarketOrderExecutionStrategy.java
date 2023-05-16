package com.nikolagrujic.tradingsimulator.service.order.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nikolagrujic.tradingsimulator.constants.Constants;
import com.nikolagrujic.tradingsimulator.exception.InvalidOrderException;
import com.nikolagrujic.tradingsimulator.model.Portfolio;
import com.nikolagrujic.tradingsimulator.model.PriceResponse;
import com.nikolagrujic.tradingsimulator.model.StockHolding;
import com.nikolagrujic.tradingsimulator.model.TradeOrder;
import com.nikolagrujic.tradingsimulator.repository.PortfolioRepository;
import com.nikolagrujic.tradingsimulator.repository.StockHoldingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;

@Component
public class MarketOrderExecutionStrategy implements OrderExecutionStrategy {
    @Value("${twelvedata.api.key}")
    private String apiKey;
    @Value("${twelvedata.api.host}")
    private String apiHost;
    private static final String PRICE_ENDPOINT = "https://twelve-data1.p.rapidapi.com/price";
    private final RestTemplate restTemplate = new RestTemplate();
    private final PortfolioRepository portfolioRepository;
    private final StockHoldingRepository stockHoldingRepository;
    private final ObjectMapper objectMapper;
    private static final BigDecimal PRICE_TICK = new BigDecimal("0.01");
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketOrderExecutionStrategy.class);

    @Autowired
    public MarketOrderExecutionStrategy(
            PortfolioRepository portfolioRepository,
            StockHoldingRepository stockHoldingRepository,
            ObjectMapper objectMapper) {
        this.portfolioRepository = portfolioRepository;
        this.stockHoldingRepository = stockHoldingRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode executeOrder(String email, TradeOrder tradeOrder) throws Exception {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (tradeOrder.getAction().equals(Constants.OrderAction.Buy))
            objectNode.put("cash", executeBuyOrder(email, tradeOrder));
        else if (tradeOrder.getAction().equals(Constants.OrderAction.Sell))
            objectNode.put("cash", executeSellOrder(email, tradeOrder));
        else
            throw new InvalidOrderException(tradeOrder.getAction() + " is an invalid action.");

        return objectNode;
    }

    private BigDecimal executeBuyOrder(String email, TradeOrder tradeOrder) throws Exception {
        BigDecimal totalPrice = getTotalPrice(tradeOrder); // To be subtracted from cash
        // Check if user has enough cash
        Portfolio portfolio = portfolioRepository.getByUser_Email(email);
        if (portfolio.getCash().compareTo(totalPrice) < 0) {
            throw new InvalidOrderException("Insufficient funds!");
        }
        BigDecimal cashLeft = portfolio.getCash().subtract(totalPrice);
        portfolio.setCash(cashLeft);
        addToStockHoldings(totalPrice, portfolio, tradeOrder);
        return cashLeft;
    }

    private BigDecimal executeSellOrder(String email, TradeOrder tradeOrder) throws Exception {
        BigDecimal totalPrice = getTotalPrice(tradeOrder); // To be added to cash
        Portfolio portfolio = portfolioRepository.getByUser_Email(email);
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
                portfolioRepository.save(portfolio);
                return;
            }
        }
        throw new InvalidOrderException("You don't have any shares of this stock.");
    }

    private void addToStockHoldings(BigDecimal totalPrice, Portfolio portfolio, TradeOrder tradeOrder) {
        // Update stock holding it already exists
        for (StockHolding stockHolding: portfolio.getStockHoldings()) {
            if (stockHolding.getSymbol().equals(tradeOrder.getSymbol())) {
                stockHolding.setQuantity(stockHolding.getQuantity() + tradeOrder.getQuantity());
                stockHolding.setPurchasePrice(stockHolding.getPurchasePrice().add(totalPrice));
                stockHolding.setDateTime(LocalDateTime.now());
                return;
            }
        }
        // Otherwise, create a new stock holding
        StockHolding stockHolding = createStockHolding(portfolio, tradeOrder, totalPrice);
        portfolio.getStockHoldings().add(stockHolding);
        portfolioRepository.save(portfolio);
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
        stockHolding.setType(tradeOrder.getType());
        stockHolding.setDateTime(LocalDateTime.now());
        return stockHolding;
    }

    private BigDecimal getTotalPrice(TradeOrder tradeOrder) throws Exception {
        BigDecimal price = fetchPrice(tradeOrder.getSymbol());

        if (tradeOrder.getAction().equals(Constants.OrderAction.Buy)) {
            price = price.add(PRICE_TICK);
        } else if (tradeOrder.getAction().equals(Constants.OrderAction.Sell)) {
            price = price.subtract(PRICE_TICK);
        }

        return price.multiply(BigDecimal.valueOf(tradeOrder.getQuantity()));
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
            LOGGER.error("Couldn't retrieve stock quote: {}", e.getMessage());
        }
        throw new Exception("Couldn't fetch the current stock price.");
    }
}