package com.rental.houserental.enums;

import com.rental.houserental.exceptions.booking.InvalidBookingStatusException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public enum BookingStatus {
    PENDING("Pending", "The booking is awaiting confirmation from the landlord."),
    CONFIRMED("Confirmed", "The landlord has accepted the booking."),
    CANCELLED("Cancelled", "The booking was cancelled by the user."),
    COMPLETED("Completed", "The stay has been completed successfully."),
    REJECTED("Rejected", "The landlord has rejected the booking request.");

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

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isConfirmed() {
        return this == CONFIRMED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
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

