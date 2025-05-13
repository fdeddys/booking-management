package com.ddabadi.booking_api.dto;

import com.ddabadi.booking_api.enums.Status;
import lombok.Data;

@Data
public class RoomRequestDto {
    private String desc;
    private String name;
    private Status status;
    private int page;
    private int size;
    private Boolean isActive;

    public RoomRequestDto() {
        this.name="";
        this.desc="";
    }
}
