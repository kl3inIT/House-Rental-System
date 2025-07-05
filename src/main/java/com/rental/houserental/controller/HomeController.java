package com.rental.houserental.controller;

import com.rental.houserental.dto.response.property.FeaturedPropertyResponseDTO;
import com.rental.houserental.entity.Category;
import com.rental.houserental.service.CategoryService;
import com.rental.houserental.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;


import java.util.List;

import static com.rental.houserental.constant.AtrributeNameConstant.*;
import static com.rental.houserental.constant.ViewNamesConstant.*;

import com.rental.houserental.dto.request.property.SearchPropertyCriteriaDTO;
import com.rental.houserental.dto.response.property.SearchPropertyResponseDTO;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PropertyService propertyService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("title", "Welcome to RentEase");

        List<Category> categories = categoryService.findAll();
        model.addAttribute(CATEGORIES, categories);

        List<FeaturedPropertyResponseDTO> featuredProperties = propertyService.getFeaturedProperties(6);
        model.addAttribute(FEATURED_PROPERTIES, featuredProperties);


        model.addAttribute("featuredPropertiesCount", featuredProperties.size());
        model.addAttribute("totalPropertiesCount", "10,000+");
        model.addAttribute("totalUsersCount", "50,000+");
        model.addAttribute("landlordCount", "500+");
        model.addAttribute("cityCount", "100+");

        return INDEX;
    }

    @GetMapping("/properties/search")
    public String searchProperties(
            SearchPropertyCriteriaDTO criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        // Add categories for the property type dropdown
        List<Category> categories = categoryService.findAll();
        model.addAttribute(CATEGORIES, categories);

        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<SearchPropertyResponseDTO> propertyPage = propertyService.searchProperties(criteria, pageable);

        model.addAttribute("properties", propertyPage.getContent());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", propertyPage.getTotalPages());
        model.addAttribute("searchCriteria", criteria);
        model.addAttribute("totalElements", propertyPage.getTotalElements());

        return "search-properties";
    }
}
