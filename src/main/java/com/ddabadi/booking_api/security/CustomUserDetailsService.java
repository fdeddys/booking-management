package com.ddabadi.booking_api.security;

import com.ddabadi.booking_api.repository.UserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional optionalUser = repo.findByUsername(username)
                .map(user -> com.ddabadi.booking_api.entity.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .role(user.getRole())
                        .build());

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        com.ddabadi.booking_api.entity.User currUser = (com.ddabadi.booking_api.entity.User) optionalUser.get();
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
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
