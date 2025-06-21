package com.rental.houserental.controller;

import com.rental.houserental.dto.request.auth.OtpRequestDTO;
import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.UserStatus;
import com.rental.houserental.exceptions.auth.EmailAlreadyVerifiedException;
import com.rental.houserental.exceptions.user.UserNotFoundException;
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
import org.springframework.data.redis.core.RedisTemplate;

import static com.rental.houserental.constant.AtrributeNameConstant.*;
import static com.rental.houserental.constant.ViewNamesConstant.*;
import static com.rental.houserental.constant.OtpConstants.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RedisTemplate<String, String> redisTemplate;

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
        return REGISTER;
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute(REGISTER_REQUEST) RegisterRequestDTO request,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(BINDING_RESULT_KEY, result);
            redirectAttributes.addFlashAttribute(REGISTER_REQUEST, request);
            return REGISTER;
        }

        User user = authService.register(request);
        if (user.getStatus() == UserStatus.PENDING) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Account already registered but not verified. OTP has been resent. Please check your email.");
        } else {
            redirectAttributes.addFlashAttribute(MESSAGE, "Registration successful! Please check your email.");
        }
        return "redirect:/verify-otp?email=" + request.getEmail();

    }

    @GetMapping("/verify-otp")
    public String showVerifyOtp(Model model, @RequestParam(required = false) String email) {
        //kiem tra xem co otp Request neu nguoi dung reload trang
        OtpRequestDTO otpRequest = (OtpRequestDTO) model.asMap().get(OTP_REQUEST);
        if (otpRequest == null) {
            otpRequest = new OtpRequestDTO();
        }
        // Nếu có email từ query param, dùng nó (ưu tiên cao nhất)
        if (email != null && !email.isEmpty()) {
            otpRequest.setEmail(email);
        }
        model.addAttribute(OTP_REQUEST, otpRequest);
        String usedEmail = otpRequest.getEmail();
        if (usedEmail != null && !usedEmail.isEmpty()) {
            String expStr = redisTemplate.opsForValue().get(OTP_EXP_PREFIX + usedEmail);
            String failStr = redisTemplate.opsForValue().get(OTP_FAIL_PREFIX + usedEmail);
            model.addAttribute(OTP_EXPIRE, expStr);
            model.addAttribute(OTP_FAIL_COUNT, failStr);
        }
        return VERIFY_OTP;
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@Valid @ModelAttribute(OTP_REQUEST) OtpRequestDTO request,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        String email = request.getEmail();
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(BINDING_RESULT_OTP, result);
            redirectAttributes.addFlashAttribute(OTP_REQUEST, request);
            return "redirect:/verify-otp?email=" + email;
        }
        if (authService.verifyOtp(email, request.getOtp())) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Email verified successfully! You can now login.");
            return REDIRECT_LOGIN;
        } else {
            redirectAttributes.addFlashAttribute(ERROR, "Invalid or expired OTP. Please try again.");
            redirectAttributes.addFlashAttribute(OTP_REQUEST, request);
            return "redirect:/verify-otp?email=" + email;
        }
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email, RedirectAttributes redirectAttributes) {
        authService.resendOtp(email);
        redirectAttributes.addFlashAttribute(MESSAGE, "Verification code has been resent. Please check your inbox.");
        return "redirect:/verify-otp?email=" + email;
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