package com.rental.houserental.service;

import com.rental.houserental.entity.User;

public interface EmailService {
    void sendVerificationEmail(User user, String otpMessage);

    void sendPasswordResetEmail(String to, String resetLink);

    void sendLandlordRequestRejectionEmail(User user, String reason);

    void sendLandlordRequestApprovalEmail(User user);
}