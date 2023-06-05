package com.nikolagrujic.tradingsimulator.service;

import com.nikolagrujic.tradingsimulator.model.Transaction;
import com.nikolagrujic.tradingsimulator.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Page<Transaction> getUserTransactions(Pageable pageable, String email) {
        return transactionRepository.findAllByUser_Email(pageable, email);
    }

    public List<Transaction> getRecentTransactions(LocalDateTime startDate) {
        return transactionRepository.findAllByDateTimeGreaterThanEqual(startDate);
    }

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}