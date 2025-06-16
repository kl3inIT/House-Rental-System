package com.rental.houserental.service;

import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.entity.User;

public interface AuthService {
    User register(RegisterRequestDTO request);
//    void sendOtpForVerification(String email);
    boolean verifyOtp(String email, String otp);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
}