package com.example.AuthDemo.service;

import com.example.AuthDemo.model.KiranaUser;
import com.example.AuthDemo.dto.KiranaUserDto;
import com.example.AuthDemo.repository.KiranaUserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class KiranaUserService {

    private final UserDetailsService userDetailsService;
    private final KiranaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public KiranaUserService(KiranaUserRepository userRepository, UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public KiranaUserDto signUp(KiranaUserDto userDto) {
        KiranaUser user = KiranaUser.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles(userDto.getRoles())
                .build();

        if (userRepository.save(user) instanceof KiranaUser) {
            return userDto;
        } else {
            return null;
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public String greetUser() {
        return "Hello, User!";
    }

    // @PreAuthorize("hasAnyRole('CA', 'SUPER_ADMIN')")
    public String greetCA() {
        return "Hello, CA!";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String greetSuperAdmin() {
        return "Hello, Super Admin!";
    }

}
