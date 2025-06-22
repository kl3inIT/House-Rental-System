package com.rental.houserental.service.impl;


import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.UserStatus;
import com.rental.houserental.exceptions.auth.*;
import com.rental.houserental.exceptions.user.*;
import com.rental.houserental.repository.UserRepository;
import com.rental.houserental.service.AuthService;
import com.rental.houserental.service.EmailService;
import com.rental.houserental.service.OtpService;
import com.rental.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.rental.houserental.constant.OtpConstants.*;
import static com.rental.houserental.constant.ViewNamesConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;
    private final OtpService otpService;


    @Override
    public User register(RegisterRequestDTO request) {
        User user = userService.findByEmail(request.getEmail());
        if (user != null) {
            switch (user.getStatus()) {
                case ACTIVE -> throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
                case SUSPENDED ->
                        throw new UserSuspendedException("This account has been temporarily suspended. Please contact support.");
                case BANNED ->
                        throw new UserBannedException("This account has been permanently banned. Please contact support.");
                case PENDING -> {
                    otpService.sendOtpForVerification(user.getEmail());
                    return user;
                }
            }
        }
        user = userService.createUser(request);
        otpService.sendOtpForVerification(user.getEmail());
        return user;
    }


    @Override
    public boolean verifyOtp(String email, String otp) {
        String key = OTP_PREFIX + email;
        String failKey = OTP_FAIL_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null) {
            throw new OtpNotFoundException("OTP not found for email: " + email, email);
        }

        if (!storedOtp.equals(otp)) {
            String failCountStr = redisTemplate.opsForValue().get(failKey);
            int failCount = 0;
            if (failCountStr != null) {
                try {
                    failCount = Integer.parseInt(failCountStr);
                } catch (NumberFormatException ignored) {
                    log.warn("Failed to parse failCountStr: {}", failCountStr);
                }
            }
            failCount++;
            long expire = redisTemplate.getExpire(key);
            if (expire <= 0) expire = 600L;
            redisTemplate.opsForValue().set(failKey, String.valueOf(failCount), Duration.ofSeconds(expire));

            if (failCount >= 3) {
                // Delete all OTP related keys when max attempts reached
                redisTemplate.delete(key);
                redisTemplate.delete(failKey);
                redisTemplate.delete(OTP_EXP_PREFIX + email);
                throw new MaxAttemptsReachedException("Maximum OTP attempts reached for email: " + email, email);
            }

            throw new InvalidOtpException("Invalid OTP for email: " + email, email);
        }

        // Valid OTP - verify user
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email, REDIRECT_VERIFY_OTP);
        }
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // Clean up all OTP related keys
        redisTemplate.delete(key);
        redisTemplate.delete(failKey);
        redisTemplate.delete(OTP_EXP_PREFIX + email);
        return true;
    }

    @Override
    public void resendOtp(String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email, REDIRECT_VERIFY_OTP);
        }
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new EmailAlreadyVerifiedException("Email already verified");
        }
        // Gửi lại OTP (sẽ reset fail count về 0)
        otpService.sendOtpForVerification(email);
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


}