package com.ddabadi.booking_api.controller;

import com.ddabadi.booking_api.dto.BookingFilterRequest;
import com.ddabadi.booking_api.dto.BookingResponseDTO;
import com.ddabadi.booking_api.dto.UserResponseDTO;
import com.ddabadi.booking_api.entity.Booking;
import com.ddabadi.booking_api.entity.Role;
import com.ddabadi.booking_api.entity.User;
import com.ddabadi.booking_api.repository.UserRepository;
import com.ddabadi.booking_api.service.BookingService;
import com.ddabadi.booking_api.service.ExcelExportService;
import com.ddabadi.booking_api.service.PdfExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {


    private final BookingService bookingService;

    private final UserRepository userRepository;
    private final PdfExportService pdfExportService;
    private final ExcelExportService excelExportService;



    public BookingController(
            BookingService bookingService, UserRepository userRepository, PdfExportService pdfExportService,
            ExcelExportService excelExportService) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.pdfExportService = pdfExportService;
        this.excelExportService = excelExportService;
    }

    @GetMapping
    public List<Booking> getAll() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getById(@PathVariable Long id) {
        Booking booking =  bookingService.getBookingById(id);
        return ResponseEntity.ok(mapToDTO(booking));

    }

    @PostMapping
    public Booking create(@RequestBody Booking booking) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return bookingService.createBooking(booking, username);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }

    @PutMapping("/{id}")
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
                .description(booking.getDescription())
                .customerName(booking.getCustomerName())
                .user(UserResponseDTO.builder()
                        .id(booking.getUser().getId())
                        .username(booking.getUser().getUsername())
                        .role(booking.getUser().getRole().name())
                        .build())
                .build();
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<BookingResponseDTO>> filterBookings(@RequestBody BookingFilterRequest request) {
        return ResponseEntity.ok(bookingService.filterBookings(request));
    }

    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=booking-report.pdf");

        List<Booking> bookings = bookingService.getAllBookings();
        pdfExportService.exportBookingsToPdf(bookings, response.getOutputStream());
    }


    @GetMapping("/export/excel")
    public void exportToExcel(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String name,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=booking-report.xlsx");

        BookingFilterRequest request = BookingFilterRequest
                .builder()
                .page(0)
                .size(100)
                .build();
        List<Booking> filtered = bookingService.filterBookingsNonPage(request);
        excelExportService.exportBookingsToExcel(filtered, response.getOutputStream());
    }

}
