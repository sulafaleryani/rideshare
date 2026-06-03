package com.rideshare.service.impl;

import com.rideshare.dto.request.FareEstimateRequest;
import com.rideshare.dto.request.RideRequest;
import com.rideshare.dto.response.FareEstimateResponse;
import com.rideshare.dto.response.PageResponse;
import com.rideshare.dto.response.RideResponse;
import com.rideshare.entity.*;
import com.rideshare.exception.BusinessException;
import com.rideshare.exception.ResourceNotFoundException;
import com.rideshare.repository.DriverRepository;
import com.rideshare.repository.RideRepository;
import com.rideshare.service.HaversineService;
import com.rideshare.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final HaversineService haversineService;

    private static final BigDecimal BASE_FARE = BigDecimal.valueOf(2.50);
    private static final BigDecimal PER_KM_RATE = BigDecimal.valueOf(1.50);

    @Override
    @Transactional
    public RideResponse requestRide(RideRequest request, User rider) {
        validateNoActiveRide(rider);

        Ride ride = Ride.builder()
                .rider(rider)
                .pickupLocation(request.getPickupLocation())
                .destinationLocation(request.getDestinationLocation())
                .pickupLatitude(request.getPickupLatitude())
                .pickupLongitude(request.getPickupLongitude())
                .destinationLatitude(request.getDestinationLatitude())
                .destinationLongitude(request.getDestinationLongitude())
                .status(RideStatus.REQUESTED)
                .build();

        ride = rideRepository.save(ride);
        log.info("Ride requested: {} for rider: {}", ride.getId(), rider.getEmail());

        return mapToResponse(ride);
    }

    @Override
    @Transactional
    public RideResponse acceptRide(Long rideId, User driver) {
        Ride ride = getRideByIdEntity(rideId);
        Driver driverEntity = getDriverByUser(driver);

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new BusinessException("Ride cannot be accepted in current status: " + ride.getStatus());
        }

        if (driverEntity.isSuspended()) {
            throw new BusinessException("Your account is suspended");
        }

        ride.setDriver(driverEntity);
        ride.setStatus(RideStatus.ACCEPTED);
        ride = rideRepository.save(ride);

        log.info("Ride {} accepted by driver {}", rideId, driver.getEmail());
        return mapToResponse(ride);
    }

    @Override
    @Transactional
    public RideResponse startRide(Long rideId, User driver) {
        Ride ride = getRideByIdEntity(rideId);
        validateDriverAccess(ride, driver);

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new BusinessException("Ride must be accepted before starting");
        }

        ride.setStatus(RideStatus.IN_PROGRESS);
        ride = rideRepository.save(ride);

        log.info("Ride {} started", rideId);
        return mapToResponse(ride);
    }

    @Override
    @Transactional
    public RideResponse completeRide(Long rideId, User driver) {
        Ride ride = getRideByIdEntity(rideId);
        validateDriverAccess(ride, driver);

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new BusinessException("Ride must be in progress to complete");
        }

        double distance = haversineService.calculateDistance(
                ride.getPickupLatitude(), ride.getPickupLongitude(),
                ride.getDestinationLatitude(), ride.getDestinationLongitude()
        );

        BigDecimal fare = calculateFare(distance);
        ride.setFare(fare);
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());

        ride = rideRepository.save(ride);

        log.info("Ride {} completed. Distance: {} km, Fare: {}", rideId, distance, fare);
        return mapToResponse(ride);
    }

    @Override
    @Transactional
    public RideResponse cancelRide(Long rideId, User user) {
        Ride ride = getRideByIdEntity(rideId);

        if (!ride.getRider().getId().equals(user.getId()) &&
            (ride.getDriver() == null || !ride.getDriver().getUser().getId().equals(user.getId()))) {
            throw new BusinessException("You are not authorized to cancel this ride");
        }

        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new BusinessException("Cannot cancel a completed or already cancelled ride");
        }

        ride.setStatus(RideStatus.CANCELLED);
        ride = rideRepository.save(ride);

        log.info("Ride {} cancelled by user {}", rideId, user.getEmail());
        return mapToResponse(ride);
    }

    @Override
    public RideResponse getRideById(Long rideId) {
        return mapToResponse(getRideByIdEntity(rideId));
    }

    @Override
    public RideResponse getCurrentRide(User rider) {
        List<RideStatus> excludedStatuses = List.of(RideStatus.COMPLETED, RideStatus.CANCELLED);
        return rideRepository.findTopByRiderAndStatusNotInOrderByCreatedAtDesc(rider, excludedStatuses)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No active ride found"));
    }

    @Override
    public PageResponse<RideResponse> getRideHistory(User rider, Pageable pageable) {
        Page<Ride> rides = rideRepository.findAllByRiderOrderByCreatedAtDesc(rider, pageable);
        Page<RideResponse> responsePage = rides.map(this::mapToResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    public PageResponse<RideResponse> getDriverRides(User driver, Pageable pageable) {
        Driver driverEntity = getDriverByUser(driver);
        Page<Ride> rides = rideRepository.findAllByDriverOrderByCreatedAtDesc(driverEntity, pageable);
        Page<RideResponse> responsePage = rides.map(this::mapToResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    public FareEstimateResponse estimateFare(FareEstimateRequest request) {
        double distance = haversineService.calculateDistance(
                request.getPickupLatitude(), request.getPickupLongitude(),
                request.getDestinationLatitude(), request.getDestinationLongitude()
        );

        BigDecimal estimatedFare = calculateFare(distance);

        return FareEstimateResponse.builder()
                .distanceKm(distance)
                .estimatedFare(estimatedFare)
                .currency("USD")
                .build();
    }

    @Override
    public PageResponse<RideResponse> getAllRides(Pageable pageable) {
        Page<Ride> rides = rideRepository.findAll(pageable);
        Page<RideResponse> responsePage = rides.map(this::mapToResponse);
        return PageResponse.from(responsePage);
    }

    private Ride getRideByIdEntity(Long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride", "id", rideId));
    }

    private Driver getDriverByUser(User user) {
        return driverRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found for user: " + user.getEmail()));
    }

    private void validateNoActiveRide(User rider) {
        List<RideStatus> excludedStatuses = List.of(RideStatus.COMPLETED, RideStatus.CANCELLED);
        var activeRide = rideRepository.findTopByRiderAndStatusNotInOrderByCreatedAtDesc(rider, excludedStatuses);
        if (activeRide.isPresent()) {
            throw new BusinessException("You already have an active ride");
        }
    }

    private void validateDriverAccess(Ride ride, User driver) {
        if (ride.getDriver() == null || !ride.getDriver().getUser().getId().equals(driver.getId())) {
            throw new BusinessException("You are not assigned to this ride");
        }
    }

    private BigDecimal calculateFare(double distanceKm) {
        BigDecimal distanceDecimal = BigDecimal.valueOf(distanceKm);
        BigDecimal fare = BASE_FARE.add(PER_KM_RATE.multiply(distanceDecimal));
        return fare.setScale(2, RoundingMode.HALF_UP);
    }

    private RideResponse mapToResponse(Ride ride) {
        return RideResponse.builder()
                .id(ride.getId())
                .riderId(ride.getRider().getId())
                .riderName(ride.getRider().getFullName())
                .driverId(ride.getDriver() != null ? ride.getDriver().getId() : null)
                .driverName(ride.getDriver() != null ? ride.getDriver().getUser().getFullName() : null)
                .vehiclePlate(ride.getDriver() != null ? ride.getDriver().getVehiclePlate() : null)
                .pickupLocation(ride.getPickupLocation())
                .destinationLocation(ride.getDestinationLocation())
                .pickupLatitude(ride.getPickupLatitude())
                .pickupLongitude(ride.getPickupLongitude())
                .destinationLatitude(ride.getDestinationLatitude())
                .destinationLongitude(ride.getDestinationLongitude())
                .fare(ride.getFare())
                .status(ride.getStatus())
                .createdAt(ride.getCreatedAt())
                .completedAt(ride.getCompletedAt())
                .build();
    }
}
