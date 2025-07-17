package com.rental.houserental.controller;

import com.rental.houserental.dto.request.property.CreatePropertyRequestDTO;
import com.rental.houserental.dto.request.property.LandlordPropertyFilterDTO;
import com.rental.houserental.dto.request.property.UpdatePropertyRequestDTO;
import com.rental.houserental.dto.response.property.AmenityDTO;
import com.rental.houserental.dto.response.property.PropertyDetailDTO;
import com.rental.houserental.dto.response.property.PropertyListItemDTO;
import com.rental.houserental.dto.response.property.PropertyStatsDTO;
import com.rental.houserental.entity.Amenity;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.FurnishingType;
import com.rental.houserental.service.AmenityService;
import com.rental.houserental.service.CategoryService;
import com.rental.houserental.service.PropertyService;
import com.rental.houserental.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

import static com.rental.houserental.constant.AtrributeNameConstant.*;
import static com.rental.houserental.constant.AtrributeNameConstant.CATEGORIES;
import static com.rental.houserental.constant.AtrributeNameConstant.PROPERTY_REQUEST;
import static com.rental.houserental.constant.AtrributeNameConstant.SUCCESS_MESSAGE;
import static com.rental.houserental.constant.ViewNamesConstant.*;

@Controller
@RequestMapping("/landlord/properties")
@PreAuthorize("hasRole('LANDLORD')")
@RequiredArgsConstructor
@Slf4j
public class LandlordPropertiesController {

    private final PropertyService propertyService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final AmenityService amenityService;

    @GetMapping("/new")
    public String showCreateForm(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute(PROPERTY_REQUEST, new CreatePropertyRequestDTO());
        model.addAttribute(CATEGORIES, categoryService.findAll());
        List<Map<String, String>> types = FurnishingType.getTypeList();
        model.addAttribute(FURNISHING_TYPES, types);
        model.addAttribute("amenities", amenityService.findAll());
        model.addAttribute("actionUrl", "/landlord/properties");
        return LANDLORD_PROPERTY_FORM;
    }

    @PostMapping()
    public String createProperty(@Valid @ModelAttribute(PROPERTY_REQUEST) CreatePropertyRequestDTO request,
                                 BindingResult bindingResult,
                                 @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
                                 Authentication authentication,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(PROPERTY_REQUEST, request);
            model.addAttribute(CATEGORIES, categoryService.findAll());
            List<Map<String, String>> types = FurnishingType.getTypeList();
            model.addAttribute(FURNISHING_TYPES, types);
            return LANDLORD_PROPERTY_FORM;
        }

        User landlord = userService.findByEmail(authentication.getName());
        propertyService.createProperty(request, landlord, imageFiles);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Property listing created successfully and saved as draft.");
        return REDIRECT_LANDLORD_NEW_LISTING;
    }

    @GetMapping()
    public String getAllProperties(
            @ModelAttribute LandlordPropertyFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            Authentication auth
    ) {
        User landlord = userService.findByEmail(auth.getName());
        Pageable pageable = PageRequest.of(page, size);

        Page<PropertyListItemDTO> properties = propertyService.searchPropertiesForLandlord(filter, landlord.getId(), pageable);
        PropertyStatsDTO stats = propertyService.getPropertyStats(landlord.getId(), filter);

        model.addAttribute("properties", properties);
        model.addAttribute("stats", stats);
        model.addAttribute("filter", filter);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("amenities", amenityService.findAll());
        return LANDLORD_PROPERTIES;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        PropertyDetailDTO property = propertyService.getPropertyDetailById(id);
        model.addAttribute("property", property);
        model.addAttribute("categories", categoryService.findAll());
        List<Map<String, String>> types = FurnishingType.getTypeList();
        model.addAttribute(FURNISHING_TYPES, types);
        model.addAttribute("oldImageUrls", property.getImageUrls());
        model.addAttribute("amenities", amenityService.findAll());
        model.addAttribute("selectedAmenityIds", property.getAmenities().stream().map(AmenityDTO::getId)
                .toList());
        model.addAttribute("actionUrl", "/landlord/properties/edit/" + id);
        return LANDLORD_PROPERTY_FORM;
    }

    @PostMapping("/edit/{id}")
    public String updateProperty(
            @PathVariable Long id,
            @ModelAttribute UpdatePropertyRequestDTO updateDto,
            @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
            RedirectAttributes redirectAttributes
    ) {
        propertyService.updateProperty(id, updateDto, imageFiles);
        redirectAttributes.addFlashAttribute("success", "Property updated successfully!");
        return REDIRECT_LANDLORD_PROPERTIES;
    }

    @PostMapping("/delete/{id}")
    public String deleteProperty(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        propertyService.deleteProperty(id);
        redirectAttributes.addFlashAttribute("successMessage", "Property delete successfully!");
        return REDIRECT_LANDLORD_PROPERTIES;
    }
}
