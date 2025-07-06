package com.rental.houserental.controller;

import com.rental.houserental.dto.response.property.FeaturedPropertyResponseDTO;
import com.rental.houserental.entity.Category;
import com.rental.houserental.enums.SortOption;
import com.rental.houserental.service.CategoryService;
import com.rental.houserental.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

import static com.rental.houserental.constant.AtrributeNameConstant.*;

import com.rental.houserental.dto.request.property.SearchPropertyCriteriaDTO;
import com.rental.houserental.dto.response.property.SearchPropertyResponseDTO;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PropertyService propertyService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        List<FeaturedPropertyResponseDTO> featuredProperties = propertyService.getFeaturedProperties();
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute(FEATURED_PROPERTIES, featuredProperties);
        model.addAttribute(CATEGORIES, categories);
        
        return "index";
    }

    @GetMapping("/properties/search")
    public String searchProperties(
            @ModelAttribute SearchPropertyCriteriaDTO searchCriteria,
            @RequestParam(required = false, defaultValue = "relevance") String sortBy,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String maxPrice,
            @PageableDefault(size = 12) Pageable pageable,
            Model model) {

        // Handle legacy parameters from home page
        if (propertyType != null && !propertyType.isEmpty()) {
            try {
                searchCriteria.setPropertyType(Long.parseLong(propertyType));
            } catch (NumberFormatException e) {
                // Ignore invalid property type
            }
        }
        
        if (maxPrice != null && !maxPrice.isEmpty()) {
            try {
                searchCriteria.setMaxPrice(new BigDecimal(maxPrice));
            } catch (NumberFormatException e) {
                // Ignore invalid max price
            }
        }

        // Search properties with sorting handled by service
        Page<SearchPropertyResponseDTO> propertiesPage = propertyService.searchPropertiesWithSorting(searchCriteria, sortBy, pageable);

        // Get categories for filter dropdown
        List<Category> categories = categoryService.getAllCategories();

        // Add attributes to model
        model.addAttribute("properties", propertiesPage.getContent());
        model.addAttribute("searchCriteria", searchCriteria);
        model.addAttribute("categories", categories);
        model.addAttribute("totalElements", propertiesPage.getTotalElements());
        model.addAttribute("totalPages", propertiesPage.getTotalPages());
        model.addAttribute("currentPage", propertiesPage.getNumber() + 1);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOptions", SortOption.values());

        return "search-properties";
    }

    @GetMapping("/properties/{id}")
    public String propertyDetail(@PathVariable Long id, Model model) {
        try {
            SearchPropertyResponseDTO property = propertyService.getPropertyById(id);
            model.addAttribute("property", property);
            return "property-detail";
        } catch (Exception e) {
            // Redirect to search page if property not found
            return "redirect:/properties/search";
        }
    }
}
