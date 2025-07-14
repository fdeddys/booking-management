package com.ddabadi.booking_api.controller;

import com.ddabadi.booking_api.dto.BookingFilterRequest;
import com.ddabadi.booking_api.dto.BookingRequestDTO;
import com.ddabadi.booking_api.dto.BookingResponseDTO;
import com.ddabadi.booking_api.dto.UserResponseDTO;
import com.ddabadi.booking_api.dto.common.ResponseWithContentDTO;
import com.ddabadi.booking_api.entity.Booking;
import com.ddabadi.booking_api.entity.Role;
import com.ddabadi.booking_api.entity.Room;
import com.ddabadi.booking_api.entity.User;
import com.ddabadi.booking_api.repository.UserRepository;
import com.ddabadi.booking_api.service.BookingService;
import com.ddabadi.booking_api.service.ExcelExportService;
import com.ddabadi.booking_api.service.PdfExportService;
import com.ddabadi.booking_api.service.RoomService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.ddabadi.booking_api.constant.ErrorCodeConstant.BOOKING.CODE_BOOKING_ID_NOT_EXIST;
import static com.ddabadi.booking_api.constant.ErrorCodeConstant.BOOKING.CODE_BOOKING_ID_NOT_EXIST_MESSAGE;
import static com.ddabadi.booking_api.constant.ErrorCodeConstant.ROOM.CODE_ROOM_ID_NOT_EXIST;
import static com.ddabadi.booking_api.constant.ErrorCodeConstant.ROOM.CODE_ROOM_ID_NOT_EXIST_MESSAGE;
import static com.ddabadi.booking_api.constant.ErrorCodeConstant.USER.CODE_USER_NOT_FOUND;
import static com.ddabadi.booking_api.constant.ErrorCodeConstant.USER.CODE_USER_NOT_FOUND_MESSAGE;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final PdfExportService pdfExportService;
    private final ExcelExportService excelExportService;
    private final RoomService roomService;


    public BookingController(
            BookingService bookingService,
            UserRepository userRepository,
            PdfExportService pdfExportService,
            ExcelExportService excelExportService,
            RoomService roomService
    ) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.pdfExportService = pdfExportService;
        this.excelExportService = excelExportService;
        this.roomService = roomService;
    }

    @GetMapping
    public List<Booking> getAll() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<BookingResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody BookingRequestDTO bookingRequestDTO) {

        Boolean isNewData;
        ResponseWithContentDTO response = new ResponseWithContentDTO();

        Optional<User> optionalUser = userRepository.findByUsername(bookingRequestDTO.getUserName());
        if (optionalUser.isEmpty()) {
            response.setErrorCode(CODE_USER_NOT_FOUND);
            response.setErrorDescription(CODE_USER_NOT_FOUND_MESSAGE);
            response.setObject(null);
            return ResponseEntity.ok(response);
        }
        Optional<Room> optionalRoom =  roomService.getRoomById(bookingRequestDTO.getRoomId());
        if (optionalRoom.isEmpty()) {
            response.setErrorCode(CODE_ROOM_ID_NOT_EXIST);
            response.setErrorDescription(CODE_ROOM_ID_NOT_EXIST_MESSAGE);
            response.setObject(null);
            return ResponseEntity.ok(response);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime starttime = LocalDateTime.parse(bookingRequestDTO.getStartTimeString(), formatter);
        LocalDateTime endtime = LocalDateTime.parse(bookingRequestDTO.getEndTimeString(), formatter);

        Booking booking = getBookingById(bookingRequestDTO.getId());
        if (booking == null) {
            response.setErrorCode(CODE_BOOKING_ID_NOT_EXIST);
            response.setErrorDescription(CODE_BOOKING_ID_NOT_EXIST_MESSAGE);
            response.setObject(null);
            return ResponseEntity.ok(response);
        }
        booking.setCustomerName(bookingRequestDTO.getCustomerName());
        booking.setEndTime(endtime);
        booking.setStartTime(starttime);
        booking.setNotes(bookingRequestDTO.getNotes());
        booking.setCustomerName(bookingRequestDTO.getCustomerName());
        booking.setNumberOfParticipants(bookingRequestDTO.getNumberOfParticipants());
        booking.setEventName(bookingRequestDTO.getEventName());
        booking.setPhoneNumber(bookingRequestDTO.getPhoneNumber());
        booking.setRoom(optionalRoom.get());
        booking.setUser(optionalUser.get());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(
                bookingService.createBooking(
                        booking,
                        username)
        );
    }

    Booking getBookingById(Long id) {
        Booking booking;
        if (id == 0)  {
            return new Booking();
        } else {
            return bookingService.getById(id);
        }
    }

    @DeleteMapping("/id/{id}")
    public void delete(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElseThrow();

        Booking updated = bookingService.updateBooking(id, request, username);
        if (!updated.getUser().getUsername().equals(username) && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You are not allowed to modify this booking");
        }

        return ResponseEntity.ok(updated);
    }

    private BookingResponseDTO mapToDTO(Booking booking) {
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

    @PostMapping("/filter")
    public ResponseEntity<Object> filterBookings(@RequestBody BookingFilterRequest request) {
        System.out.println("controller filter");
        return ResponseEntity.ok(bookingService.filterBookings(request));
    }

    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=booking-report.pdf");

        List<Booking> bookings = bookingService.getAllBookings();
        pdfExportService.exportBookingsToPdf(bookings, response.getOutputStream());
    }


    @PostMapping("/report/daily")
    public void dailyReport(
            @RequestBody BookingFilterRequest request,
            HttpServletResponse response
    ) throws IOException {


        String filename = "daily-report.xlsx"; // bisa juga hasil dynamic


        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
//        BookingFilterRequest request = BookingFilterRequest
//                .builder()
//                .page(0)
//                .size(100)
//                .build();
        List<Booking> filtered = bookingService.filterBookingsNonPage(request);
        excelExportService.exportBookingsToExcel(filtered, response.getOutputStream());
    }

}
