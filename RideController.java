package com.rideshare.controller;

import com.rideshare.dto.request.FareEstimateRequest;
import com.rideshare.dto.request.RideRequest;
import com.rideshare.dto.response.ApiResponse;
import com.rideshare.dto.response.FareEstimateResponse;
import com.rideshare.dto.response.PageResponse;
import com.rideshare.dto.response.RideResponse;
import com.rideshare.entity.User;
import com.rideshare.service.RideService;
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
@RequestMapping("/rides")
@RequiredArgsConstructor
@Tag(name = "Rides", description = "Ride management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class RideController {

    private final RideService rideService;

    @PostMapping("/request")
    @Operation(summary = "Request a new ride")
    public ApiResponse<RideResponse> requestRide(@Valid @RequestBody RideRequest request,
                                                  @AuthenticationPrincipal User rider) {
        return ApiResponse.success("Ride requested successfully", rideService.requestRide(request, rider));
    }

    @PostMapping("/{rideId}/accept")
    @Operation(summary = "Accept a ride (Driver)")
    public ApiResponse<RideResponse> acceptRide(@PathVariable Long rideId,
                                                 @AuthenticationPrincipal User driver) {
        return ApiResponse.success("Ride accepted successfully", rideService.acceptRide(rideId, driver));
    }

    @PostMapping("/{rideId}/start")
    @Operation(summary = "Start a ride (Driver)")
    public ApiResponse<RideResponse> startRide(@PathVariable Long rideId,
                                                @AuthenticationPrincipal User driver) {
        return ApiResponse.success("Ride started successfully", rideService.startRide(rideId, driver));
    }

    @PostMapping("/{rideId}/complete")
    @Operation(summary = "Complete a ride (Driver)")
    public ApiResponse<RideResponse> completeRide(@PathVariable Long rideId,
                                                   @AuthenticationPrincipal User driver) {
        return ApiResponse.success("Ride completed successfully", rideService.completeRide(rideId, driver));
    }

    @PostMapping("/{rideId}/cancel")
    @Operation(summary = "Cancel a ride")
    public ApiResponse<RideResponse> cancelRide(@PathVariable Long rideId,
                                                 @AuthenticationPrincipal User user) {
        return ApiResponse.success("Ride cancelled successfully", rideService.cancelRide(rideId, user));
    }

    @GetMapping("/{rideId}")
    @Operation(summary = "Get ride details by ID")
    public ApiResponse<RideResponse> getRideById(@PathVariable Long rideId) {
        return ApiResponse.success(rideService.getRideById(rideId));
    }

    @GetMapping("/current")
    @Operation(summary = "Get current active ride for rider")
    public ApiResponse<RideResponse> getCurrentRide(@AuthenticationPrincipal User rider) {
        return ApiResponse.success(rideService.getCurrentRide(rider));
    }

    @GetMapping("/history")
    @Operation(summary = "Get ride history for rider")
    public ApiResponse<PageResponse<RideResponse>> getRideHistory(
            @AuthenticationPrincipal User rider,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(rideService.getRideHistory(rider, pageable));
    }

    @GetMapping("/driver/history")
    @Operation(summary = "Get ride history for driver")
    public ApiResponse<PageResponse<RideResponse>> getDriverRides(
            @AuthenticationPrincipal User driver,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(rideService.getDriverRides(driver, pageable));
    }

    @PostMapping("/estimate-fare")
    @Operation(summary = "Estimate fare for a ride")
    public ApiResponse<FareEstimateResponse> estimateFare(@Valid @RequestBody FareEstimateRequest request) {
        return ApiResponse.success(rideService.estimateFare(request));
    }
}
