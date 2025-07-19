package com.rental.houserental.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequestDTO {
    
    @NotBlank(message = "Token is required")
    private String token;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String newPassword;
    
    @NotBlank(message = "Password confirmation is required")
    @Size(min = 8, max = 50, message = "Password confirmation must be between 8 and 50 characters")
    private String confirmPassword;
    
    // Custom validation method
    public boolean isPasswordMatching() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
} 