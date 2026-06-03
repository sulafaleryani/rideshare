package com.rideshare.service;

import com.rideshare.dto.request.DriverRegisterRequest;
import com.rideshare.dto.request.LoginRequest;
import com.rideshare.dto.request.RegisterRequest;
import com.rideshare.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse registerRider(RegisterRequest request);
    AuthResponse registerDriver(DriverRegisterRequest request);
    AuthResponse login(LoginRequest request);
}
