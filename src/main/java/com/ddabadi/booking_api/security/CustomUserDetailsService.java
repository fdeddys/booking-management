package com.ddabadi.booking_api.security;

import com.ddabadi.booking_api.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username){

        Optional optionalUser = null;
        log.info("username : " + username);
        try {
            Optional optUser = repo.findByUsername(username);
            if (!optUser.isPresent()) {
                log.info("Not found");
            }
            optionalUser = repo.findByUsername(username)
                    .map(user -> com.ddabadi.booking_api.entity.User.builder()
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .id(user.getId())
                            .role(user.getRole())
                            .build());

            if (optionalUser.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        com.ddabadi.booking_api.entity.User currUser = (com.ddabadi.booking_api.entity.User) optionalUser.get();
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                System.out.println("USER ROLE ==> " + currUser.getRole()); // harusnya ADMIN atau USER, bukan null
                if (currUser.getRole().name() == null) {
                    return List.of(); // atau throw new IllegalStateException("User has no role");
                }
                return java.util.List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + currUser.getRole().name()
                        )
                );
            }

            @Override
            public String getPassword() {
                return currUser.getPassword();
            }

            @Override
            public String getUsername() {
                return currUser.getUsername();
            }

        };
    }
}
