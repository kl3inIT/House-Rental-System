package com.rental.houserental.service.impl;

import com.rental.houserental.dto.request.auth.LoginRequestDTO;
import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.UserStatus;
import com.rental.houserental.exceptions.auth.EmailAlreadyExistsException;
import com.rental.houserental.exceptions.auth.EmailAlreadyVerifiedException;
import com.rental.houserental.exceptions.auth.PasswordNotMatchException;
import com.rental.houserental.exceptions.auth.UserNotVerifiedException;
import com.rental.houserental.exceptions.user.UserNotFoundException;
import com.rental.houserental.exceptions.user.UserSuspendedException;
import com.rental.houserental.exceptions.user.UserBannedException;
import com.rental.houserental.repository.UserRepository;
import com.rental.houserental.service.AuthService;
import com.rental.houserental.service.EmailService;
import com.rental.houserental.service.OtpService;
import com.rental.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import static com.rental.houserental.constant.OtpConstants.*;
import static com.rental.houserental.constant.ViewNamesConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
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
    @Transactional
    public boolean login(LoginRequestDTO request) {
        try {
            User user = userService.findByEmail(request.getEmail());

            if (user == null) {
                throw new UserNotFoundException("User not found with email: " + request.getEmail(), REDIRECT_LOGIN);
            }

            switch (user.getStatus()) {
                case PENDING:
                    throw new UserNotVerifiedException("Please verify your email before logging in.");
                case SUSPENDED:
                    throw new UserSuspendedException("Your account has been temporarily suspended. Please contact support.");
                case BANNED:
                    throw new UserBannedException("Your account has been permanently banned. Please contact support.");
                default:
                    break;
            }

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            // Set authentication vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("User logged in successfully: {}", request.getEmail());
            return true;
            
        } catch (BadCredentialsException e) {
            log.warn("Invalid login attempt for email: {}", request.getEmail());
            throw new RuntimeException("Invalid email or password.");
        } catch (Exception e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean verifyOtp(String email, String otp) {
        String key = OTP_PREFIX + email;
        String failKey = OTP_FAIL_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(key);
        if (storedOtp == null || !storedOtp.equals(otp)) {
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
            }
            return false;
        }
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email, REDIRECT_VERIFY_OTP);
        }
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        redisTemplate.delete(key);
        redisTemplate.delete(failKey);
        redisTemplate.delete(OTP_EXP_PREFIX + email);
        return true;
    }

    @Override
    @Transactional
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


}