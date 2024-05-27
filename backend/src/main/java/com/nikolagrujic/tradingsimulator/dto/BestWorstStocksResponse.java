package com.nikolagrujic.tradingsimulator.dto;

import com.nikolagrujic.tradingsimulator.model.StockHolding;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BestWorstStocksResponse {
    private StockHolding best;
    private StockHolding worst;
}