package com.rental.houserental.exceptions.auth;

import lombok.Getter;

@Getter
public class MaxAttemptsReachedException extends RuntimeException {
    private final String email;
    
    public MaxAttemptsReachedException(String message, String email) {
        super(message);
        this.email = email;
    }

} 