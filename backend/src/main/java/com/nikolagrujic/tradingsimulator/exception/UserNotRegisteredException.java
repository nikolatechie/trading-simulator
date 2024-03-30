package com.nikolagrujic.tradingsimulator.exception;

import lombok.Getter;

@Getter
public class UserNotRegisteredException extends RuntimeException {
    private final String userEmail;

    public UserNotRegisteredException(String message, String userEmail) {
        super(message);
        this.userEmail = userEmail;
    }
}