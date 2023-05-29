package com.nikolagrujic.tradingsimulator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nikolagrujic.tradingsimulator.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final ObjectMapper objectMapper;

    @Autowired
    public PortfolioController(PortfolioService portfolioService, ObjectMapper objectMapper) {
        this.portfolioService = portfolioService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/cash")
    public ResponseEntity<?> getAvailableCash() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("cash", portfolioService.getAvailableCash());
        return ResponseEntity.ok(objectNode);
    }

    @GetMapping("/quantity")
    public ResponseEntity<?> getQuantity(@RequestParam String symbol) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("quantity", portfolioService.getQuantity(symbol));
        return ResponseEntity.ok(objectNode);
    }
}