package com.ddabadi.booking_api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookingFilterRequest {
//    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int page = 0;
    private int size = 10;
    private Integer roomId;
}
