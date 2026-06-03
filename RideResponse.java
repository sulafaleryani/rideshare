package com.rideshare.dto.response;

import com.rideshare.entity.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideResponse {
    private Long id;
    private Long riderId;
    private String riderName;
    private Long driverId;
    private String driverName;
    private String vehiclePlate;
    private String pickupLocation;
    private String destinationLocation;
    private Double pickupLatitude;
    private Double pickupLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private BigDecimal fare;
    private RideStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
