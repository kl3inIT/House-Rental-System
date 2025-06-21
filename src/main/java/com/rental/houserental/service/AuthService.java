package com.rental.houserental.service;

import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.entity.User;

public interface AuthService {
    User register(RegisterRequestDTO request);
    boolean verifyOtp(String email, String otp);
    void resendOtp(String email);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
}