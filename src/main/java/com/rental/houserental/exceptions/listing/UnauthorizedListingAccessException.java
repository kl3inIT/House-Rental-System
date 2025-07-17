package com.rental.houserental.exceptions.listing;

public class UnauthorizedListingAccessException extends RuntimeException {
    
    public UnauthorizedListingAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedListingAccessException(String message, Throwable cause) {
        super(message, cause);
    }
} 