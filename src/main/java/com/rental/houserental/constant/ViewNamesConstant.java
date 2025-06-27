package com.rental.houserental.constant;

public final class ViewNamesConstant {

    // View Names
    public static final String LOGIN = "login";
    public static final String REGISTER = "register";
    public static final String VERIFY_OTP = "verify-otp";
    public static final String FORGOT_PASSWORD = "forgot-password";
    public static final String RESET_PASSWORD = "reset-password";
    public static final String GENERIC_ERROR = "error/generic";
    public static final String LANDLORD_DASHBOARD = "landlord/dashboard";
    public static final String LANDLORD_NEW_LISTING = "landlord/new-listing";

    // Simple Redirects
    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String REDIRECT_REGISTER = "redirect:/register";
    public static final String REDIRECT_VERIFY_OTP = "redirect:/verify-otp";
    public static final String REDIRECT_FORGOT_PASSWORD = "redirect:/forgot-password";
    public static final String REDIRECT_LANDLORD_DASHBOARD = "redirect:/landlord/dashboard";

    // Utility methods for building redirects with parameters
    public static String redirectVerifyOtpWithEmail(String email) {
        return "redirect:/verify-otp?email=" + email;
    }
    
    public static String redirectResetPasswordWithToken(String token) {
        return "redirect:/reset-password?token=" + token;
    }

    // Prevent instantiation
    private ViewNamesConstant() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
