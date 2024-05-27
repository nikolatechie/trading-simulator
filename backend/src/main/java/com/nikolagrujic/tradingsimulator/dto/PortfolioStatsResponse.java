package com.nikolagrujic.tradingsimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioStatsResponse {
    private BigDecimal totalValue;
    private BigDecimal totalGainOrLoss;
}