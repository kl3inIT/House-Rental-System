package com.rental.houserental.controller;

import com.rental.houserental.dto.request.user.ChangePasswordRequestDTO;
import com.rental.houserental.dto.request.user.UpdateProfileRequestDTO;
import com.rental.houserental.dto.response.user.UserProfileResponseDTO;
import com.rental.houserental.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.rental.houserental.constant.ViewNamesConstant.REDIRECT_USER_PROFILE;
import static com.rental.houserental.constant.ViewNamesConstant.USER_PROFILE;

@Controller
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @GetMapping("/user/profile")
    public String profile(Model model) {
        UserProfileResponseDTO userProfile = userService.getCurrentUserProfile();
        UpdateProfileRequestDTO updateProfileRequest = new UpdateProfileRequestDTO();
        updateProfileRequest.setName(userProfile.getName());
        updateProfileRequest.setEmail(userProfile.getEmail());
        updateProfileRequest.setPhone(userProfile.getPhone());
        updateProfileRequest.setAddress(userProfile.getAddress());
        updateProfileRequest.setGender(userProfile.getGender());
        if (userProfile.getDateOfBirth() != null) {
            updateProfileRequest.setDateOfBirth(userProfile.getDateOfBirth().toLocalDate());
        }

        model.addAttribute("userProfile", userProfile);
        model.addAttribute("updateProfileRequest", updateProfileRequest);
        model.addAttribute("changePasswordRequest", new ChangePasswordRequestDTO());
        return USER_PROFILE; // templates/user/profile.html
    }

    @PostMapping("/user/profile/update")
    public String updateProfile(@Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequestDTO request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check your input and try again");
            return REDIRECT_USER_PROFILE;
        }

        try {
            userService.updateProfile(request);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return REDIRECT_USER_PROFILE;
    }

    @PostMapping("/user/profile/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordRequest") ChangePasswordRequestDTO request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check your input and try again");
            return REDIRECT_USER_PROFILE;
        }

        try {
            userService.changePassword(request);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return REDIRECT_USER_PROFILE;
    }

    // REST API endpoints for AJAX calls
    @GetMapping("/api/user/profile")
    @ResponseBody
    public ResponseEntity<UserProfileResponseDTO> getProfile() {
        UserProfileResponseDTO profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/api/user/profile")
    @ResponseBody
    public ResponseEntity<?> updateProfileApi(@Valid @RequestBody UpdateProfileRequestDTO request) {
        try {
            UserProfileResponseDTO updatedProfile = userService.updateProfile(request);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/user/change-password")
    @ResponseBody
    public ResponseEntity<?> changePasswordApi(@Valid @RequestBody ChangePasswordRequestDTO request) {
        try {
            userService.changePassword(request);
            return ResponseEntity.ok().body("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
