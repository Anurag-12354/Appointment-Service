package com.anurag.appointmentbooking.controller;

import com.anurag.appointmentbooking.dto.LoginRequest;
import com.anurag.appointmentbooking.dto.LoginResponse;
import com.anurag.appointmentbooking.dto.RegisterRequest;
import com.anurag.appointmentbooking.dto.RegisterResponse;
import com.anurag.appointmentbooking.service.AuthService;
import com.anurag.appointmentbooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(
            UserService userService,
            AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                authService.login(request));
    }
}