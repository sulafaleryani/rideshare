package com.rideshare.service.impl;

import com.rideshare.dto.request.UpdateLocationRequest;
import com.rideshare.dto.response.DriverResponse;
import com.rideshare.dto.response.PageResponse;
import com.rideshare.entity.Driver;
import com.rideshare.entity.User;
import com.rideshare.exception.BusinessException;
import com.rideshare.exception.ResourceNotFoundException;
import com.rideshare.repository.DriverRepository;
import com.rideshare.repository.UserRepository;
import com.rideshare.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DriverResponse updateAvailability(User user, boolean available) {
        Driver driver = getDriverByUser(user);

        if (driver.isSuspended() && available) {
            throw new BusinessException("Cannot update availability: Driver account is suspended");
        }

        driver.setAvailable(available);
        driver = driverRepository.save(driver);

        log.info("Driver {} set availability to {}", user.getEmail(), available);
        return mapToResponse(driver);
    }

    @Override
    @Transactional
    public DriverResponse updateLocation(User user, UpdateLocationRequest request) {
        Driver driver = getDriverByUser(user);

        driver.setCurrentLatitude(request.getLatitude());
        driver.setCurrentLongitude(request.getLongitude());
        driver = driverRepository.save(driver);

        log.info("Driver {} updated location to ({}, {})", user.getEmail(), request.getLatitude(), request.getLongitude());
        return mapToResponse(driver);
    }

    @Override
    public DriverResponse getDriverProfile(User user) {
        Driver driver = getDriverByUser(user);
        return mapToResponse(driver);
    }

    @Override
    public DriverResponse getDriverById(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        return mapToResponse(driver);
    }

    @Override
    public PageResponse<DriverResponse> getAllDrivers(Pageable pageable) {
        Page<Driver> drivers = driverRepository.findAll(pageable);
        Page<DriverResponse> responsePage = drivers.map(this::mapToResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional
    public DriverResponse suspendDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));

        driver.setSuspended(true);
        driver.setAvailable(false);
        driver = driverRepository.save(driver);

        User user = driver.getUser();
        user.setActive(false);
        userRepository.save(user);

        log.info("Driver {} suspended", driver.getUser().getEmail());
        return mapToResponse(driver);
    }

    @Override
    @Transactional
    public DriverResponse unsuspendDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));

        driver.setSuspended(false);
        driver = driverRepository.save(driver);

        User user = driver.getUser();
        user.setActive(true);
        userRepository.save(user);

        log.info("Driver {} unsuspended", driver.getUser().getEmail());
        return mapToResponse(driver);
    }

    private Driver getDriverByUser(User user) {
        return driverRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found for user: " + user.getEmail()));
    }

    private DriverResponse mapToResponse(Driver driver) {
        return DriverResponse.builder()
                .id(driver.getId())
                .userId(driver.getUser().getId())
                .fullName(driver.getUser().getFullName())
                .email(driver.getUser().getEmail())
                .vehicleType(driver.getVehicleType())
                .vehiclePlate(driver.getVehiclePlate())
                .currentLatitude(driver.getCurrentLatitude())
                .currentLongitude(driver.getCurrentLongitude())
                .available(driver.isAvailable())
                .suspended(driver.isSuspended())
                .build();
    }
}
