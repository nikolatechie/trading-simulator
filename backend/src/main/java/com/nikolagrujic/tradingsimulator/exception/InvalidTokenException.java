package com.nikolagrujic.tradingsimulator.exception;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {
    private final String token;

    public InvalidTokenException(String message, String token) {
        super(message);
        this.token = token;
    }
}