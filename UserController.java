package com.rideshare.controller;

import com.rideshare.dto.request.UpdateLocationRequest;
import com.rideshare.dto.response.ApiResponse;
import com.rideshare.dto.response.DriverResponse;
import com.rideshare.dto.response.PageResponse;
import com.rideshare.entity.User;
import com.rideshare.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
@Tag(name = "Driver", description = "Driver management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DriverController {

    private final DriverService driverService;

    @PutMapping("/availability")
    @Operation(summary = "Update driver availability")
    public ApiResponse<DriverResponse> updateAvailability(@RequestParam boolean available,
                                                           @AuthenticationPrincipal User user) {
        return ApiResponse.success("Availability updated", driverService.updateAvailability(user, available));
    }

    @PutMapping("/location")
    @Operation(summary = "Update driver current location")
    public ApiResponse<DriverResponse> updateLocation(@Valid @RequestBody UpdateLocationRequest request,
                                                       @AuthenticationPrincipal User user) {
        return ApiResponse.success("Location updated", driverService.updateLocation(user, request));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get driver profile")
    public ApiResponse<DriverResponse> getDriverProfile(@AuthenticationPrincipal User user) {
        return ApiResponse.success(driverService.getDriverProfile(user));
    }

    @GetMapping("/{driverId}")
    @Operation(summary = "Get driver by ID")
    public ApiResponse<DriverResponse> getDriverById(@PathVariable Long driverId) {
        return ApiResponse.success(driverService.getDriverById(driverId));
    }
}
