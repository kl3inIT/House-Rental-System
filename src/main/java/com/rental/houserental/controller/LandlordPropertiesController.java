package com.rental.houserental.controller;

import com.rental.houserental.dto.request.property.CreatePropertyRequestDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.FurnishingType;
import com.rental.houserental.service.CategoryService;
import com.rental.houserental.service.PropertyService;
import com.rental.houserental.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/new")
    public String newPropertyForm(Model model) {
        model.addAttribute(PROPERTY_REQUEST, new CreatePropertyRequestDTO());
        model.addAttribute(CATEGORIES, categoryService.findAll());
        List<Map<String, String>> types = FurnishingType.getTypeList();
        model.addAttribute(FURNISHING_TYPES, types);
        return LANDLORD_NEW_LISTING;
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
            return LANDLORD_NEW_LISTING;
        }

        User landlord = userService.findByEmail(authentication.getName());

        propertyService.createProperty(request, landlord, imageFiles);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Property listing created successfully and saved as draft.");
        return REDIRECT_LANDLORD_NEW_LISTING;
    }

    @GetMapping()
    public String allProperties(Model model) {
        model.addAttribute(PROPERTY_REQUEST, new CreatePropertyRequestDTO());
        model.addAttribute(CATEGORIES, categoryService.findAll());
        List<Map<String, String>> types = FurnishingType.getTypeList();
        model.addAttribute(FURNISHING_TYPES, types);
        return LANDLORD_PROPERTIES;
    }
}
