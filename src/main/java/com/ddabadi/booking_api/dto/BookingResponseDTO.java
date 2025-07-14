package com.ddabadi.booking_api.dto;

import com.ddabadi.booking_api.entity.Room;
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
    private String customerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private UserResponseDTO user;
    private String phoneNumber;
    private Integer numberOfParticipants;
    private String eventName;
    private String notes;
    private Room room;
}
