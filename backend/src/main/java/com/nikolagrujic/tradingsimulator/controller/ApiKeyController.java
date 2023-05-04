package com.nikolagrujic.tradingsimulator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/key")
public class ApiKeyController {
    @Value("${alphavantage.api.key}")
    private String apiKey;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyController.class);

    @Autowired
    public ApiKeyController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping("/alpha-vantage")
    public ResponseEntity<?> getAlphaVantageKey() {
        LOGGER.info("Received a request for the AlphaVantage API key...");
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("apiKey", apiKey);
        return ResponseEntity.ok(objectNode);
    }
}