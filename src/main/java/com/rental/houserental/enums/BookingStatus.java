package com.rental.houserental.enums;

import com.rental.houserental.exceptions.booking.InvalidBookingStatusException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public enum BookingStatus {
    CONFIRMED("Confirmed", "Booking and deposit successful."),
    ACTIVE("Active", "The tenant has checked in and is currently staying."),
    COMPLETED("Completed", "The stay has been completed successfully."),
    CANCELLED("Cancelled", "The booking was cancelled");

    private final String displayName;
    private final String description;

    BookingStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static BookingStatus fromString(String status) {
        try {
            return BookingStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidBookingStatusException("Invalid booking status: " + status);
        }
    }

    public boolean isConfirmed() {
        return this == CONFIRMED;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }

    public static List<Map<String, String>> getStatusList() {
        return Arrays.stream(BookingStatus.values())
                .map(status -> Map.of(
                        "code", status.name(),
                        "displayName", status.getDisplayName(),
                        "description", status.getDescription()
                ))
                .toList();
    }
}
