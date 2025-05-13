package com.ddabadi.booking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {
    private Long id;
    private String description;
    private String customerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private UserResponseDTO user;
}
