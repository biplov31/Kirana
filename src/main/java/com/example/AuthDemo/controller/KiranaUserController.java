package com.example.AuthDemo.controller;

import com.example.AuthDemo.dto.KiranaUserAuthenticationDto;
import com.example.AuthDemo.dto.KiranaUserRegistrationDto;
import com.example.AuthDemo.dto.KiranaUserResponseDto;
import com.example.AuthDemo.service.KiranaUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class KiranaUserController {

    private final KiranaUserService userService;

    @PostMapping("/signup")
    public ResponseEntity<KiranaUserResponseDto> register(@RequestBody KiranaUserRegistrationDto userDto) {
        KiranaUserResponseDto responseDto = userService.signUp(userDto);
        if (responseDto != null) {
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<KiranaUserResponseDto> authenticate(@RequestBody KiranaUserAuthenticationDto userDto) {
        KiranaUserResponseDto responseDto = userService.authenticate(userDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public String sendPublicResponse() {
        return "Hello from Kirana!";
    }

    @GetMapping("/user")
    public String sendUserResponse() {
        return userService.greetUser();
    }

    @GetMapping("/ca")
    public String sendCAResponse() {
        return userService.greetCA();
    }

    @GetMapping("/super-admin")
    public String sendSuperAdminResponse() {
        return userService.greetSuperAdmin();
    }

}
