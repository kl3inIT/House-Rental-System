package com.rental.houserental.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginPageRequestDTO {
    
    // Spring binds these automatically from URL parameters
    private boolean error;
    private boolean logout;
    private boolean expired;
    private boolean disabled;
    private boolean locked;
    
    // Constructor for Spring to bind request parameters
    public LoginPageRequestDTO() {
        // Default constructor for Spring to instantiate this class
    }
    
    // Simple utility methods to check message types
    public boolean hasError() {
        return error;
    }
    
    public boolean hasLogout() {
        return logout;
    }
    
    public boolean hasExpired() {
        return expired;
    }
    
    public boolean hasDisabled() {
        return disabled;
    }
    
    public boolean hasLocked() {
        return locked;
    }

} 