package com.ddabadi.booking_api.service;


import com.ddabadi.booking_api.dto.BookingFilterRequest;
import com.ddabadi.booking_api.dto.BookingResponseDTO;
import com.ddabadi.booking_api.dto.UserResponseDTO;
import com.ddabadi.booking_api.entity.Booking;
import com.ddabadi.booking_api.entity.User;
import com.ddabadi.booking_api.repository.BookingRepository;
import com.ddabadi.booking_api.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElse(new Booking());
    }

    public Booking createBooking(Booking booking, String username) {
        // Validasi bentrok

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Booking> conflicts = bookingRepository.findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                booking.getEndTime(), booking.getStartTime()
        );
        if (!conflicts.isEmpty() && user.getUsername().equals(conflicts.get(0).getUser().getUsername())) {
            throw new RuntimeException("Booking conflict detected");
        }
        booking.setUser(user);
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public Booking updateBooking(Long id, Booking request, String username) {
        Booking existing = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!existing.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only update your own bookings");
        }

        existing.setCustomerName(request.getCustomerName());
        existing.setStartTime(request.getStartTime());
        existing.setEndTime(request.getEndTime());
        existing.setCustomerName(request.getCustomerName());
        existing.setDescription(request.getDescription());

        return bookingRepository.save(existing);
    }

    public Page<BookingResponseDTO> filterBookings(BookingFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        // Build dynamic spec with optional filters
        Specification<Booking> spec = Specification.where(null);

        if (request.getUserId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("user").get("id"), request.getUserId()));
        }

        if (request.getStartDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("startTime"), request.getStartDate().atStartOfDay()));
        }

        if (request.getEndDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("endTime"), request.getEndDate().atTime(23, 59)));
        }

        return bookingRepository.findAll(spec, pageable)
                .map(this::mapToDTO);
    }


    public List<Booking> filterBookingsNonPage(BookingFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        // Build dynamic spec with optional filters
        Specification<Booking> spec = Specification.where(null);

        if (request.getUserId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("user").get("id"), request.getUserId()));
        }

        if (request.getStartDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("startTime"), request.getStartDate().atStartOfDay()));
        }

        if (request.getEndDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("endTime"), request.getEndDate().atTime(23, 59)));
        }

        return bookingRepository.findAll(spec);
    }

    private BookingResponseDTO mapToDTO(Booking booking) {
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .description(booking.getDescription())
                .customerName(booking.getCustomerName())
                .user(UserResponseDTO.builder()
                        .id(booking.getUser().getId())
                        .username(booking.getUser().getUsername())
                        .role(booking.getUser().getRole().name())
                        .build())
                .build();
    }

}
