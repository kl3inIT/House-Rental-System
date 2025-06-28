package com.rental.houserental.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("title", "Welcome to RentEase");
        
        // TODO: Replace with actual data from services
        model.addAttribute("featuredPropertiesCount", 3);
        model.addAttribute("totalPropertiesCount", "10,000+");
        model.addAttribute("totalUsersCount", "50,000+");
        model.addAttribute("landlordCount", "500+");
        model.addAttribute("cityCount", "100+");
        
        return "index";
    }
}
