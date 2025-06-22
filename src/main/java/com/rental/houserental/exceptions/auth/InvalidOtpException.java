package com.rental.houserental.exceptions.auth;

import lombok.Getter;

@Getter
public class InvalidOtpException extends RuntimeException {

    private final String email;
    public InvalidOtpException(String message, String email) {
        super(message);
        this.email = email;
    }
} 