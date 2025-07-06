package com.rental.houserental.exceptions.booking;

public class InvalidBookingStatusException extends RuntimeException {
    public InvalidBookingStatusException(String message) {
        super(message);
    }
}
