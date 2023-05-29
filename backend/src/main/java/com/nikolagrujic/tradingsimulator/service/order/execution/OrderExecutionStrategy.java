package com.nikolagrujic.tradingsimulator.service.order.execution;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nikolagrujic.tradingsimulator.model.TradeOrder;
import java.math.BigDecimal;

public interface OrderExecutionStrategy {
    ObjectNode executeOrder(String email, TradeOrder tradeOrder, BigDecimal price) throws Exception;
}