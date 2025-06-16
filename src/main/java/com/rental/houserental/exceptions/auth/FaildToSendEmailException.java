package com.rental.houserental.exceptions.auth;

public class FaildToSendEmailException extends RuntimeException {
    public FaildToSendEmailException(String message) {
        super(message);
    }
}
