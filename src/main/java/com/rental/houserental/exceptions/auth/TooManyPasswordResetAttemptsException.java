package com.rental.houserental.exceptions.auth;

public class TooManyPasswordResetAttemptsException extends RuntimeException {
    public TooManyPasswordResetAttemptsException(String message) {
        super(message);
    }
} 