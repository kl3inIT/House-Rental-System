package com.rental.houserental.constant;

public final class ErrorMessageConstant {

    private ErrorMessageConstant() {
        // Prevent instantiation
    }
    public static final String MSG_404 = "The page you are looking for could not be found.";
    public static final String MSG_403 = "You do not have permission to access this page.";
    public static final String MSG_500 = "An internal server error occurred. Please try again later.";
    public static final String MSG_GENERIC = "An unexpected error has occurred.";
    
    public static final String PROPERTY_CREATE_SUCCESS_PUBLISH = "Property published successfully!";
    public static final String PROPERTY_CREATE_SUCCESS_DRAFT = "Property saved as draft successfully!";
    public static final String PROPERTY_CREATE_FAILED = "Failed to create property: ";
}
