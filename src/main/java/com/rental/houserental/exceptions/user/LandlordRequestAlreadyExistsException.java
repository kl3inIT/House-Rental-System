package com.rental.houserental.exceptions.user;

public class LandlordRequestAlreadyExistsException extends RuntimeException {
    public LandlordRequestAlreadyExistsException(String message) {
        super(message);
    }
}