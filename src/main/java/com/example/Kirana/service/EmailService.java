package com.example.Kirana.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendEmail(String to, String otp);

}
