package com.rental.houserental.controller;

import com.rental.houserental.dto.response.property.FeaturedPropertyResponseDTO;
import com.rental.houserental.entity.Category;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

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

    @GetMapping("/")
    public String home(Model model) {
        List<FeaturedPropertyResponseDTO> featuredProperties = propertyService.getFeaturedProperties(6);
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute(FEATURED_PROPERTIES, featuredProperties);
        model.addAttribute(CATEGORIES, categories);
        return INDEX;
    }




//    @GetMapping("/properties/{id}")
//    public String propertyDetail(@PathVariable Long id, Model model) {
//        try {
//            SearchPropertyResponseDTO property = propertyService.getPropertyById(id);
//            model.addAttribute("property", property);
//            return "property-detail";
//        } catch (Exception e) {
//            log.error("Error loading property detail for ID: {}", id, e);
//            // Redirect to search page if property not found
//            return "redirect:/properties/search";
//        }
//    }

    @GetMapping("/properties/search")
    public String searchProperties(
            @ModelAttribute SearchPropertyCriteriaDTO criteria,
            @PageableDefault(size = 12) Pageable pageable,
            Model model) {
        Page<SearchPropertyResponseDTO> page = propertyService.searchProperties(criteria, pageable);
        model.addAttribute("properties", page.getContent());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage", page.getNumber() + 1);
        model.addAttribute("searchCriteria", criteria);
        model.addAttribute("categories", categoryService.getAllCategories());
        // TODO: add sort options if needed
        return "search-properties";
    }
}
