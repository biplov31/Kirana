package com.example.Kirana.service;

import com.example.Kirana.config.KiranaAuthenticationProvider;
import com.example.Kirana.dto.*;
import com.example.Kirana.model.KiranaOtp;
import com.example.Kirana.model.KiranaUser;
import com.example.Kirana.model.UserDetailsImpl;
import com.example.Kirana.repository.KiranaUserRepository;
import com.example.Kirana.utils.LoggedInUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KiranaUserService {

    // private final KiranaAuthenticationProvider kiranaAuthenticationProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final KiranaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final OtpService otpService;
    private final LoggedInUser loggedInUser;

    public KiranaUserResponseDto signUp(KiranaUserRegistrationDto userDto) {
        KiranaUser user = KiranaUser.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles(userDto.getRoles())
                .build();

        KiranaUser savedUser = userRepository.save(user);
        // savedUser != null is pointless because JPA returns a non-null entity even if the operation fails
        if (savedUser.getId() != null) {
            String otp = otpService.generateOtp();

            KiranaOtp kiranaOtp = KiranaOtp.builder()
                    .otpCode(otp)
                    .kiranaUser(savedUser)
                    .build();

            KiranaOtp savedOtp = otpService.saveOtp(kiranaOtp);
            if (savedOtp.getId() != null) emailService.sendEmail(savedUser.getEmail(), otp);

            // store the authenticated user in Security Context, so it can be used for verification
            Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), userDto.getPassword());
            KiranaAuthenticationProvider kiranaAuthenticationProvider = new KiranaAuthenticationProvider(userDetailsService, passwordEncoder);
            Authentication authenticated = kiranaAuthenticationProvider.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authenticated);

            var jwtToken = jwtService.generateToken(new UserDetailsImpl(user));
            return KiranaUserResponseDto.builder().accessToken(jwtToken).build();
        } else {
            throw new RuntimeException("Failed to save the user.");
        }
    }

    public boolean verifyEmail(KiranaOtpVerificationDto kiranaOtpDto) {
        KiranaUser currentUser = loggedInUser.getLoggedInUserEntity();
        System.out.println("Verifying email for: " + currentUser.getEmail());
        // KiranaOtpDto kiranaOtp = KiranaOtpDto.builder()
        //         .kiranaUser(currentUser)
        //         .otpCode(kiranaOtpDto.getOtp())
        //         .build();

        boolean isVerified = otpService.isOtpValid(currentUser.getId(), kiranaOtpDto.getOtp());

        if (isVerified) {
            currentUser.setVerified(true);
            currentUser.setRoles(currentUser.getRoles() + ",VERIFIED");
            userRepository.save(currentUser);

            return true;
        } else {
            return false;
        }
    }

    public KiranaUserResponseDto authenticate(KiranaUserAuthenticationDto userDto) {
        KiranaAuthenticationProvider kiranaAuthenticationProvider = new KiranaAuthenticationProvider(userDetailsService, passwordEncoder);
        kiranaAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));
        var user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        var jwtToken = jwtService.generateToken(new UserDetailsImpl(user));
        return KiranaUserResponseDto.builder().accessToken(jwtToken).build();
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
