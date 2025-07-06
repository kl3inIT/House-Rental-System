package com.rental.houserental.exceptions.auth;

import lombok.Getter;

@Getter
public class OtpNotFoundException extends RuntimeException {

    private final String email;

    public OtpNotFoundException(String message, String email) {
        super(message);
        this.email = email;
    }
}
