package com.rideshare.service;

import com.rideshare.dto.request.FareEstimateRequest;
import com.rideshare.dto.request.RideRequest;
import com.rideshare.dto.response.FareEstimateResponse;
import com.rideshare.dto.response.PageResponse;
import com.rideshare.dto.response.RideResponse;
import com.rideshare.entity.User;
import org.springframework.data.domain.Pageable;

public interface RideService {
    RideResponse requestRide(RideRequest request, User rider);
    RideResponse acceptRide(Long rideId, User driver);
    RideResponse startRide(Long rideId, User driver);
    RideResponse completeRide(Long rideId, User driver);
    RideResponse cancelRide(Long rideId, User user);
    RideResponse getRideById(Long rideId);
    RideResponse getCurrentRide(User rider);
    PageResponse<RideResponse> getRideHistory(User rider, Pageable pageable);
    PageResponse<RideResponse> getDriverRides(User driver, Pageable pageable);
    FareEstimateResponse estimateFare(FareEstimateRequest request);
    PageResponse<RideResponse> getAllRides(Pageable pageable);
}
