package com.nikolagrujic.tradingsimulator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    private String alphaVantageApiKey;
    @Value("${rapid.api.key}")
    private String rapidApiKey;
    private final ObjectMapper objectMapper;

    @Autowired
    public ApiKeyController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping("/alpha-vantage")
    public ResponseEntity<?> getAlphaVantageApiKey() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("apiKey", alphaVantageApiKey);
        return ResponseEntity.ok(objectNode);
    }

    @GetMapping("/rapid-api")
    public ResponseEntity<?> getRapidApiKey() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("apiKey", rapidApiKey);
        return ResponseEntity.ok(objectNode);
    }
}