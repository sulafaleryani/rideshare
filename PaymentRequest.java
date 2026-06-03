package com.rideshare.dto.request;

import com.rideshare.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Ride ID is required")
    private Long rideId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
