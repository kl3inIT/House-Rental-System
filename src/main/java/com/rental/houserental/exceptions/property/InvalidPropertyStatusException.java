package com.rental.houserental.exceptions.property;

public class InvalidPropertyStatusException extends RuntimeException {
    public InvalidPropertyStatusException(String message) {
        super(message);
    }
}
