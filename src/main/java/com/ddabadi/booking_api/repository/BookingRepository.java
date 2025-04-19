package com.ddabadi.booking_api.repository;

import com.ddabadi.booking_api.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    List<Booking> findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(LocalDateTime end, LocalDateTime start);
}
