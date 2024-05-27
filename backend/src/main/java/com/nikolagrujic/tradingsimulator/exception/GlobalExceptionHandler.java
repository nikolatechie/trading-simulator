package com.nikolagrujic.tradingsimulator.exception;

import com.nikolagrujic.tradingsimulator.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotRegisteredException.class)
    public ResponseEntity<?> handleUserNotRegisteredException(UserNotRegisteredException e) {
        LOGGER.error("User is not registered: {}", e.getUserEmail());
        return ResponseEntity.status(401).body(new ErrorResponse("You must register before logging in."));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        LOGGER.error("User {} already exists", e.getUserEmail());
        return ResponseEntity.status(409).body(new ErrorResponse("Account with this email already exists!"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        LOGGER.error("Authentication failed: {}", e.getMessage());
        return ResponseEntity.status(401).body(new ErrorResponse("The password is incorrect."));
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<?> handleExpiredTokenException(ExpiredTokenException e) {
        return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException e) {
        LOGGER.error("The token is invalid: {}", e.getToken());
        return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(IncompleteBodyException.class)
    public ResponseEntity<?> handleIncompleteBodyException(IncompleteBodyException e) {
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(InvalidArticleException.class)
    public ResponseEntity<?> handleInvalidArticleException(InvalidArticleException e) {
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        // Default exception
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
    }
}