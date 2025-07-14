package com.ddabadi.booking_api.controller;


import com.ddabadi.booking_api.dto.AuthRequest;
import com.ddabadi.booking_api.dto.RegisterRequest;
import com.ddabadi.booking_api.service.AuthService;
import com.ddabadi.booking_api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
//        return ResponseEntity.ok(authService.register(request));
//    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AuthRequest request) {
        log.info("Loginn : " + request.toString());
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping(value = "challange")
    public Object getChallange(
            @RequestParam("username") String username
    ) {
        log.info("challange : ");
        return authService.challangeProses(username);
    }

    @PostMapping(value = "register")
    public Object register(
            @RequestBody RegisterRequest registerRequest
    ) {
        return userService.createUser(registerRequest);
    }

}


