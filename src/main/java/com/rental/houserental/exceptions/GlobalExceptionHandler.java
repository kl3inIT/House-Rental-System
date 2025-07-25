package com.rental.houserental.exceptions;

import com.rental.houserental.exceptions.auth.*;
import com.rental.houserental.exceptions.booking.InvalidBookingStatusException;
import com.rental.houserental.exceptions.category.CategoryNotFoundException;
import com.rental.houserental.exceptions.common.BadRequestException;
import com.rental.houserental.exceptions.common.ResourceNotFoundException;
import com.rental.houserental.exceptions.listing.InsufficientBalanceException;
import com.rental.houserental.exceptions.property.*;
import com.rental.houserental.exceptions.transaction.InvalidTransactionTypeException;
import com.rental.houserental.exceptions.user.*;
import com.rental.houserental.exceptions.wishlist.WishlistOperationException;
import jakarta.servlet.http.HttpServletRequest;
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

    @ExceptionHandler(SecurityException.class)
    public String handleEmailAlreadyVerifiedException(SecurityException ex, RedirectAttributes redirectAttributes) {
        log.warn("Security err: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Security err payment gateway SE Pay");
        return REDIRECT_INDEX;
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

    @ExceptionHandler(InvalidTransactionTypeException.class)
    public String handleInvalidTransactionTypeException(InvalidTransactionTypeException ex, Model model) {
        log.warn("Invalid transaction type: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "The specified transaction type is not valid.");
        return GENERIC_ERROR;
    }

    @ExceptionHandler(InvalidBookingStatusException.class)
    public String handleInvalidBookingStatusException(InvalidBookingStatusException ex, Model model) {
        log.warn("Invalid booking status: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "The specified booking status is not valid.");
        return GENERIC_ERROR;
    }

    @ExceptionHandler(WishlistOperationException.class)
    public String handleWishlistOperationException(WishlistOperationException ex, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("wishlist")) {
            return "redirect:/wishlist";
        }
        return "redirect:" + (referer != null ? referer : "/");
    }

    //generic phải để cuối để bắt hết được lỗi
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected error: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "An unexpected error occurred. Please try again later.");
        return ERROR_500;
    }

    @ExceptionHandler(LandlordRequestAlreadyExistsException.class)
    public String handleLandLordRequestExist(InvalidTransactionTypeException ex, Model model) {
        log.warn("Landlord request already exist: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "Landlord request already exist.");
        return GENERIC_ERROR;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        log.warn("Resource not found: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "The requested resource could not be found.");
        return ERROR_404;
    }

    @ExceptionHandler(PropertyDeleteException.class)
    public String handlePropertyDeleteException(PropertyDeleteException ex, RedirectAttributes redirectAttributes) {
        log.error("Property deletion failed: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Only delete when the building is in DRAFT state!. Please try again later.");
        return REDIRECT_LANDLORD_PROPERTIES;
    }

    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequestException(BadRequestException ex, Model model) {
        log.warn("Bad request: {}", ex.getMessage());
        model.addAttribute(MESSAGE, "The request was invalid. Please check your input and try again.");
        return GENERIC_ERROR;
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public String handleInsufficientBalanceException(InsufficientBalanceException ex, RedirectAttributes redirectAttributes) {
        log.warn("Insufficient balance: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR, "Your balance is insufficient to complete this transaction. Please top up your account.");
        return REDIRECT_LANDLORD_LISTINGS;
    }
}
