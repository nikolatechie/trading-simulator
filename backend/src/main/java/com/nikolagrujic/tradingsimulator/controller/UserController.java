package com.nikolagrujic.tradingsimulator.controller;

import com.nikolagrujic.tradingsimulator.model.UserDto;
import com.nikolagrujic.tradingsimulator.response.ErrorResponse;
import com.nikolagrujic.tradingsimulator.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/settingsInfo")
    public ResponseEntity<?> getUserSettingsInfo() {
        try {
            return ResponseEntity.ok(userService.getUserSettingsInfo());
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve user info: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                new ErrorResponse(e.getMessage())
            );
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserDto userDto) {
        try {
            return ResponseEntity.ok(userService.updateUser(userDto));
        } catch (Exception e) {
            LOGGER.error("Failed to update user: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                new ErrorResponse(e.getMessage())
            );
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAccount() {
        try {
            userService.deleteAccount();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            LOGGER.error("Failed to delete account: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                new ErrorResponse(e.getMessage())
            );
        }
    }
}