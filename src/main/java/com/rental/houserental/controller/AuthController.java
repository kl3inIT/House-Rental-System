package com.rental.houserental.controller;

import com.rental.houserental.dto.request.auth.LoginRequestDTO;
import com.rental.houserental.dto.request.auth.LoginPageRequestDTO;
import com.rental.houserental.dto.request.auth.OtpRequestDTO;
import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.UserStatus;
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
    public String loginPage(Model model, LoginPageRequestDTO pageRequest) {
        
        // Add login request DTO for form binding
        if (!model.containsAttribute(LOGIN_REQUEST)) {
            model.addAttribute(LOGIN_REQUEST, new LoginRequestDTO());
        }
        
        // Add page request for message handling
        model.addAttribute("pageRequest", pageRequest);
        
        return LOGIN;
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
            return REDIRECT_REGISTER;
        }

        User user = authService.register(request);
        if (user.getStatus() == UserStatus.PENDING) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Account already registered but not verified. OTP has been resent. Please check your email.");
        } else {
            redirectAttributes.addFlashAttribute(MESSAGE, "Registration successful! Please check your email.");
        }
        return redirectVerifyOtpWithEmail(request.getEmail());
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtp(Model model, @RequestParam(required = false) String email) {
        // Kiểm tra xem có OTP Request nếu người dùng reload trang
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
            return redirectVerifyOtpWithEmail(email);
        }
        if (authService.verifyOtp(email, request.getOtp())) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Email verified successfully! You can now login.");
            return REDIRECT_LOGIN;
        } else {
            redirectAttributes.addFlashAttribute(ERROR, "Invalid or expired OTP. Please try again.");
            redirectAttributes.addFlashAttribute(OTP_REQUEST, request);
            return redirectVerifyOtpWithEmail(email);
        }
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email, RedirectAttributes redirectAttributes) {
            authService.resendOtp(email);
            redirectAttributes.addFlashAttribute(MESSAGE, "Verification code has been resent. Please check your inbox.");
        return redirectVerifyOtpWithEmail(email);
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute(ERROR, model.containsAttribute(ERROR) ? model.getAttribute(ERROR) : null);
        model.addAttribute(MESSAGE, model.containsAttribute(MESSAGE) ? model.getAttribute(MESSAGE) : null);
        return FORGOT_PASSWORD;
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            authService.forgotPassword(email);
            redirectAttributes.addFlashAttribute(MESSAGE, "Password reset instructions have been sent to your email.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR, e.getMessage());
            return REDIRECT_FORGOT_PASSWORD;
        }
        return REDIRECT_LOGIN;
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        model.addAttribute(ERROR, model.containsAttribute(ERROR) ? model.getAttribute(ERROR) : null);
        return RESET_PASSWORD;
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        try {
            authService.resetPassword(token, newPassword);
            redirectAttributes.addFlashAttribute(MESSAGE, "Password has been reset successfully. You can now login with your new password.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR, e.getMessage());
            return redirectResetPasswordWithToken(token);
        }
        return REDIRECT_LOGIN;
    }
}