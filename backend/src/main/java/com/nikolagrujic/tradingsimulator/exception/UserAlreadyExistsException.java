package com.nikolagrujic.tradingsimulator.exception;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final String userEmail;

    public UserAlreadyExistsException(String message, String userEmail) {
        super(message);
        this.userEmail = userEmail;
    }
}
