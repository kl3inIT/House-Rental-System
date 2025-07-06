package com.rental.houserental.controller;

import com.rental.houserental.dto.response.property.FeaturedPropertyResponseDTO;
import com.rental.houserental.entity.Category;
import com.rental.houserental.enums.SortOption;
import com.rental.houserental.service.CategoryService;
import com.rental.houserental.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class HomeController {

    private final PropertyService propertyService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        try {
            List<FeaturedPropertyResponseDTO> featuredProperties = propertyService.getFeaturedProperties();
            List<Category> categories = categoryService.getAllCategories();
            
            model.addAttribute(FEATURED_PROPERTIES, featuredProperties);
            model.addAttribute(CATEGORIES, categories);
            
            return "index";
        } catch (Exception e) {
            log.error("Error loading home page", e);
            model.addAttribute("error", "Unable to load featured properties. Please try again later.");
            return "index";
        }
    }

    @GetMapping("/properties/search")
    public String searchProperties(
            @ModelAttribute SearchPropertyCriteriaDTO searchCriteria,
            @RequestParam(required = false, defaultValue = "relevance") String sortBy,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String maxPrice,
            @PageableDefault(size = 12) Pageable pageable,
            Model model) {

        try {
            // Handle legacy parameters from home page
            if (propertyType != null && !propertyType.isEmpty()) {
                try {
                    searchCriteria.setPropertyType(Long.parseLong(propertyType));
                } catch (NumberFormatException e) {
                    log.warn("Invalid property type parameter: {}", propertyType);
                    // Ignore invalid property type
                }
            }
            
            if (maxPrice != null && !maxPrice.isEmpty()) {
                try {
                    searchCriteria.setMaxPrice(new BigDecimal(maxPrice));
                } catch (NumberFormatException e) {
                    log.warn("Invalid max price parameter: {}", maxPrice);
                    // Ignore invalid max price
                }
            }

            // Validate sortBy parameter
            try {
                SortOption.fromValue(sortBy);
            } catch (Exception e) {
                log.warn("Invalid sort parameter: {}, using default", sortBy);
                sortBy = "relevance";
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

            log.info("Search completed: {} properties found with criteria: {}", 
                    propertiesPage.getTotalElements(), searchCriteria);

            return "search-properties";
            
        } catch (Exception e) {
            log.error("Error during property search", e);
            model.addAttribute("error", "An error occurred while searching properties. Please try again.");
            model.addAttribute("properties", List.of());
            model.addAttribute("searchCriteria", searchCriteria);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("totalElements", 0L);
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentPage", 1);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortOptions", SortOption.values());
            return "search-properties";
        }
    }

    @GetMapping("/properties/{id}")
    public String propertyDetail(@PathVariable Long id, Model model) {
        try {
            SearchPropertyResponseDTO property = propertyService.getPropertyById(id);
            model.addAttribute("property", property);
            return "property-detail";
        } catch (Exception e) {
            log.error("Error loading property detail for ID: {}", id, e);
            // Redirect to search page if property not found
            return "redirect:/properties/search";
        }
    }
}
