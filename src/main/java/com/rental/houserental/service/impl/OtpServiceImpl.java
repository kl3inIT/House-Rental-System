package com.rental.houserental.service.impl;

import com.rental.houserental.entity.User;
import com.rental.houserental.enums.UserStatus;
import com.rental.houserental.exceptions.auth.EmailAlreadyVerifiedException;
import com.rental.houserental.repository.UserRepository;
import com.rental.houserental.service.EmailService;
import com.rental.houserental.service.OtpService;
import com.rental.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;
import static com.rental.houserental.constant.Constant.OtpConstants.*;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailService emailService;
    private final UserService userService;
     // OTP hết hạn sau 10 phút

    private static final Random RANDOM = new SecureRandom();

    @Override
    @Transactional
    public void sendOtpForVerification(String email) {
        User user = userService.findByEmail(email);
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new EmailAlreadyVerifiedException("Email already verified");
        }
        String otp = generateOtp();
        String key = OTP_PREFIX + email;
        redisTemplate.opsForValue().set(key, otp, OTP_EXPIRY);
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
