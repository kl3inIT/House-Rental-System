package com.rental.houserental.controller;

import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.rental.houserental.constant.Constant.AtrributeNames.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("error", model.containsAttribute("error") ? model.getAttribute("error") : null);
        model.addAttribute("message", model.containsAttribute("message") ? model.getAttribute("message") : null);
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute(REGISTER_REQUEST, new RegisterRequestDTO());
        model.addAttribute(ERROR, model.getAttribute(ERROR));
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequestDTO request, BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerRequest", result);
            redirectAttributes.addFlashAttribute("registerRequest", request);
            return "redirect:/register";
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.registerRequest", "Passwords do not match");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerRequest", result);
            redirectAttributes.addFlashAttribute("registerRequest", request);
            return "redirect:/register";
        }

        try {
            authService.register(request);
            redirectAttributes.addFlashAttribute("message", "Registration successful! Please check your email for the verification code.");
            return "redirect:/verify-otp";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage(Model model) {
        model.addAttribute("email", model.containsAttribute("email") ? model.getAttribute("email") : "");
        model.addAttribute("error", model.containsAttribute("error") ? model.getAttribute("error") : null);
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp, RedirectAttributes redirectAttributes) {
        try {
            if (authService.verifyOtp(email, otp)) {
                redirectAttributes.addFlashAttribute("message", "Email verified successfully! You can now login.");
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid or expired OTP. Please try again.");
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/verify-otp";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/verify-otp";
        }
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            authService.sendOtpForVerification(email);
            redirectAttributes.addFlashAttribute("message", "Verification code has been resent. Please check your inbox.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        redirectAttributes.addFlashAttribute("email", email);
        return "redirect:/verify-otp";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("error", model.containsAttribute("error") ? model.getAttribute("error") : null);
        model.addAttribute("message", model.containsAttribute("message") ? model.getAttribute("message") : null);
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            authService.forgotPassword(email);
            redirectAttributes.addFlashAttribute("message", "Password reset instructions have been sent to your email.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forgot-password";
        }
        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        model.addAttribute("error", model.containsAttribute("error") ? model.getAttribute("error") : null);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        try {
            authService.resetPassword(token, newPassword);
            redirectAttributes.addFlashAttribute("message", "Password has been reset successfully. You can now login with your new password.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reset-password?token=" + token;
        }
        return "redirect:/login";
    }
}