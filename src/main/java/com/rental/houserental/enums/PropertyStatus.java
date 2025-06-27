package com.rental.houserental.enums;

import com.rental.houserental.exceptions.property.InvalidPropertyStatusException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public enum PropertyStatus {
    DRAFT("Draft", "The property is saved as draft and not visible to tenants."),
    AVAILABLE("Available", "The property is available for rent and visible to tenants."),
    BOOKED("Booked", "The property has been booked and is waiting for confirmation."),
    RENTED("Rented", "The property has been rented and is no longer available."),
    UNAVAILABLE("Unavailable", "The property is temporarily unavailable for rent."),
    ADMIN_HIDDEN("Hidden by Admin", "The property is hidden by admin and cannot be modified by landlord."),
    ADMIN_BANNED("Banned by Admin", "The property is banned due to policy violation.");

    private final String displayName;
    private final String description;

    PropertyStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static PropertyStatus fromString(String status) {
        try {
            return PropertyStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPropertyStatusException("Invalid property status: " + status);
        }
    }

    public static List<Map<String, String>> getStatusList() {
        return Arrays.stream(PropertyStatus.values())
                .map(status -> Map.of(
                        "code", status.name(),
                        "displayName", status.getDisplayName(),
                        "description", status.getDescription()
                ))
                .toList();
    }

    public boolean isVisible() {
        return this == AVAILABLE || this == BOOKED || this == RENTED;
    }

    public boolean isAvailableForRent() {
        return this == AVAILABLE;
    }

    public boolean isDraft() {
        return this == DRAFT;
    }

    public boolean isBooked() {
        return this == BOOKED;
    }

    public boolean isAdminControlled() {
        return this == ADMIN_HIDDEN || this == ADMIN_BANNED;
    }

    public boolean canLandlordModify() {
        return !isAdminControlled();
    }

    public boolean isHiddenByAdmin() {
        return this == ADMIN_HIDDEN;
    }

    public boolean isBannedByAdmin() {
        return this == ADMIN_BANNED;
    }
}
