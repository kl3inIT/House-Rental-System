package com.rental.houserental.exceptions;

import com.rental.houserental.exceptions.user.InvalidUserRoleException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUserRoleException.class)
    public String handleInvalidUserRoleException(InvalidUserRoleException ex, Model model) {
        log.warn("Invalid user role: {}", ex.getMessage());
        model.addAttribute("message", "The specified user role is not valid.");
        return "error/generic";
    }

    //generic phải để cuối để bắt hết được lỗi
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected error: {}", ex.getMessage());
        model.addAttribute("message", "An unexpected error occurred. Please try again later.");
        return "error/500"; //
    }
}
