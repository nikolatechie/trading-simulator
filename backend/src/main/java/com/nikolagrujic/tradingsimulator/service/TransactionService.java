package com.nikolagrujic.tradingsimulator.service;

import com.nikolagrujic.tradingsimulator.constants.Constants;
import com.nikolagrujic.tradingsimulator.model.Transaction;
import com.nikolagrujic.tradingsimulator.repository.TransactionRepository;
import com.nikolagrujic.tradingsimulator.dto.TransactionsStatsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Page<Transaction> getUserTransactions(
        Pageable pageable, String email, String startDateStr, String endDateStr, String searchTerm
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return transactionRepository.findAllByFilter(email, startDateTime, endDateTime, searchTerm, pageable);
    }

    public TransactionsStatsResponse getStats() {
        BigDecimal totalCashFlow = BigDecimal.ZERO;
        BigDecimal totalInvestment = BigDecimal.ZERO;
        Map<String, BigDecimal> values = new HashMap<>();
        String symbol = "";
        BigDecimal cashValue = BigDecimal.ZERO;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Transaction> transactions = transactionRepository.findAllByUser_Email(email);
        for (Transaction transaction: transactions) {
            totalCashFlow = totalCashFlow.add(transaction.getTradePrice()); // Calculating total cash flow
            // Calculating total investment
            if (transaction.getAction().equals(Constants.OrderAction.Buy)) {
                totalInvestment = totalInvestment.add(transaction.getTradePrice());
            }
            // Finding the most traded stock
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
        return new TransactionsStatsResponse(totalCashFlow, totalInvestment, symbol);
    }

    public Page<Transaction> getRecentTransactions(String email, Pageable pageable) {
        return transactionRepository.findAllByUser_Email(email, pageable);
    }

    public List<Transaction> getRecentTransactions(LocalDateTime startDate) {
        return transactionRepository.findAllByDateTimeGreaterThanEqual(startDate);
    }

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}