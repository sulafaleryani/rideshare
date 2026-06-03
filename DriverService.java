package com.rideshare.service;

import com.rideshare.dto.request.UpdateLocationRequest;
import com.rideshare.dto.response.DriverResponse;
import com.rideshare.dto.response.PageResponse;
import com.rideshare.entity.User;
import org.springframework.data.domain.Pageable;

public interface DriverService {
    DriverResponse updateAvailability(User user, boolean available);
    DriverResponse updateLocation(User user, UpdateLocationRequest request);
    DriverResponse getDriverProfile(User user);
    DriverResponse getDriverById(Long driverId);
    PageResponse<DriverResponse> getAllDrivers(Pageable pageable);
    DriverResponse suspendDriver(Long driverId);
    DriverResponse unsuspendDriver(Long driverId);
}
