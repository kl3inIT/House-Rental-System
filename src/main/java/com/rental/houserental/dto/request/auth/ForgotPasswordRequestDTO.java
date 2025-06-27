package com.rental.houserental.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequestDTO {
    
    @NotBlank(message = "Email address is required")
    @Email(message = "Please enter a valid email address")
    private String email;
} 