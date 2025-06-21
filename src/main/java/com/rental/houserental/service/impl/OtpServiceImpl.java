package com.rental.houserental.service.impl;

import com.rental.houserental.entity.User;
import com.rental.houserental.enums.UserStatus;
import com.rental.houserental.exceptions.auth.EmailAlreadyVerifiedException;
import com.rental.houserental.exceptions.user.UserNotFoundException;
import com.rental.houserental.service.EmailService;
import com.rental.houserental.service.OtpService;
import com.rental.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Random;
import static com.rental.houserental.constant.OtpConstants.*;
import static com.rental.houserental.constant.ViewNamesConstant.*;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;
    private final UserService userService;
    private static final Random RANDOM = new SecureRandom();

    @Override
    @Transactional
    public void sendOtpForVerification(String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email, REDIRECT_VERIFY_OTP);
        }
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new EmailAlreadyVerifiedException("Email already verified");
        }
        String otp = generateOtp();
        redisTemplate.opsForValue().set(OTP_PREFIX + email, otp, OTP_EXPIRY);
        redisTemplate.opsForValue().set(OTP_FAIL_PREFIX + email, "0", OTP_EXPIRY);

        long expiryMillis = System.currentTimeMillis() + OTP_EXPIRY.toMillis();
        redisTemplate.opsForValue().set(OTP_EXP_PREFIX + email, String.valueOf(expiryMillis), OTP_EXPIRY);

        emailService.sendVerificationEmail(user, otp);
    }

    private String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(RANDOM.nextInt(10));
        }
        return otp.toString();
    }
}
