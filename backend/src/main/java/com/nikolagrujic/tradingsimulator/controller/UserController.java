package com.nikolagrujic.tradingsimulator.controller;

import com.nikolagrujic.tradingsimulator.exception.ExpiredTokenException;
import com.nikolagrujic.tradingsimulator.exception.InvalidTokenException;
import com.nikolagrujic.tradingsimulator.exception.UserAlreadyExistsException;
import com.nikolagrujic.tradingsimulator.response.ErrorResponse;
import com.nikolagrujic.tradingsimulator.service.UserService;
import lombok.AllArgsConstructor;
import com.nikolagrujic.tradingsimulator.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping(path = "/register")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody User user) {
        try {
            userService.registerNewUser(user);
            return ResponseEntity.status(201).build(); // Created
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(409).body(new ErrorResponse(e.getMessage())); // Conflict
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String token) {
        try {
            userService.verifyUser(token);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        } catch (ExpiredTokenException e) {
            return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
        }

        return ResponseEntity.ok().build();
    }
}
