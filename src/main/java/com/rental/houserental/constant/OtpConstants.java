package com.rental.houserental.constant;

import java.time.Duration;

public final class OtpConstants {

    private OtpConstants() {
        // Prevent instantiation
    }

    public static final int OTP_LENGTH = 6;

    public static final String OTP_PREFIX = "otp:";
    public static final String OTP_FAIL_PREFIX = "otp:fail:";
    public static final String OTP_EXP_PREFIX = "otp:exp:";

    public static final Duration OTP_EXPIRY = Duration.ofMinutes(5);
}
