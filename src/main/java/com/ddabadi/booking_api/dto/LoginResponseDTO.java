package com.ddabadi.booking_api.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String serviceToken;
    private LoginResponseUserDTO user;
}
