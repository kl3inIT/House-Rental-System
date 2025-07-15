package com.rental.houserental.controller;

import com.rental.houserental.dto.response.user.UserProfileResponseDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.service.LandlordUpgradeRequestService;
import com.rental.houserental.service.UserService;
import com.rental.houserental.exceptions.user.LandlordRequestAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/landlord-upgrade-requests")
@RequiredArgsConstructor
public class LandlordUpgradeRequestController {

    private final LandlordUpgradeRequestService requestService;
    private final UserService userService;

    // User gửi request
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public String createRequest(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestParam String reason,
            Model model) {
        User user = userService.findByEmail(principal.getUsername());
        try {
            requestService.createRequest(user, user.getName(), user.getPhone(), reason);
            model.addAttribute("success", "Your upgrade request has been submitted!");
        } catch (LandlordRequestAlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to submit request: " + e.getMessage());
        }
        // Lấy lại thông tin user profile để render view
        UserProfileResponseDTO userProfile = userService.getCurrentUserProfile();
        model.addAttribute("userProfile", userProfile);
        model.addAttribute("updateProfileRequest",
                new com.rental.houserental.dto.request.user.UpdateProfileRequestDTO());
        model.addAttribute("changePasswordRequest",
                new com.rental.houserental.dto.request.user.ChangePasswordRequestDTO());
        return "user/profile";
    }
}