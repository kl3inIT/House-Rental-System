package com.rental.houserental.controller;

import com.rental.houserental.dto.request.user.ChangePasswordRequestDTO;
import com.rental.houserental.dto.request.user.UpdateProfileRequestDTO;
import com.rental.houserental.dto.response.user.UserProfileResponseDTO;
import com.rental.houserental.service.BookingService;
import com.rental.houserental.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

import static com.rental.houserental.constant.ViewNamesConstant.USER_PROFILE;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final BookingService bookingService;

    @GetMapping("/profile")
    public String profile(Model model) {
        UserProfileResponseDTO userProfile = userService.getCurrentUserProfile();
        model.addAttribute("userProfile", userProfile);
        model.addAttribute("updateProfileRequest", userProfile);
        model.addAttribute("changePasswordRequest", new ChangePasswordRequestDTO());
        model.addAttribute("currentRented", bookingService.getCurrentRentedProperties());
        model.addAttribute("retalhistory", bookingService.getRentHistoryProperties());
        return USER_PROFILE; // templates/user/profile.html
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequestDTO request,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please check your input and try again");
            return USER_PROFILE;
        }
        try {
            userService.updateProfile(request);
            model.addAttribute("success", "Profile updated successfully");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        UserProfileResponseDTO userProfile = userService.getCurrentUserProfile();
        model.addAttribute("userProfile", userProfile);
        model.addAttribute("updateProfileRequest", request);
        model.addAttribute("changePasswordRequest", new ChangePasswordRequestDTO());
        model.addAttribute("currentRented", bookingService.getCurrentRentedProperties());
        model.addAttribute("retalhistory", bookingService.getRentHistoryProperties());
        return USER_PROFILE;
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordRequest") ChangePasswordRequestDTO request,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please check your input and try again");
            return USER_PROFILE;
        }
        try {
            userService.changePassword(request);
            model.addAttribute("success", "Password changed successfully");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        UserProfileResponseDTO userProfile = userService.getCurrentUserProfile();
        model.addAttribute("userProfile", userProfile);
        model.addAttribute("updateProfileRequest", userProfile);
        model.addAttribute("changePasswordRequest", request);
        model.addAttribute("currentRented", bookingService.getCurrentRentedProperties());
        model.addAttribute("retalhistory", bookingService.getRentHistoryProperties());
        return USER_PROFILE;
    }
}
