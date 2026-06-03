package com.rideshare.controller;

import com.rideshare.dto.request.PaymentRequest;
import com.rideshare.dto.response.ApiResponse;
import com.rideshare.dto.response.PaymentResponse;
import com.rideshare.entity.User;
import com.rideshare.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing endpoints")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @Operation(summary = "Process payment for a ride")
    public ApiResponse<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request,
                                                        @AuthenticationPrincipal User rider) {
        return ApiResponse.success("Payment processed successfully", paymentService.processPayment(request, rider));
    }

    @GetMapping("/ride/{rideId}")
    @Operation(summary = "Get payment details by ride ID")
    public ApiResponse<PaymentResponse> getPaymentByRideId(@PathVariable Long rideId) {
        return ApiResponse.success(paymentService.getPaymentByRideId(rideId));
    }
}
