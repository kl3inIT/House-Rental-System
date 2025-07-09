package com.rental.houserental.advice;

import com.rental.houserental.entity.User;
import com.rental.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserService userService;

    @ModelAttribute
    public void addCurrentUserToModel(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("currentUser",user);
    }
}