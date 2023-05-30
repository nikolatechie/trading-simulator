package com.nikolagrujic.tradingsimulator.response;

import com.nikolagrujic.tradingsimulator.model.StockHolding;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HoldingsResponse {
    private String twelveDataApiKey;
    private Page<StockHolding> holdings;
}