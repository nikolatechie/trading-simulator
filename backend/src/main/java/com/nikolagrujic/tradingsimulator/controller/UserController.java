package com.nikolagrujic.tradingsimulator.controller;

import com.nikolagrujic.tradingsimulator.dto.LoginRequest;
import com.nikolagrujic.tradingsimulator.dto.ResetPasswordRequest;
import com.nikolagrujic.tradingsimulator.dto.UserDto;
import com.nikolagrujic.tradingsimulator.dto.ErrorResponse;
import com.nikolagrujic.tradingsimulator.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @GetMapping("/fullName")
    public ResponseEntity<?> getUserFullName() {
        return ResponseEntity.ok(userService.getFullName());
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserDto userDto) {
        try {
            return ResponseEntity.ok(userService.updateUser(userDto));
        } catch (Exception e) {
            LOGGER.error("Failed to update user: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                new ErrorResponse(e.getMessage())
            );
        }
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody LoginRequest loginRequest) {
        return userService.sendResetPasswordLink(loginRequest);
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> updatePasswordAfterResetRequest(@Valid @RequestBody ResetPasswordRequest request) {
        userService.updatePassword(request);
        return ResponseEntity.ok().build();
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