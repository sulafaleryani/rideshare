package com.rideshare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id", nullable = false)
    private User rider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Column(nullable = false)
    private String pickupLocation;

    @Column(nullable = false)
    private String destinationLocation;

    @Column(nullable = false)
    private Double pickupLatitude;

    @Column(nullable = false)
    private Double pickupLongitude;

    @Column(nullable = false)
    private Double destinationLatitude;

    @Column(nullable = false)
    private Double destinationLongitude;

    @Column(precision = 10, scale = 2)
    private BigDecimal fare;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RideStatus status = RideStatus.REQUESTED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
