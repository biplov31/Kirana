package com.example.AuthDemo.controller;

import com.example.AuthDemo.dto.KiranaUserDto;
import com.example.AuthDemo.service.KiranaUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class KiranaUserController {

    private final KiranaUserService userService;

    public KiranaUserController(KiranaUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<KiranaUserDto> signUp(@RequestBody KiranaUserDto userDto) {
        KiranaUserDto createdUser = userService.signUp(userDto);
        if (createdUser != null) {
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
