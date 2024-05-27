package com.nikolagrujic.tradingsimulator.controller;

import com.nikolagrujic.tradingsimulator.model.Transaction;
import com.nikolagrujic.tradingsimulator.dto.ErrorResponse;
import com.nikolagrujic.tradingsimulator.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<?> getUserTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateTime") String sortBy,
            @RequestParam(defaultValue = "false") boolean recent,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String searchTerm
    ) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            LOGGER.info("Retrieving user transactions: {}", email);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
            Page<Transaction> transactions = (recent) ?
                    transactionService.getRecentTransactions(email, pageable) :
                    transactionService.getUserTransactions(pageable, email, startDate, endDate, searchTerm);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            LOGGER.error("Couldn't retrieve transactions: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                new ErrorResponse(e.getMessage())
            );
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            return ResponseEntity.ok(transactionService.getStats());
        } catch (Exception e) {
            LOGGER.error("Couldn't retrieve stats: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                new ErrorResponse(e.getMessage())
            );
        }
    }
}