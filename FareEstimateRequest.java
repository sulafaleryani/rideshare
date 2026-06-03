package com.rideshare.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FareEstimateRequest {

    @NotNull(message = "Pickup latitude is required")
    private Double pickupLatitude;

    @NotNull(message = "Pickup longitude is required")
    private Double pickupLongitude;

    @NotNull(message = "Destination latitude is required")
    private Double destinationLatitude;

    @NotNull(message = "Destination longitude is required")
    private Double destinationLongitude;
}
