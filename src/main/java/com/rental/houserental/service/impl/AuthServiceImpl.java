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
import java.util.UUID;
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
                redisTemplate.delete(key);
                redisTemplate.delete(failKey);
                redisTemplate.delete(OTP_EXP_PREFIX + email);
                throw new MaxAttemptsReachedException("Maximum OTP attempts reached for email: " + email, email);
            }

            throw new InvalidOtpException("Invalid OTP for email: " + email, email);
        }

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
    public void forgotPassword(String email) {
        log.info("Password reset requested for email: {}", email);
        
        // Rate limiting check - allow max 3 requests per 15 minutes
        String rateLimitKey = "reset_rate_limit:" + email;
        String attempts = redisTemplate.opsForValue().get(rateLimitKey);
        
        if (attempts != null && Integer.parseInt(attempts) >= 3) {
            log.warn("Rate limit exceeded for password reset: {}", email);
            throw new TooManyPasswordResetAttemptsException("Too many password reset requests. Please try again later.");
        }
        
        // Increment rate limit counter
        redisTemplate.opsForValue().increment(rateLimitKey);
        redisTemplate.expire(rateLimitKey, Duration.ofMinutes(15));
        
        // Check if user exists and validate business rules
        User user = userService.findByEmail(email);
        
        if (user != null) {
            // Business logic validation similar to register flow
            if (user.getStatus() == UserStatus.SUSPENDED) {
                throw new UserSuspendedException("Your account has been temporarily suspended. Please contact support to restore access.");
            }
            
            if (user.getStatus() == UserStatus.BANNED) {
                throw new UserBannedException("Your account has been permanently banned. Please contact support.");
            }
            
            // Generate secure token
            String token = UUID.randomUUID().toString().replace("-", "") +
                          System.currentTimeMillis();
            String key = "reset:" + token;
            
            // Store token with 30 minutes expiration
            redisTemplate.opsForValue().set(key, email, Duration.ofMinutes(30));
            
            // Store token usage flag (one-time use)
            redisTemplate.opsForValue().set("reset_used:" + token, "false", Duration.ofMinutes(30));
            
            String resetLink = "http://localhost:8080/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(email, resetLink);
            
            log.info("Password reset email sent to: {}", email);
        } else {
            log.warn("Password reset requested for non-existent email: {}", email);
            // Still simulate sending email to avoid timing attacks
            try {
                Thread.sleep(100); // Random delay 100-300ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    @Override
    public void resetPassword(String token, String newPassword) {
        log.info("Password reset attempted with token: {}", token.substring(0, 8) + "...");
        
        // Validate input
        if (token.trim().isEmpty()) {
            throw new InvalidResetTokenException("Invalid reset token");
        }
        
        String key = "reset:" + token;
        String usedKey = "reset_used:" + token;
        
        // Check if token exists
        String email = redisTemplate.opsForValue().get(key);
        if (email == null) {
            log.warn("Invalid or expired reset token used: {}", token.substring(0, 8) + "...");
            throw new InvalidResetTokenException("Invalid or expired reset token");
        }
        
        // Check if token has been used (one-time use)
        String tokenUsed = redisTemplate.opsForValue().get(usedKey);
        if ("true".equals(tokenUsed)) {
            log.warn("Reset token already used: {}", token.substring(0, 8) + "...");
            throw new InvalidResetTokenException("Reset token has already been used");
        }
        
        // Mark token as used immediately to prevent race conditions
        redisTemplate.opsForValue().set(usedKey, "true", Duration.ofMinutes(30));
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found", REDIRECT_FORGOT_PASSWORD));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Clean up tokens
        redisTemplate.delete(key);
        redisTemplate.delete(usedKey);
        
        // Clear rate limiting after successful reset
        String rateLimitKey = "reset_rate_limit:" + email;
        redisTemplate.delete(rateLimitKey);
        
        log.info("Password successfully reset for user: {}", email);
    }


}