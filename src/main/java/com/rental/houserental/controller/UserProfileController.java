package com.rental.houserental.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.rental.houserental.constant.ViewNamesConstant.REDIRECT_USER_PROFILE;
import static com.rental.houserental.constant.ViewNamesConstant.USER_PROFILE;

@Controller
@RequiredArgsConstructor
public class UserProfileController {
    @GetMapping("/user/profile")
    public String profile() {
        return USER_PROFILE; // templates/user/profile.html
    }
}
