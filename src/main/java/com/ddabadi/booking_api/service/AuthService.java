package com.ddabadi.booking_api.service;

import com.ddabadi.booking_api.dto.AuthRequest;
import com.ddabadi.booking_api.dto.LoginResponseDTO;
import com.ddabadi.booking_api.dto.LoginResponseUserDTO;
import com.ddabadi.booking_api.dto.RegisterRequest;
import com.ddabadi.booking_api.entity.Role;
import com.ddabadi.booking_api.entity.User;
import com.ddabadi.booking_api.repository.UserRepository;
import com.ddabadi.booking_api.security.CustomUserDetailsService;
import com.ddabadi.booking_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService  {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return jwtService.generateToken(user);
    }

    public LoginResponseDTO authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails user = customUserDetailsService.loadUserByUsername(request.getUsername());

//                userRepository.findByUsername(request.getUsername())
//                .orElseThrow();

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setServiceToken(jwtService.generateToken(user));
        LoginResponseUserDTO userDto = new LoginResponseUserDTO();
        userDto.setId(request.getUsername());
        userDto.setName(request.getUsername());
        userDto.setRole("Admin");
        userDto.setLastName("");
        userDto.setFirstName("");
        userDto.setEmail("");
        userDto.setPassword("");

        loginResponseDTO.setUser(userDto);
        return loginResponseDTO;
    }
}
