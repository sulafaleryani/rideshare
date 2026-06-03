package com.rideshare.service;

import com.rideshare.dto.request.PaymentRequest;
import com.rideshare.dto.response.PaymentResponse;
import com.rideshare.entity.User;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request, User rider);
    PaymentResponse getPaymentByRideId(Long rideId);
}
