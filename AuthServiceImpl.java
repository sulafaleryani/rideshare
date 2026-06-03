package com.rideshare.service.impl;

import com.rideshare.dto.request.DriverRegisterRequest;
import com.rideshare.dto.request.LoginRequest;
import com.rideshare.dto.request.RegisterRequest;
import com.rideshare.dto.response.AuthResponse;
import com.rideshare.entity.Driver;
import com.rideshare.entity.Role;
import com.rideshare.entity.User;
import com.rideshare.exception.BusinessException;
import com.rideshare.repository.DriverRepository;
import com.rideshare.repository.UserRepository;
import com.rideshare.security.JwtTokenProvider;
import com.rideshare.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse registerRider(RegisterRequest request) {
        validateEmailUniqueness(request.getEmail());

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.RIDER)
                .active(true)
                .build();

        user = userRepository.save(user);
        log.info("Registered new rider: {}", user.getEmail());

        String token = jwtTokenProvider.generateToken(user);
        return buildAuthResponse(user, token);
    }

    @Override
    @Transactional
    public AuthResponse registerDriver(DriverRegisterRequest request) {
        validateEmailUniqueness(request.getEmail());

        if (driverRepository.existsByVehiclePlate(request.getVehiclePlate())) {
            throw new BusinessException("Vehicle plate already registered: " + request.getVehiclePlate());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.DRIVER)
                .active(true)
                .build();

        user = userRepository.save(user);

        Driver driver = Driver.builder()
                .user(user)
                .vehicleType(request.getVehicleType())
                .vehiclePlate(request.getVehiclePlate().toUpperCase())
                .available(false)
                .suspended(false)
                .build();

        driverRepository.save(driver);
        log.info("Registered new driver: {}", user.getEmail());

        String token = jwtTokenProvider.generateToken(user);
        return buildAuthResponse(user, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(user);

        log.info("User logged in: {}", user.getEmail());
        return buildAuthResponse(user, token);
    }

    private void validateEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email.toLowerCase())) {
            throw new BusinessException("Email already registered: " + email);
        }
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
