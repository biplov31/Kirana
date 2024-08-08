package com.example.Kirana.service.impl;

import com.example.Kirana.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String to, String otp) {
        try {
            // SimpleMailMessage mail = new SimpleMailMessage();
            // mail.setTo(to);
            // mail.setSubject(subject);
            // mail.setText(body);
            // javaMailSender.send(mail);
            System.out.println("Working on email!");
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject("Email verification");
            mimeMessageHelper.setText("""
                    </div>
                        <a href="http://localhost:8080/verify-account?email=%s&otp=%s" target="_blank">Click to verify</a>
                    </div>
                    """.formatted(to, otp), true);

            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
