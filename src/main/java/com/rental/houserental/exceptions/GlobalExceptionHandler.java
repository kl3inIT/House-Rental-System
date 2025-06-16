package com.rental.houserental.exceptions;

import com.rental.houserental.exceptions.user.InvalidUserRoleException;
import com.rental.houserental.exceptions.user.InvalidUserStatusException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import static com.rental.houserental.constant.Constant.AtrributeNames.MESSAGE;
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUserRoleException.class)
    public String handleInvalidUserRoleException(InvalidUserRoleException ex, Model model) {
        log.warn("Invalid user role: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "The specified user role is not valid.");
        return "error/generic";
    }

    @ExceptionHandler(InvalidUserStatusException.class)
    public String handleInvalidUserStatusException(InvalidUserStatusException ex, Model model) {
        log.warn("Invalid user status: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "The specified user status is not valid.");
        return "error/generic";
    }

    //generic phải để cuối để bắt hết được lỗi
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected error: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "An unexpected error occurred. Please try again later.");
        return "error/500"; //
    }
}
