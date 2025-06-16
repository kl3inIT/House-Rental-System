package com.rental.houserental.service.impl;

import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.UserStatus;
import com.rental.houserental.repository.UserRepository;
import com.rental.houserental.service.AuthService;
import com.rental.houserental.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String OTP_PREFIX = "otp:";
    private static final Duration OTP_EXPIRY = Duration.ofMinutes(10); // OTP hết hạn sau 10 phút
    private static final int OTP_LENGTH = 6;

    @Override
    @Transactional
    public User register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.PENDING);

        user = userRepository.save(user);

        // Gửi OTP để xác minh
        sendOtpForVerification(user.getEmail());

        return user;
    }

    @Override
    @Transactional
    public void sendOtpForVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new RuntimeException("Email already verified");
        }

        // Tạo OTP ngẫu nhiên
        String otp = generateOtp();
        String key = OTP_PREFIX + email;

        // Lưu OTP vào Redis
        redisTemplate.opsForValue().set(key, otp, OTP_EXPIRY);

        // Gửi OTP qua email
        String otpMessage = "Your verification code is: " + otp + ". It will expire in 10 minutes.";
        emailService.sendVerificationEmail(user, otpMessage); // Thay link bằng nội dung OTP
    }

    @Override
    @Transactional
    public boolean verifyOtp(String email, String otp) {
        String key = OTP_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null || !storedOtp.equals(otp)) {
            return false;
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        redisTemplate.delete(key);
        return true;
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {
        sendOtpForVerification(email);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = java.util.UUID.randomUUID().toString();
        String key = "reset:" + token;
        redisTemplate.opsForValue().set(key, email, Duration.ofHours(1));

        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(email, resetLink);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        String key = "reset:" + token;
        String email = redisTemplate.opsForValue().get(key);

        if (email == null) {
            throw new RuntimeException("Invalid or expired reset token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redisTemplate.delete(key);
    }

    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}