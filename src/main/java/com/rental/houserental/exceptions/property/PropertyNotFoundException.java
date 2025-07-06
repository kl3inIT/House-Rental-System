package com.rental.houserental.exceptions.property;

public class PropertyNotFoundException extends RuntimeException {
    public PropertyNotFoundException(String message) {
        super(message);
    }
    
    public PropertyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 