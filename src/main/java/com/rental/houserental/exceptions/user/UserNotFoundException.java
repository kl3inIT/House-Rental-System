package com.rental.houserental.exceptions.user;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String redirectPath;

    public UserNotFoundException(String message, String redirectPath) {
        super(message);
        this.redirectPath = redirectPath;
    }

}
