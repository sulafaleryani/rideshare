package com.rideshare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String vehicleType;
    private String vehiclePlate;
    private Double currentLatitude;
    private Double currentLongitude;
    private boolean available;
    private boolean suspended;
}
