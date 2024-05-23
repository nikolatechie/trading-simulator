package com.nikolagrujic.tradingsimulator.constants;

import org.springframework.stereotype.Component;

@Component
public class Constants {
    // Paths
    public static final String VERIFY_EMAIL_PATH = "/verify-email";
    public static final String RESET_PASSWORD_PATH = "/reset-password";
    public static final String RESET_PASSWORD_BACKEND_PATH = "/user/reset-password";
    public static final String UPDATE_PASSWORD_PATH = "/user/update-password";

    // Validation
    public static final int PASSWORD_MIN_LENGTH = 8;

    // Tokens
    public static final int EMAIL_TOKEN_EXPIRATION_MINUTES = 30;
    public static final int JWT_EXPIRATION_DAYS = 10;

    // Timers
    public static final long REQUEST_DELAY_MILLISECONDS = 2000;

    // Stocks
    public enum StockExchange {
        NASDAQ, NYSE
    }
    public enum OrderAction {
        Buy, Sell
    }
    public enum OrderType {
        Market, Limit, Stop
    }
    public enum OrderDuration {
        IOC, FOK, DAY, GTC
    }

    // Portfolio
    public static final double STARTING_CASH_BALANCE = 30000.00;
}