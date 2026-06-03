package com.rideshare.repository;

import com.rideshare.entity.Driver;
import com.rideshare.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByUser(User user);

    Optional<Driver> findByUserId(Long userId);

    Page<Driver> findAll(Pageable pageable);

    Page<Driver> findAllBySuspended(boolean suspended, Pageable pageable);

    /**
     * Find all available, non-suspended drivers who have a known location.
     * Haversine-based sorting is done in the service layer to remain DB-agnostic.
     */
    @Query("SELECT d FROM Driver d WHERE d.available = true AND d.suspended = false " +
           "AND d.currentLatitude IS NOT NULL AND d.currentLongitude IS NOT NULL")
    List<Driver> findAllAvailableDrivers();

    /**
     * Haversine query – use only when the DB is PostgreSQL (earthdistance / cube extension).
     * Left here as an optional optimisation; the service layer falls back to the Java
     * Haversine implementation which works with any data source (including H2 in tests).
     */
    @Query(value = """
            SELECT * FROM drivers d
            WHERE d.available = TRUE AND d.suspended = FALSE
              AND d.current_latitude  IS NOT NULL
              AND d.current_longitude IS NOT NULL
            ORDER BY (
                2 * 6371 * ASIN(SQRT(
                    POWER(SIN(RADIANS(d.current_latitude  - :lat) / 2), 2) +
                    COS(RADIANS(:lat)) * COS(RADIANS(d.current_latitude)) *
                    POWER(SIN(RADIANS(d.current_longitude - :lng) / 2), 2)
                ))
            )
            LIMIT :limit
            """, nativeQuery = true)
    List<Driver> findNearestAvailableDriversNative(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("limit") int limit
    );

    boolean existsByVehiclePlate(String vehiclePlate);

    boolean existsByUser(User user);
}
