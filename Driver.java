package com.rideshare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false, unique = true)
    private String vehiclePlate;

    @Column
    private Double currentLatitude;

    @Column
    private Double currentLongitude;

    @Column(nullable = false)
    @Builder.Default
    private boolean available = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean suspended = false;
}
