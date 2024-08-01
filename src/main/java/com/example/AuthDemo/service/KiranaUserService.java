package com.example.AuthDemo.service;

import com.example.AuthDemo.config.KiranaAuthenticationProvider;
import com.example.AuthDemo.dto.KiranaUserAuthenticationDto;
import com.example.AuthDemo.dto.KiranaUserResponseDto;
import com.example.AuthDemo.model.KiranaUser;
import com.example.AuthDemo.dto.KiranaUserRegistrationDto;
import com.example.AuthDemo.model.UserDetailsImpl;
import com.example.AuthDemo.repository.KiranaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KiranaUserService {

    // private final KiranaAuthenticationProvider kiranaAuthenticationProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final KiranaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public KiranaUserResponseDto signUp(KiranaUserRegistrationDto userDto) {
        KiranaUser user = KiranaUser.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles(userDto.getRoles())
                .build();

        if (userRepository.save(user) instanceof KiranaUser) {
            var jwtToken = jwtService.generateToken(new UserDetailsImpl(user));
            return KiranaUserResponseDto.builder().token(jwtToken).build();
        } else {
            return null;
        }
    }

    public KiranaUserResponseDto authenticate(KiranaUserAuthenticationDto userDto) {
        KiranaAuthenticationProvider kiranaAuthenticationProvider = new KiranaAuthenticationProvider(userDetailsService, passwordEncoder);
        kiranaAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));
        var user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        var jwtToken = jwtService.generateToken(new UserDetailsImpl(user));
        return KiranaUserResponseDto.builder().token(jwtToken).build();
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
