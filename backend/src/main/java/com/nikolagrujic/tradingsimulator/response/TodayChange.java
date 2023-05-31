package com.nikolagrujic.tradingsimulator.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodayChange {
    private BigDecimal valueChange;
    private double percentageChange;
}