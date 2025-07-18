package com.rental.houserental.controller;

import com.rental.houserental.dto.response.property.PropertyDetailDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.service.PropertyService;
import com.rental.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static com.rental.houserental.constant.ViewNamesConstant.PROPERTY_DETAIL;

@Controller
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final UserService userService;

    @GetMapping("/{id}")
    public String viewPropertyDetail(@PathVariable Long id, Model model, Principal principal) {
        PropertyDetailDTO property = propertyService.getPropertyDetailById(id);
        Long currentUserId = null;
        if (principal != null) {
            User currentUser = userService.findByEmail(principal.getName());
            currentUserId = currentUser.getId();
        }
        if (currentUserId != null && !currentUserId.equals(property.getLandlordId())) {
            propertyService.increaseView(id);
        }
        model.addAttribute("property", property);
        model.addAttribute("similarProperties", propertyService.getSimularProperties(3, id));
        return PROPERTY_DETAIL;
    }
}
