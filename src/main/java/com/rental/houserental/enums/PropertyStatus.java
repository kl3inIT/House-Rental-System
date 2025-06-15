package com.rental.houserental.enums;

public enum PropertyStatus {
    AVAILABLE,
    PENDING,
    RENTED,
    UNAVAILABLE;

    public static PropertyStatus fromString(String status) {
        try {
            return PropertyStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid property status: " + status);
        }
    }

}
