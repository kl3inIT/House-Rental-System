package com.rental.houserental.enums;

import com.rental.houserental.exceptions.user.InvalidUserStatusException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public enum UserStatus {
    ACTIVE("Active", "The user is active and can use the system normally."),
    SUSPENDED("Suspended", "The user is temporarily suspended due to violations."),
    BANNED("Banned", "The user is permanently banned from the system."),
    PENDING("Pending", "The user is awaiting approval (e.g., landlord registration).");

    private final String displayName;
    private final String description;

    UserStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static UserStatus fromString(String status) {
        try {
            return UserStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidUserStatusException("Invalid user status: " + status);
        }
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static List<Map<String, String>> getStatusList() {
        return Arrays.stream(UserStatus.values())
                .map(status -> Map.of(
                        "code", status.name(),
                        "displayName", status.getDisplayName(),
                        "description", status.getDescription()
                ))
                .toList();
    }
}
