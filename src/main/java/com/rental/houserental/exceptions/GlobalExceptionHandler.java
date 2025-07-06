package com.rental.houserental.exceptions;

import com.rental.houserental.exceptions.auth.*;
import com.rental.houserental.exceptions.category.CategoryNotFoundException;
import com.rental.houserental.exceptions.property.InvalidPropertyStatusException;
import com.rental.houserental.exceptions.property.ImageUploadException;
import com.rental.houserental.exceptions.property.FileDeleteException;
import com.rental.houserental.exceptions.property.PropertyNotFoundException;
import com.rental.houserental.exceptions.user.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.rental.houserental.constant.AtrributeNameConstant.*;
import static com.rental.houserental.constant.ViewNamesConstant.*;

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
        redirectAttributes.addFlashAttribute(ERROR, "The email address is already registered. Please use a different email.");
        return REDIRECT_REGISTER;
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public String handlePasswordNotMatchException(PasswordNotMatchException ex, RedirectAttributes redirectAttributes) {
        log.warn("Password mismatch: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "The passwords do not match. Please try again.");
        return REDIRECT_REGISTER;
    }

    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public String handleEmailAlreadyVerifiedException(EmailAlreadyVerifiedException ex, RedirectAttributes redirectAttributes) {
        log.warn("Email already verified: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "This email has already been verified. Please log in.");
        return REDIRECT_LOGIN;
    }

    @ExceptionHandler(UserSuspendedException.class)
    public String handleUserSuspendedException(UserSuspendedException ex, RedirectAttributes redirectAttributes) {
        log.warn("User suspended: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Your account has been temporarily suspended. Please contact support.");
        return REDIRECT_REGISTER;
    }

    @ExceptionHandler(UserBannedException.class)
    public String handleUserBannedException(UserBannedException ex, RedirectAttributes redirectAttributes) {
        log.warn("User banned: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Your account has been permanently banned. Please contact support.");
        return REDIRECT_REGISTER;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException ex, RedirectAttributes redirectAttributes) {
        log.warn("User not found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "User not found. Please register first.");

        // Use the redirectPath from the exception if available, otherwise default to register
        String redirectPath = ex.getRedirectPath();
        return redirectPath != null ? redirectPath : REDIRECT_REGISTER;
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public String handleUserNotVerifiedException(UserNotVerifiedException ex, RedirectAttributes redirectAttributes) {
        log.warn("User not verified: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Please verify your email before logging in.");
        return REDIRECT_LOGIN;
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public String handleInvalidCredentialsException(InvalidCredentialsException ex, RedirectAttributes redirectAttributes) {
        log.warn("Invalid credentials: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Invalid email or password. Please try again.");
        return REDIRECT_LOGIN;
    }

    @ExceptionHandler(OtpNotFoundException.class)
    public String handleOtpNotFoundException(OtpNotFoundException ex, RedirectAttributes redirectAttributes) {
        log.warn("OTP not found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Verification code not found or expired. Please request a new code.");
        return redirectVerifyOtpWithEmail(ex.getEmail());
    }

    @ExceptionHandler(InvalidOtpException.class)
    public String handleInvalidOtpException(InvalidOtpException ex, RedirectAttributes redirectAttributes) {
        log.warn("Invalid OTP: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Invalid verification code. Please try again.");
        return redirectVerifyOtpWithEmail(ex.getEmail());
    }

    @ExceptionHandler(MaxAttemptsReachedException.class)
    public String handleMaxAttemptsReachedException(MaxAttemptsReachedException ex, RedirectAttributes redirectAttributes) {
        log.warn("Max attempts reached: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Too many incorrect attempts. Please request a new verification code.");

        return redirectVerifyOtpWithEmail(ex.getEmail());
    }

    @ExceptionHandler(TooManyPasswordResetAttemptsException.class)
    public String handleTooManyPasswordResetAttemptsException(TooManyPasswordResetAttemptsException ex, RedirectAttributes redirectAttributes) {
        log.warn("Too many password reset attempts: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Too many password reset requests. Please try again in 15 minutes.");
        return REDIRECT_FORGOT_PASSWORD;
    }

    @ExceptionHandler(InvalidResetTokenException.class)
    public String handleInvalidResetTokenException(InvalidResetTokenException ex, RedirectAttributes redirectAttributes) {
        log.warn("Invalid reset token: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Invalid or expired reset link. Please request a new password reset.");
        return REDIRECT_FORGOT_PASSWORD;
    }

    @ExceptionHandler(FaildToSendEmailException.class)
    public String handleFaildToSendEmailException(FaildToSendEmailException ex, RedirectAttributes redirectAttributes) {
        log.error("Failed to send email: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Failed to send email. Please try again later or contact support.");
        return REDIRECT_FORGOT_PASSWORD;
    }

    @ExceptionHandler(InvalidPropertyStatusException.class)
    public String handleInvalidPropertyStatusException(InvalidPropertyStatusException ex, Model model) {
        log.warn("Invalid property status: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "The specified property status is not valid.");
        return GENERIC_ERROR;
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public String handleCategoryNotFoundException(CategoryNotFoundException ex, Model model) {
        log.warn("Category not found: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "The specified category does not exist.");
        return GENERIC_ERROR;
    }

    @ExceptionHandler(PropertyNotFoundException.class)
    public String handlePropertyNotFoundException(PropertyNotFoundException ex, Model model) {
        log.warn("Property not found: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "The requested property could not be found.");
        return ERROR_404;
    }

    @ExceptionHandler(ImageUploadException.class)
    public String handleImageUploadException(ImageUploadException ex, RedirectAttributes redirectAttributes) {
        log.error("Image upload failed: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Failed to upload images. Property was not created. Please try again.");
        return REDIRECT_LANDLORD_NEW_LISTING;
    }

    @ExceptionHandler(FileDeleteException.class)
    public String handleFileDeleteException(FileDeleteException ex, Model model) {
        log.error("File deletion failed: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "Failed to delete file. Please try again later.");
        return GENERIC_ERROR;
    }


    //generic phải để cuối để bắt hết được lỗi
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected error: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "An unexpected error occurred. Please try again later.");
        return ERROR_500;
    }
}
