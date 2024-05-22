package com.nikolagrujic.tradingsimulator.controller;

import com.nikolagrujic.tradingsimulator.constants.Constants;
import com.nikolagrujic.tradingsimulator.service.UserService;
import com.nikolagrujic.tradingsimulator.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
public class RegistrationController {
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody User user) {
        LOGGER.info("Registering new user: {}", user.getEmail());
        userService.registerNewUser(user);
        return ResponseEntity.status(201).build(); // Created
    }

    @GetMapping(Constants.VERIFY_EMAIL_PATH)
    public ResponseEntity<?> verifyUserEmail(@RequestParam String token) {
        LOGGER.info("Verifying user with token: {}", token);
        userService.verifyUser(token);
        return ResponseEntity.ok().build();
    }
}