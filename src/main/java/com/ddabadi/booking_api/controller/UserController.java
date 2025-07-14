package com.ddabadi.booking_api.controller;

import com.ddabadi.booking_api.constant.BookingConstant;
import com.ddabadi.booking_api.dto.RegisterRequest;
import com.ddabadi.booking_api.dto.RoomRequestDto;
import com.ddabadi.booking_api.entity.AESKeyPair;
import com.ddabadi.booking_api.entity.User;
import com.ddabadi.booking_api.service.AuthService;
import com.ddabadi.booking_api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    private AuthService authService;

    @GetMapping(value = "tes")
    public ResponseEntity<String> getHash(){

        return ResponseEntity.ok(userService.generatePassword("budi"));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        System.out.println("User get by id : " + id);
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User not found");
            error.put("userId", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/filter/page/{page}/count/{count}")
    public ResponseEntity<Object> filterBookings(
            @PathVariable int page,
            @PathVariable int count
    ) {
        log.info("User Filter ");
        return ResponseEntity.ok(userService.findAll(page, count));
    }
}
