package com.rental.houserental.constant;

public class Constant {

    private Constant() {}

    public static class CommonConstants {
        private CommonConstants() {}

        public static final String SUCCESS = "SUCCESS";


    }

    public static class ErrorMessages {
        private ErrorMessages() {}

        // Error Messages
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
        public static final String USER = "user";
        public static final String REGISTER_REQUEST = "registerRequest";
    }
}
