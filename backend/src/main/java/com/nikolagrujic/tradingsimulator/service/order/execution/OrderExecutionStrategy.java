package com.nikolagrujic.tradingsimulator.service.order.execution;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nikolagrujic.tradingsimulator.model.TradeOrder;

public interface OrderExecutionStrategy {
    ObjectNode executeOrder(String email, TradeOrder tradeOrder) throws Exception;
}