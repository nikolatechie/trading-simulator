package com.nikolagrujic.tradingsimulator.service;

import com.nikolagrujic.tradingsimulator.constants.Constants.StockExchange;
import com.nikolagrujic.tradingsimulator.model.StockInfo;
import com.nikolagrujic.tradingsimulator.model.StocksListResponse;
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
import java.net.URI;
import java.util.List;
import java.util.Objects;

@Service
public class StocksService {
    @Value("${twelvedata.api.key}")
    private String apiKey;
    @Value("${twelvedata.api.host}")
    private String apiHost;
    private static final String STOCKS_ENDPOINT = "https://twelve-data1.p.rapidapi.com/stocks";
    private final RestTemplate restTemplate = new RestTemplate();
    private final StockInfoRepository stockInfoRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(StocksService.class);

    @Autowired
    public StocksService(StockInfoRepository stockInfoRepository) {
        this.stockInfoRepository = stockInfoRepository;
    }

    public Page<StockInfo> getListOfStockInfo(String search, Pageable pageable) {
        if (search == null || search.length() < 2) return stockInfoRepository.findAll(pageable);
        return stockInfoRepository.findAll(search, pageable);
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
}