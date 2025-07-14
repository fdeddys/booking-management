package com.ddabadi.booking_api.service;


import com.ddabadi.booking_api.dto.BookingFilterRequest;
import com.ddabadi.booking_api.dto.BookingResponseDTO;
import com.ddabadi.booking_api.dto.UserResponseDTO;
import com.ddabadi.booking_api.dto.common.PageResponse;
import com.ddabadi.booking_api.dto.common.ResponseWithContentDTO;
import com.ddabadi.booking_api.entity.Booking;
import com.ddabadi.booking_api.entity.User;
import com.ddabadi.booking_api.repository.BookingRepository;
import com.ddabadi.booking_api.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.ddabadi.booking_api.constant.ErrorCodeConstant.BOOKING.CODE_FAILED_VALIDATION;
import static com.ddabadi.booking_api.constant.ErrorCodeConstant.CODE_SUCCESS;
import static com.ddabadi.booking_api.constant.ErrorCodeConstant.CODE_SUCCESS_MESSAGE;
import static com.ddabadi.booking_api.constant.ErrorCodeConstant.USER.CODE_USER_NOT_FOUND;

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

    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElse(new Booking());
        return mapToDTO(booking);
    }

    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElse(null);
    }

    public ResponseWithContentDTO createBooking(Booking booking, String username) {
        // Validasi bentrok
        ResponseWithContentDTO response = new ResponseWithContentDTO();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return errorValidation(CODE_USER_NOT_FOUND, "User not found f");
        }
        User user = optionalUser.get();
        List<Booking> conflicts = bookingRepository.findByStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndRoom(
                booking.getEndTime(), booking.getStartTime(), booking.getRoom()
        );
//        if (!conflicts.isEmpty() && user.getUsername().equals(conflicts.get(0).getUser().getUsername())) {
        if (!conflicts.isEmpty()) {
            // new record
            if (booking.getId()==0) {
                return errorValidation(CODE_FAILED_VALIDATION, "Booking conflict detected");
            }
            // update record => jika record sendiri, boleh conflict dg jadwal sendiri
            boolean recordDiriSendiri = true;
            for (Booking book: conflicts) {
                if (!book.getId().equals(booking.getId())){
                    recordDiriSendiri = false;
                }
            }
            if (!recordDiriSendiri) {
                return errorValidation(CODE_FAILED_VALIDATION, "Booking conflict detected");
            }
        }
        booking.setUser(user);
        response.setErrorCode(CODE_SUCCESS);
        response.setErrorDescription(CODE_SUCCESS_MESSAGE);
        response.setObject(bookingRepository.save(booking));
        return response ;
    }

    ResponseWithContentDTO errorValidation(String errorCode, String errorMessage) {
        ResponseWithContentDTO response = new ResponseWithContentDTO();
        response.setErrorCode(errorCode);
        response.setErrorDescription(errorMessage);
        return response;
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
        existing.setNotes(request.getNotes());

        return bookingRepository.save(existing);
    }

    public PageResponse<BookingResponseDTO> filterBookings(BookingFilterRequest request) {
        System.out.println("filter bookings");
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by("room.name").ascending().and(Sort.by("startTime"))
        );

        // Build dynamic spec with optional filters
        Specification<Booking> spec = Specification.where(null);

        System.out.println("getStartDate");
        if (request.getStartDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("startTime"), request.getStartDate().atStartOfDay()));
        }
        System.out.println("getEndDate");
        if (request.getEndDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("endTime"), request.getEndDate().atTime(23, 59)));
        }

        System.out.println("bookingRepository");
        Page<BookingResponseDTO> result = bookingRepository.findAll(spec, pageable)
                .map(this::mapToDTO);
        System.out.println("Iterate");
        result.forEach(data -> {
            System.out.println("data " + data.getCustomerName());
        });

//        return new PageResponse<BookingResponseDTO>(
//                result.getContent(),
//                result.getPageable().getPageNumber(),
//                result.getSize(),
//                result.getTotalElements(),
//                result.getSize(),
//                result.getPageable().hasPrevious()
//        );

        return new PageResponse<BookingResponseDTO>(result);
    }


    public List<Booking> filterBookingsNonPage(BookingFilterRequest request) {
//        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        // Build dynamic spec with optional filters
        Specification<Booking> spec = Specification.where(null);

//        if (request.getUserId() != null) {
//            spec = spec.and((root, query, cb) ->
//                    cb.equal(root.get("user").get("id"), request.getUserId()));
//        }

        if (request.getStartDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("startTime"), request.getStartDate().atStartOfDay()));
        }

        if (request.getEndDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("endTime"), request.getEndDate().atTime(23, 59)));
        }

        if (request.getRoomId() != 0 ){
            spec = spec.and((root, query, cb)->
                    cb.equal(root.get("room").get("id"), request.getRoomId())
            );
        }

        return bookingRepository.findAll(spec);
    }

    private BookingResponseDTO mapToDTO(Booking booking) {
//        return BookingResponseDTO.builder()
//                .id(booking.getId())
//                .startTime(booking.getStartTime())
//                .endTime(booking.getEndTime())
//                .description(booking.getNotes())
//                .customerName(booking.getCustomerName())
//                .user(UserResponseDTO.builder()
//                        .id(booking.getUser().getId())
//                        .username(booking.getUser().getUsername())
//                        .role(booking.getUser().getRole().name())
//                        .build())
//                .build();

        return BookingResponseDTO.builder()
                .id(booking.getId())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .customerName(booking.getCustomerName())
                .phoneNumber(booking.getPhoneNumber())
                .numberOfParticipants(booking.getNumberOfParticipants())
                .eventName(booking.getEventName())
                .notes(booking.getNotes())
                .room(booking.getRoom())
                .user(UserResponseDTO
                        .builder()
                        .id(booking.getUser().getId())
                        .username(booking.getUser().getUsername())
                        .role(booking.getUser().getRole().name())
                        .build())
                .build();
    }

}
