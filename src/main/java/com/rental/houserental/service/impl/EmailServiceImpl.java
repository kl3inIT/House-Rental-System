package com.rental.houserental.service.impl;

import com.rental.houserental.entity.User;
import com.rental.houserental.exceptions.auth.FaildToSendEmailException;
import com.rental.houserental.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendVerificationEmail(User user, String otp) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("otpMessage", otp);
        String emailContent = templateEngine.process("email/verify-email", context);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("Verify your RentEase account");
            helper.setText(emailContent, true);
            mailSender.send(message);
            log.info("Verification email sent to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", user.getEmail(), e);
            throw new FaildToSendEmailException("Failed to send verification email to: " + user.getEmail());
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        Context context = new Context();
        context.setVariable("resetLink", resetLink);

        String emailContent = templateEngine.process("email/reset-password", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Reset your RentEase password");
            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", to, e);
            throw new FaildToSendEmailException("Failed to send password reset email");
        }
    }

    @Override
    public void sendLandlordRequestRejectionEmail(User user, String reason) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("reason", reason);
        String emailContent = templateEngine.process("email/landlord-request-rejected", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("Your Landlord Upgrade Request - Rejected");
            helper.setText(emailContent, true);
            mailSender.send(message);
            log.info("Landlord request rejection email sent to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send landlord request rejection email to: {}", user.getEmail(), e);
            throw new FaildToSendEmailException("Failed to send landlord request rejection email");
        }
    }

    @Override
    public void sendLandlordRequestApprovalEmail(User user) {
        Context context = new Context();
        context.setVariable("user", user);
        String emailContent = templateEngine.process("email/landlord-request-approved", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("Your Landlord Upgrade Request - Approved!");
            helper.setText(emailContent, true);
            mailSender.send(message);
            log.info("Landlord request approval email sent to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send landlord request approval email to: {}", user.getEmail(), e);
            throw new FaildToSendEmailException("Failed to send landlord request approval email");
        }
    }
}