package com.nikolagrujic.tradingsimulator.controller;

import com.nikolagrujic.tradingsimulator.model.StockInfo;
import com.nikolagrujic.tradingsimulator.dto.ErrorResponse;
import com.nikolagrujic.tradingsimulator.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stocks")
public class StocksController {
    private final StockService stockService;
    private static final Logger LOGGER = LoggerFactory.getLogger(StocksController.class);

    @Autowired
    public StocksController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<?> getListOfStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        try {
            LOGGER.info("Retrieving a list of stocks (page = {})", page);
            Pageable pageable = PageRequest.of(page, size);
            Page<StockInfo> stocks = stockService.getListOfStockInfo(search, pageable);
            return ResponseEntity.ok().body(stocks);
        } catch (Exception e) {
            LOGGER.error("Couldn't retrieve list of stocks: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                new ErrorResponse(e.getMessage())
            );
        }
    }

    @GetMapping("/recommendation")
    public ResponseEntity<?> getRecommendedStock() {
        try {
            return ResponseEntity.ok(stockService.getRecommendedStock());
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve recommended stocks: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                new ErrorResponse(e.getMessage())
            );
        }
    }
}