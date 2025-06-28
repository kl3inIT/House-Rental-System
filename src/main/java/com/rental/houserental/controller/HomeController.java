package com.rental.houserental.controller;

import com.rental.houserental.dto.response.FeaturedPropertyResponseDTO;
import com.rental.houserental.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PropertyService propertyService;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("title", "Welcome to RentEase");
        
        // Load featured properties directly in controller
        List<FeaturedPropertyResponseDTO> featuredProperties = propertyService.getFeaturedProperties(6);
        model.addAttribute("featuredProperties", featuredProperties);
        
        // Statistics data
        model.addAttribute("featuredPropertiesCount", featuredProperties.size());
        model.addAttribute("totalPropertiesCount", "10,000+");
        model.addAttribute("totalUsersCount", "50,000+");
        model.addAttribute("landlordCount", "500+");
        model.addAttribute("cityCount", "100+");
        
        return "index";
    }
}
