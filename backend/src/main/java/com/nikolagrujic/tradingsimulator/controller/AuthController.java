package com.nikolagrujic.tradingsimulator.controller;

import com.nikolagrujic.tradingsimulator.dto.JwtDto;
import com.nikolagrujic.tradingsimulator.dto.LoginRequest;
import com.nikolagrujic.tradingsimulator.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LOGGER.info("Authenticating user: {}", loginRequest.getEmail());
        return authService.login(loginRequest);
    }

    @PostMapping(path = "/check-jwt-expiry")
    public ResponseEntity<?> checkJwtForExpiry(@RequestBody JwtDto jwtDto) {
        LOGGER.info("Checking JWT for expiry: {}", jwtDto.getJwt());
        return authService.checkJwtForExpiry(jwtDto);
    }
}