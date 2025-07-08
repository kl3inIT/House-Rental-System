package com.rental.houserental.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public enum FurnishingType {
    FULL("Full"),
    BASIC("Basic"),
    NONE("None");

    private final String displayName;

    FurnishingType(String displayName) {
        this.displayName = displayName;
    }

    public static FurnishingType fromString(String type) {
        try {
            return FurnishingType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid furnishing type: " + type);
        }
    }

    public static List<Map<String, String>> getTypeList() {
        return Arrays.stream(FurnishingType.values())
                .map(type -> Map.of(
                        "code", type.name(),
                        "displayName", type.getDisplayName()
                ))
                .toList();
    }
}
