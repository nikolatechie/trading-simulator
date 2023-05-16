package com.nikolagrujic.tradingsimulator.model;

import com.nikolagrujic.tradingsimulator.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TradeOrder {
    private String symbol;
    private Constants.OrderAction action;
    private int quantity;
    private Constants.OrderType type;
    private BigDecimal price;
    private Constants.OrderDuration duration;

    @Override
    public String toString() {
        return
            "TradeOrder{" +
            "action=" + action +
            ", quantity=" + quantity +
            ", type=" + type +
            ", price=" + price +
            ", duration=" + duration +
            '}';
    }
}