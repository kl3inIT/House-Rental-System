package com.rental.houserental.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequestDTO {
    
    @NotBlank(message = "Email address is required")
    @Email(message = "Please enter a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
} 