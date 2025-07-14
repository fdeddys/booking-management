package com.ddabadi.booking_api.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class BookingRequestDTO implements Serializable {

    private Long id;

    private String customerName;
    private Integer numberOfParticipants;
    private String eventName;
    private String notes;
    private String phoneNumber;

    private String startTimeString;
    private String endTimeString;

    private String userName;
    private Integer roomId;

}
