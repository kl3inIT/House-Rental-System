package com.rental.houserental.enums;

import com.rental.houserental.exceptions.user.InvalidUserRoleException;

public enum UserRole {
    ADMIN,
    USER,
    LANDLORD;

    public static UserRole fromString(String role) {
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (InvalidUserRoleException e) {
            throw new InvalidUserRoleException(e.getMessage());
        }
    }
}
