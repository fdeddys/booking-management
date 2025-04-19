package com.ddabadi.booking_api.dto;


import lombok.Data;

@Data
public class LoginResponseUserDTO {
    String firstName;
    String lastName;
    String id;
    String email;
    String password;
    String name;
    String role;

}
