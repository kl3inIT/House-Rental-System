package com.rental.houserental.controller;

import com.rental.houserental.dto.request.listing.CreateListingRequestDTO;
import com.rental.houserental.dto.request.listing.ListingFilterDTO;
import com.rental.houserental.dto.response.listing.ListingListItemDTO;
import com.rental.houserental.dto.response.listing.ListingPriceDTO;
import com.rental.houserental.dto.response.listing.ListingStatsDTO;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.entity.User;
import com.rental.houserental.service.ListingService;
import com.rental.houserental.service.PropertyService;
import com.rental.houserental.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.rental.houserental.constant.AtrributeNameConstant.*;
import static com.rental.houserental.constant.ViewNamesConstant.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/landlord/listings")
@PreAuthorize("hasRole('LANDLORD')")
public class ListingController {

    private final ListingService listingService;

    private final UserService userService;

    private final PropertyService propertyService;

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpServletRequest request, Authentication authentication) {
        User landlord = userService.findByEmail(authentication.getName());
        model.addAttribute("currentUri", request.getRequestURI());
        CreateListingRequestDTO dto = CreateListingRequestDTO.builder().duration(1).build();
        model.addAttribute("listing", dto);
        List<RentalProperty> properties = propertyService.getPropertiesByLandlordStatusNotAvailable(landlord.getId());
        model.addAttribute(PROPERTIES, properties);
        model.addAttribute("listingPrices", listingService.getListingPriceDTO());
        return LANDLORD_LISTINGS_CREATE;
    }

    @PostMapping("/create")
    public String createListing(@Valid @ModelAttribute("listing") CreateListingRequestDTO request,
                                BindingResult bindingResult,
                                Authentication authentication,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        User landlord = userService.findByEmail(authentication.getName());
        ListingPriceDTO priceDTO = listingService.getListingPriceDTO();

        if (bindingResult.hasErrors()) {
            return loadCreateListingPage(model, request, landlord, priceDTO);
        }
        listingService.createListingWithPayment(request, landlord, priceDTO);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Listing created successfully!");
        return REDIRECT_LANDLORD_LISTINGS;
    }

    private String loadCreateListingPage(Model model, CreateListingRequestDTO request, User landlord,
                                         ListingPriceDTO listingPriceDTO) {
        List<RentalProperty> properties = propertyService.getPropertiesByLandlordStatusNotAvailable(landlord.getId());
        model.addAttribute(LISTING, request);
        model.addAttribute(PROPERTIES, properties);
        model.addAttribute(ERROR,  "Please correct the errors in the form.");
        model.addAttribute("listingPrices", listingPriceDTO);
        model.addAttribute("startDate", request.getStartDate());
        return LANDLORD_LISTINGS_CREATE;
    }

    @GetMapping()
    public String getAllListings(@ModelAttribute ListingFilterDTO filter,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "6") int size,
                                 Model model,
                                 Authentication authentication) {
        User landlord = userService.findByEmail(authentication.getName());
        Pageable pageable = PageRequest.of(page, size);
        Page<ListingListItemDTO> listings = listingService.getListingsForLandlord(filter, landlord.getId(), pageable);
        ListingStatsDTO listingStats = listingService.getListingStats(landlord.getId(), filter);
        model.addAttribute(LISTINGS, listings);
        model.addAttribute("listingStats", listingStats);
        model.addAttribute("filter", filter);
        model.addAttribute(PROPERTIES, List.of());
        return LANDLORD_LISTINGS;
    }

    @PostMapping("/delete/{id}")
    public String deleteListing(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        User landlord = userService.findByEmail(authentication.getName());
        listingService.deleteListing(id, landlord);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Listing deleted successfully");
        return REDIRECT_LANDLORD_LISTINGS;
    }

    @PostMapping("/hide/{id}")
    public String hideListing(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        User landlord = userService.findByEmail(authentication.getName());
        listingService.hideListing(id, landlord);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Listing hidden successfully");
        return REDIRECT_LANDLORD_LISTINGS;
    }

    @PostMapping("/unhide/{id}")
    public String unhideListing(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        User landlord = userService.findByEmail(authentication.getName());
        listingService.unhideListing(id, landlord);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Listing unhidden successfully");
        return REDIRECT_LANDLORD_LISTINGS;
    }
}

