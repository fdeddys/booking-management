package com.ddabadi.booking_api.dto;


import lombok.Data;

@Data
public class RegisterRequest {
    private Integer id;
    private String username;
    private String password;
}