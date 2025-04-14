package com.ddabadi.booking_api.service;


import com.ddabadi.booking_api.entity.Booking;
import com.ddabadi.booking_api.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public Booking createBooking(Booking booking) {
        // Validasi bentrok
        List<Booking> conflicts = bookingRepository.findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                booking.getEndTime(), booking.getStartTime());
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Booking conflict detected");
        }
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}
