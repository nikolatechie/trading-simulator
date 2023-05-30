package com.nikolagrujic.tradingsimulator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nikolagrujic.tradingsimulator.model.StockHolding;
import com.nikolagrujic.tradingsimulator.response.ErrorResponse;
import com.nikolagrujic.tradingsimulator.response.HoldingsResponse;
import com.nikolagrujic.tradingsimulator.service.PortfolioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {
    @Value("${twelvedata.api.key}")
    private String twelveDataApiKey;
    private final PortfolioService portfolioService;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioController.class);

    @Autowired
    public PortfolioController(PortfolioService portfolioService, ObjectMapper objectMapper) {
        this.portfolioService = portfolioService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/holdings")
    public ResponseEntity<?> getPortfolioHoldings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "dateTime") String sortBy
    ) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            LOGGER.info("Retrieving portfolio holdings: {}", email);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
            Page<StockHolding> holdings = portfolioService.getHoldingsByEmail(pageable, email);
            return ResponseEntity.ok(new HoldingsResponse(twelveDataApiKey, holdings));
        } catch (Exception e) {
            LOGGER.error("Couldn't retrieve portfolio holdings: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                new ErrorResponse(e.getMessage())
            );
        }
    }

    @GetMapping("/cash")
    public ResponseEntity<?> getAvailableCash() {
        try {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("cash", portfolioService.getAvailableCash());
            return ResponseEntity.ok(objectNode);
        } catch (Exception e) {
            LOGGER.error("Couldn't retrieve user cash: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                new ErrorResponse(e.getMessage())
            );
        }
    }

    @GetMapping("/quantity")
    public ResponseEntity<?> getQuantity(@RequestParam String symbol) {
        try {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("quantity", portfolioService.getQuantity(symbol));
            return ResponseEntity.ok(objectNode);
        } catch (Exception e) {
            LOGGER.error("[{}] Couldn't retrieve stock quantity: {}", symbol, e.getMessage());
            return ResponseEntity.status(500).body(
                new ErrorResponse(e.getMessage())
            );
        }
    }
}