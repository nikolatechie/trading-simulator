package com.nikolagrujic.tradingsimulator.controller;

import com.nikolagrujic.tradingsimulator.exception.UserAlreadyExistsException;
import com.nikolagrujic.tradingsimulator.response.ErrorResponse;
import com.nikolagrujic.tradingsimulator.service.UserService;
import lombok.AllArgsConstructor;
import com.nikolagrujic.tradingsimulator.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
