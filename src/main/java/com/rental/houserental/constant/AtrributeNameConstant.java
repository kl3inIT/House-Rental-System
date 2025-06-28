package com.rental.houserental.constant;

public final class AtrributeNameConstant {

    private AtrributeNameConstant() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Standard message attributes
    public static final String MESSAGE = "message";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String WARNING_MESSAGE = "warningMessage";
    public static final String INFO_MESSAGE = "infoMessage";
    
    // Legacy support (for GlobalExceptionHandler)
    public static final String ERROR = "error";
    
    public static final String STATUS = "status";
    public static final String EMAIL = "email";
    public static final String USER = "user";
    public static final String REGISTER_REQUEST = "registerRequest";
    public static final String LOGIN_REQUEST = "loginRequest";
    public static final String FORGOT_PASSWORD_REQUEST = "forgotPasswordRequest";
    public static final String RESET_PASSWORD_REQUEST = "resetPasswordRequest";
    public static final String PROPERTY_REQUEST = "property";
    public static final String BINDING_RESULT_KEY = "org.springframework.validation.BindingResult.registerRequest";
    public static final String BINDING_RESULT_FORGOT_PASSWORD = "org.springframework.validation.BindingResult.forgotPasswordRequest";
    public static final String BINDING_RESULT_RESET_PASSWORD = "org.springframework.validation.BindingResult.resetPasswordRequest";
    public static final String BINDING_RESULT_PROPERTY = "org.springframework.validation.BindingResult.property";

    public static final String OTP_EXPIRE = "otpExpire";
    public static final String OTP_FAIL_COUNT = "otpFailCount";
    public static final String OTP_REQUEST = "otpRequest";
    public static final String BINDING_RESULT_OTP = "org.springframework.validation.BindingResult.otpRequest";

    public static final String CATEGORIES = "categories";
}
