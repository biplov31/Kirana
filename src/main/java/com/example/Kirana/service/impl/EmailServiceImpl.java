package com.example.Kirana.service.impl;

import com.example.Kirana.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    @Override
    public void sendEmail(String to, String otp) {
        try {
            // SimpleMailMessage mail = new SimpleMailMessage();
            // mail.setTo(to);
            // mail.setSubject(subject);
            // mail.setText(body);
            // javaMailSender.send(mail);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            Context context = new Context();
            context.setVariable("username", to);
            context.setVariable("otp", otp);
            // process the email template with the given context
            String htmlContent = templateEngine.process("Otp_email_template_kirana", context);

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject("Email verification for Kirana");
            mimeMessageHelper.setText(htmlContent, true);
            // mimeMessageHelper.setText("""
            //         </div>
            //             <a href="http://localhost:8080/verify-account?email=%s&otp=%s" target="_blank">Click to verify</a>
            //         </div>
            //         """.formatted(to, otp), true);

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            log.warn("Messaging Exception: {}", e.getMessage());
        }
    }

}
