package com.rental.houserental.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserProfileController {
    @GetMapping("/user/profile")
    public String profile() {
        return "user/profile"; // templates/user/profile.html
    }
}
