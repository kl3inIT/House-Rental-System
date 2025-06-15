package com.rental.houserental.enums;

public enum Gender {
    MALE,
    FEMALE,
    OTHER;

    public static Gender fromString(String gender) {
        try {
            return Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid property status: " + gender);
        }
    }
}