package com.rental.houserental.enums;

public enum LandlordUpgradeRequestStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String displayName;

    public String getName() {
        return displayName;
    }

    LandlordUpgradeRequestStatus(String displayName) {
        this.displayName = displayName;
    }
}