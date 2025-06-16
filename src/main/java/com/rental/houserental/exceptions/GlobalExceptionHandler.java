package com.rental.houserental.exceptions;

import com.rental.houserental.exceptions.auth.EmailAlreadyExistsException;
import com.rental.houserental.exceptions.auth.EmailAlreadyVerifiedException;
import com.rental.houserental.exceptions.auth.PasswordNotMatchException;
import com.rental.houserental.exceptions.user.InvalidUserRoleException;
import com.rental.houserental.exceptions.user.InvalidUserStatusException;
import com.rental.houserental.exceptions.user.UserNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.rental.houserental.constant.Constant.AtrributeNames.*;
import static com.rental.houserental.constant.Constant.ViewNames.*;
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUserRoleException.class)
    public String handleInvalidUserRoleException(InvalidUserRoleException ex, Model model) {
        log.warn("Invalid user role: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "The specified user role is not valid.");
        return GENERIC_ERROR;
    }

    @ExceptionHandler(InvalidUserStatusException.class)
    public String handleInvalidUserStatusException(InvalidUserStatusException ex, Model model) {
        log.warn("Invalid user status: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "The specified user status is not valid.");
        return GENERIC_ERROR;
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailAlreadyExistsException(EmailAlreadyExistsException ex, RedirectAttributes redirectAttributes) {
        log.warn("Email already exists: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(MESSAGE, "The email address is already registered. Please use a different email.");
        return REDIRECT_REGISTER;
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public String handlePasswordNotMatchException(PasswordNotMatchException ex, RedirectAttributes redirectAttributes) {
        log.warn("Password mismatch: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(MESSAGE, "The passwords do not match. Please try again.");
        return REDIRECT_REGISTER;
    }

    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public String handleEmailAlreadyVerifiedException(EmailAlreadyVerifiedException ex, RedirectAttributes redirectAttributes) {
        log.warn("Email already verified: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(MESSAGE, "This email has already been verified. Please log in.");
        return REDIRECT_LOGIN;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException ex, Model model) {
        log.warn("User not found: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "The user you are looking for does not exist.");
        return GENERIC_ERROR;
    }

    //generic phải để cuối để bắt hết được lỗi
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected error: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "An unexpected error occurred. Please try again later.");
        return "error/500"; //
    }
}
