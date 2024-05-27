package com.nikolagrujic.tradingsimulator.dto;

import com.nikolagrujic.tradingsimulator.model.StockInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StocksListResponse {
    private List<StockInfo> data;
}