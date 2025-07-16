package com.rental.houserental.constant;

public final class ViewNamesConstant {

    // View Names
    public static final String INDEX = "index";
    public static final String LOGIN = "login";
    public static final String REGISTER = "register";
    public static final String VERIFY_OTP = "verify-otp";
    public static final String FORGOT_PASSWORD = "forgot-password";
    public static final String RESET_PASSWORD = "reset-password";
    public static final String GENERIC_ERROR = "error/generic";
    public static final String ERROR_403 = "error/403";
    public static final String ERROR_404 = "error/404";
    public static final String ERROR_500 = "error/500";

    // Transaction Views
    public static final String WALLET_TRANSACTION = "wallet-transactions";
    public static final String WALLET_DEPOSIT = "wallet-deposit";

    // Admin Transaction Views
    public static final String ADMIN_TRANSACTION = "admin/transactions";
    // Dashboard Views
    public static final String ADMIN_DASHBOARD = "admin/dashboard";
    public static final String LANDLORD_DASHBOARD = "landlord/dashboard";
    public static final String LANDLORD_PROPERTY_FORM = "landlord/property-form";
    public static final String LANDLORD_PROPERTIES = "landlord/properties";
    
    // User Views
    public static final String USER_PROFILE = "user/profile";
    public static final String PROPERTY_DETAIL = "property-detail";
    public static final String MY_BOOKING = "my-bookings";
    public static final String BOOKING_DETAIL = "booking-detail";

    // Checkout View
    public static final String CHECKOUT = "checkout";

    // Simple Redirects
    public static final String REDIRECT_INDEX = "redirect:/";
    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String REDIRECT_REGISTER = "redirect:/register";
    public static final String REDIRECT_VERIFY_OTP = "redirect:/verify-otp";
    public static final String REDIRECT_FORGOT_PASSWORD = "redirect:/forgot-password";
    public static final String REDIRECT_ADMIN_DASHBOARD = "redirect:/admin/dashboard";
    public static final String REDIRECT_LANDLORD_DASHBOARD = "redirect:/landlord/dashboard";
    public static final String REDIRECT_LANDLORD_NEW_LISTING = "redirect:/landlord/properties/new";
    public static final String REDIRECT_USER_PROFILE = "redirect:/user/profile";
    public static final String REDIRECT_WALLET_TRANSACTION = "redirect:/wallet/transactions";
    public static final String REDIRECT_LANDLORD_PROPERTIES = "redirect:/landlord/properties";
    public static final String REDIRECT_MY_BOOKING = "redirect:/my-bookings";


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
