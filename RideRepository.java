package com.rideshare.repository;

import com.rideshare.entity.Driver;
import com.rideshare.entity.Ride;
import com.rideshare.entity.RideStatus;
import com.rideshare.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    Page<Ride> findAllByRiderOrderByCreatedAtDesc(User rider, Pageable pageable);

    Page<Ride> findAllByDriverOrderByCreatedAtDesc(Driver driver, Pageable pageable);

    Optional<Ride> findTopByRiderAndStatusNotInOrderByCreatedAtDesc(
            User rider, List<RideStatus> excludedStatuses);

    Page<Ride> findAllByStatus(RideStatus status, Pageable pageable);

    List<Ride> findAllByDriverAndStatus(Driver driver, RideStatus status);

    long countByStatus(RideStatus status);
}
