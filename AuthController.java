package com.rideshare.controller;

import com.rideshare.dto.request.DriverRegisterRequest;
import com.rideshare.dto.request.LoginRequest;
import com.rideshare.dto.request.RegisterRequest;
import com.rideshare.dto.response.ApiResponse;
import com.rideshare.dto.response.AuthResponse;
import com.rideshare.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/rider")
    @Operation(summary = "Register a new rider")
    public ApiResponse<AuthResponse> registerRider(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("Rider registered successfully", authService.registerRider(request));
    }

    @PostMapping("/register/driver")
    @Operation(summary = "Register a new driver")
    public ApiResponse<AuthResponse> registerDriver(@Valid @RequestBody DriverRegisterRequest request) {
        return ApiResponse.success("Driver registered successfully", authService.registerDriver(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login to the system")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("Login successful", authService.login(request));
    }
}
