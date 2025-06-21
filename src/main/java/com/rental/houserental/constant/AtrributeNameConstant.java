package com.rental.houserental.constant;

public final class AtrributeNameConstant {

    private AtrributeNameConstant() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String MESSAGE = "message";
    public static final String STATUS = "status";
    public static final String ERROR = "error";
    public static final String EMAIL = "email";
    public static final String USER = "user";
    public static final String REGISTER_REQUEST = "registerRequest";
    public static final String LOGIN_REQUEST = "loginRequest";
    public static final String BINDING_RESULT_KEY = "org.springframework.validation.BindingResult.registerRequest";
    public static final String BINDING_RESULT_LOGIN = "org.springframework.validation.BindingResult.loginRequest";

    public static final String OTP_EXPIRE = "otpExpire";
    public static final String OTP_FAIL_COUNT = "otpFailCount";
    public static final String OTP_REQUEST = "otpRequest";
    public static final String BINDING_RESULT_OTP = "org.springframework.validation.BindingResult.otpRequest";
}
