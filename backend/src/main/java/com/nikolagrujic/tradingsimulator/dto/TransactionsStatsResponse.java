package com.nikolagrujic.tradingsimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsStatsResponse {
    private BigDecimal totalCashFlow;
    private BigDecimal totalInvestment;
    private String mostTradedStock;
}