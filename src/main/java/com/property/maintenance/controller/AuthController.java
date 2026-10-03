package com.property.maintenance.controller;

import com.property.maintenance.dto.ApiResponse;
import com.property.maintenance.dto.LoginDto;
import com.property.maintenance.dto.RegisterDto;
import com.property.maintenance.model.User;
import com.property.maintenance.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> login(@RequestBody LoginDto loginDto) {
        User user = authService.login(loginDto);
        return ResponseEntity.ok(ApiResponse.success("Login successful", user));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody RegisterDto registerDto) {
        User user = authService.register(registerDto);
        return new ResponseEntity<>(ApiResponse.success("User registered successfully", user), HttpStatus.CREATED);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = authService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }
}
