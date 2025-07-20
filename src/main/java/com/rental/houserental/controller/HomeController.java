package com.rental.houserental.controller;

import com.rental.houserental.dto.response.property.FeaturedPropertyResponseDTO;
import com.rental.houserental.entity.Category;
import com.rental.houserental.entity.User;
import com.rental.houserental.service.CategoryService;
import com.rental.houserental.service.PropertyService;
import com.rental.houserental.service.UserService;
import com.rental.houserental.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static com.rental.houserental.constant.AtrributeNameConstant.*;
import static com.rental.houserental.constant.ViewNamesConstant.*;
import com.rental.houserental.dto.request.property.SearchPropertyCriteriaDTO;
import com.rental.houserental.dto.response.property.SearchPropertyResponseDTO;


@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final PropertyService propertyService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final WishlistService wishlistService;

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        List<FeaturedPropertyResponseDTO> featuredProperties = propertyService.getFeaturedProperties(6);
        List<Category> categories = categoryService.getAllCategories();
        
        // Load wishlist status if user is authenticated
        if (principal != null) {
            try {
                User currentUser = userService.findByEmail(principal.getName());
                List<Long> propertyIds = featuredProperties.stream()
                        .map(FeaturedPropertyResponseDTO::getId)
                        .toList();
                
                Map<Long, Boolean> wishlistStatus = wishlistService.getWishlistStatus(currentUser.getId(), propertyIds);
                
                model.addAttribute("wishlistStatus", wishlistStatus);
            } catch (Exception e) {
                log.warn("Error loading wishlist status for user: {}", principal.getName(), e);
            }
        }
        
        model.addAttribute(FEATURED_PROPERTIES, featuredProperties);
        model.addAttribute(CATEGORIES, categories);
        return INDEX;
    }

    @GetMapping("/properties/search")
    public String searchProperties(
            @ModelAttribute SearchPropertyCriteriaDTO criteria,
            @RequestParam(value = "propertyTypes", required = false) Long propertyType,
            @RequestParam(value = "priceRanges", required = false) String priceRange,
            @PageableDefault(size = 12) Pageable pageable,
            Model model,
            Principal principal) {
        
        // Debug logging
        log.info("Search criteria - priceRange: {}, propertyType: {}", priceRange, propertyType);
        log.info("Search criteria DTO - priceRanges: {}, propertyTypes: {}", criteria.getPriceRanges(), criteria.getPropertyTypes());
        
        // Handle propertyTypes from home page search form (single value to list conversion)
        if (propertyType != null && criteria.getPropertyTypes().isEmpty()) {
            criteria.getPropertyTypes().add(propertyType);
        }
        
        // Handle priceRange from home page search form
        if (priceRange != null && !priceRange.trim().isEmpty() && criteria.getPriceRanges().isEmpty()) {
            criteria.getPriceRanges().add(priceRange);
            log.info("Added priceRange from request param: {}", priceRange);
        }
        
        log.info("Final search criteria - priceRanges: {}, propertyTypes: {}", 
                criteria.getPriceRanges(), criteria.getPropertyTypes());
        
        Page<SearchPropertyResponseDTO> page = propertyService.searchProperties(criteria, pageable);
        
        // Load wishlist status if user is authenticated
        if (principal != null) {
            try {
                User currentUser = userService.findByEmail(principal.getName());
                List<Long> propertyIds = page.getContent().stream()
                        .map(SearchPropertyResponseDTO::getId)
                        .toList();
                
                Map<Long, Boolean> wishlistStatus = wishlistService.getWishlistStatus(currentUser.getId(), propertyIds);
                model.addAttribute("wishlistStatus", wishlistStatus);
            } catch (Exception e) {
                log.warn("Error loading wishlist status for user: {}", principal.getName(), e);
            }
        }
        
        // Add sort options
        List<Map<String, String>> sortOptions = List.of(
            Map.of("value", "date", "displayName", "Latest"),
            Map.of("value", "price", "displayName", "Price"),
            Map.of("value", "views", "displayName", "Most Viewed"),
            Map.of("value", "area", "displayName", "Area"),
            Map.of("value", "bedrooms", "displayName", "Bedrooms"),
            Map.of("value", "bathrooms", "displayName", "Bathrooms")
        );
        
        model.addAttribute("properties", page.getContent());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage", page.getNumber() + 1);
        model.addAttribute("searchCriteria", criteria);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("sortOptions", sortOptions);
        model.addAttribute("sortBy", criteria.getSortBy() != null ? criteria.getSortBy() : "date");
        model.addAttribute("sortDirection", criteria.getSortDirection() != null ? criteria.getSortDirection() : "desc");
        
        return "search-properties";
    }
}
