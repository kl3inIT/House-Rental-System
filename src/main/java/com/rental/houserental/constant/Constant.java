package com.rental.houserental.constant;

import java.time.Duration;

public class Constant {

    private Constant() {}

    public static class CommonConstants {
        private CommonConstants() {}

        public static final String REDIRECT = "redirect:";
    }

    public static class ErrorMessages {
        private ErrorMessages() {}

        public static final String MSG_404 = "The page you are looking for could not be found.";
        public static final String MSG_403 = "You do not have permission to access this page.";
        public static final String MSG_500 = "An internal server error occurred. Please try again later.";
        public static final String MSG_GENERIC = "An unexpected error has occurred.";
    }

    public static class AtrributeNames {
        private AtrributeNames() {}

        public static final String MESSAGE = "message";
        public static final String STATUS = "status";
        public static final String ERROR = "error";
        public static final String EMAIL = "email";
        public static final String USER = "user";
        public static final String REGISTER_REQUEST = "registerRequest";
        public static final String BINDING_RESULT_KEY = "org.springframework.validation.BindingResult.registerRequest";
    }

    public static class ViewNames {
        private ViewNames() {}

        public static final String LOGIN = "login";
        public static final String REGISTER = "register";
        public static final String VERIFY_OTP = "verify-otp";
        public static final String GENERIC_ERROR = "error/generic";

        public static final String REDIRECT_LOGIN = "redirect:/login";
        public static final String REDIRECT_REGISTER = "redirect:/register";
        public static final String REDIRECT_VERIFY_OTP = "redirect:/verify-otp";

    }

    public static class OtpConstants {
        private OtpConstants() {}

        public static final int OTP_LENGTH = 6;
        public static final String OTP_PREFIX = "otp:";
        public static final Duration OTP_EXPIRY = Duration.ofMinutes(5); // OTP valid for 5 minutes
    }
}
