package com.example.Kirana.service;

import com.example.Kirana.dto.KiranaOtpDto;
import com.example.Kirana.model.KiranaOtp;
import com.example.Kirana.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final Long OTP_VALIDITY = TimeUnit.MINUTES.toNanos(5);

    private final OtpRepository otpRepository;

    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

    public KiranaOtp saveOtp(KiranaOtp kiranaOtp) {
        return otpRepository.save(kiranaOtp);
    }

    public Boolean isOtpValid(Integer userId, String otp) {
        Optional<KiranaOtp> optionalOtp = otpRepository.findOtpByKiranaUserId(userId);

        if (optionalOtp.isEmpty()) {
            throw new SecurityException("OTP not found.");
        }

        KiranaOtp existingOtp = optionalOtp.get();

        if (!isOtpNonExpired(existingOtp)) {
            otpRepository.delete(existingOtp);
            throw new SecurityException("OTP has expired.");
        }

        if (!otp.equals(existingOtp.getOtpCode())) {
            throw new SecurityException("OTP does not match.");
        }

        otpRepository.delete(existingOtp); // remove the OTP after verification
        return true;
    }

    public boolean isOtpNonExpired(KiranaOtp kiranaOtp) {
        return LocalDateTime.now().isBefore(kiranaOtp.getCreatedAt().plusMinutes(OTP_VALIDITY));
    }

}
