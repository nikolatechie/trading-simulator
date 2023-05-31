package com.nikolagrujic.tradingsimulator.exception;

public class HistoryExistsException extends RuntimeException {
    public HistoryExistsException(String message) {
        super(message);
    }
}