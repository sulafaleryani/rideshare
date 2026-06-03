package com.rideshare.service.impl;

import com.rideshare.dto.request.PaymentRequest;
import com.rideshare.dto.response.PaymentResponse;
import com.rideshare.entity.*;
import com.rideshare.exception.BusinessException;
import com.rideshare.exception.ResourceNotFoundException;
import com.rideshare.repository.PaymentRepository;
import com.rideshare.repository.RideRepository;
import com.rideshare.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RideRepository rideRepository;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, User rider) {
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new ResourceNotFoundException("Ride", "id", request.getRideId()));

        if (!ride.getRider().getId().equals(rider.getId())) {
            throw new BusinessException("You are not authorized to pay for this ride");
        }

        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new BusinessException("Payment can only be processed for completed rides");
        }

        if (paymentRepository.findByRide(ride).isPresent()) {
            throw new BusinessException("Payment already processed for this ride");
        }

        Payment payment = Payment.builder()
                .ride(ride)
                .amount(ride.getFare())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.COMPLETED)
                .transactionReference(generateTransactionReference())
                .build();

        payment = paymentRepository.save(payment);

        log.info("Payment processed for ride {}: amount {}, transaction {}",
                ride.getId(), payment.getAmount(), payment.getTransactionReference());

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByRideId(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride", "id", rideId));

        Payment payment = paymentRepository.findByRide(ride)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for ride: " + rideId));

        return mapToResponse(payment);
    }

    private String generateTransactionReference() {
        return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .rideId(payment.getRide().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionReference(payment.getTransactionReference())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
