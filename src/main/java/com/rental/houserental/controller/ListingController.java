package com.rental.houserental.controller;

import com.rental.houserental.dto.request.listing.CreateListingRequestDTO;
import com.rental.houserental.dto.request.listing.ListingFilterDTO;
import com.rental.houserental.dto.request.listing.UpdateListingRequestDTO;
import com.rental.houserental.dto.response.listing.ListingDetailDTO;
import com.rental.houserental.dto.response.listing.ListingListItemDTO;
import com.rental.houserental.dto.response.listing.ListingPriceDTO;
import com.rental.houserental.dto.response.listing.ListingStatsDTO;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.entity.User;
import com.rental.houserental.service.ListingService;
import com.rental.houserental.service.PropertyService;
import com.rental.houserental.service.SystemConfigService;
import com.rental.houserental.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    private final SystemConfigService systemConfigService;

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpServletRequest request, Authentication authentication) {
        User landlord = userService.findByEmail(authentication.getName());
        
        model.addAttribute("currentUri", request.getRequestURI());
        CreateListingRequestDTO dto = new CreateListingRequestDTO();
        dto.setDuration(1);
        model.addAttribute("listing", dto);

        List<RentalProperty> properties = propertyService.getPropertiesByLandlordStatusNotAvailable(landlord.getId());
        ListingPriceDTO priceDTO = ListingPriceDTO.builder()
                .normalPricePerMonth(systemConfigService.getNormalListingPricePerMonth())
                .highlightPricePerMonth(systemConfigService.getHighlightListingPricePerMonth())
                .normalPricePerWeek(systemConfigService.getNormalListingPricePerWeek())
                .highlightPricePerWeek(systemConfigService.getHighlightListingPricePerWeek())
                .build();
        
        model.addAttribute(PROPERTIES, properties);
        model.addAttribute("listingPrices", priceDTO);
        return LANDLORD_LISTINGS_CREATE;
    }

    @PostMapping("/create")
    public String createListing(@Valid @ModelAttribute("listing") CreateListingRequestDTO request,
                                BindingResult bindingResult,
                                Authentication authentication,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        User landlord = userService.findByEmail(authentication.getName());
        ListingPriceDTO priceDTO = ListingPriceDTO.builder()
                .normalPricePerMonth(systemConfigService.getNormalListingPricePerMonth())
                .highlightPricePerMonth(systemConfigService.getHighlightListingPricePerMonth())
                .normalPricePerWeek(systemConfigService.getNormalListingPricePerWeek())
                .highlightPricePerWeek(systemConfigService.getHighlightListingPricePerWeek())
                .build();

        if (bindingResult.hasErrors()) {
            return loadCreateListingPage(model, request, landlord, priceDTO, null);
        }

        double totalPrice = listingService.calculateListingFee(
                Boolean.TRUE.equals(request.getIsHighlight()),
                request.getDurationType(),
                request.getDuration(),
                priceDTO
        );

        if (landlord.getBalance() < totalPrice) {
            return loadCreateListingPage(model, request, landlord, priceDTO,
                    "You do not have enough balance to create this listing. Please top up your wallet to continue.");
        }

        try {
            listingService.processPayment(landlord, totalPrice);
            listingService.createListing(request, landlord);

            redirectAttributes.addFlashAttribute("successMessage", "Listing created successfully!");
            return REDIRECT_LANDLORD_LISTINGS;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR, "Failed to create listing: " + e.getMessage());
            return REDIRECT_LANLORD_LISTINGS_CREATE;
        }
    }

    private String loadCreateListingPage(Model model, CreateListingRequestDTO request, User landlord,
                                         ListingPriceDTO listingPriceDTO, String errorMsg) {
        List<RentalProperty> properties = propertyService.getPropertiesByLandlordStatusNotAvailable(landlord.getId());
        model.addAttribute(LISTING, request);
        model.addAttribute(PROPERTIES, properties);
        model.addAttribute(ERROR, errorMsg);
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

        try {
            Page<ListingListItemDTO> listings = listingService.getListingsForLandlord(filter, landlord.getId(), pageable);
            ListingStatsDTO listingStats = listingService.getListingStats(landlord.getId(), filter);

            model.addAttribute(LISTINGS, listings);
            model.addAttribute("listingStats", listingStats);
            model.addAttribute("filter", filter);
            
            return "landlord/listings";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to load listings: " + e.getMessage());
            model.addAttribute(LISTINGS, Page.empty(pageable));
            model.addAttribute("listingStats", ListingStatsDTO.builder()
                    .totalListings(0L)
                    .activeListings(0L)
                    .expiredListings(0L)
                    .upcomingListings(0L)
                    .highlightedListings(0L)
                    .totalViews(0L)
                    .totalInquiries(0L)
                    .totalRevenue(0.0)
                    .build());
            model.addAttribute("filter", filter);
            model.addAttribute(PROPERTIES, List.of());
            
            return "landlord/listings";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            User landlord = userService.findByEmail(authentication.getName());

            ListingDetailDTO listing = listingService.getListingDetailById(id, landlord);

            List<RentalProperty> properties = propertyService.getPropertiesByLandlordStatusNotAvailable(landlord.getId());

            UpdateListingRequestDTO updateRequest = UpdateListingRequestDTO.builder()
                    .startDate(listing.getStartDate())
                    .endDate(listing.getEndDate())
                    .amount(listing.getAmount())
                    .description(listing.getDescription())
                    .isHighlight(listing.getIsHighlight())
                    .build();
            
            model.addAttribute(LISTINGS, updateRequest);
            model.addAttribute(PROPERTIES, properties);
            model.addAttribute("actionUrl", "/landlord/listings/edit/" + id);
            model.addAttribute("listingId", id);
            
            return "landlord/edit-listing";
        } catch (Exception e) {
            return "redirect:/landlord/listings";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateListing(@PathVariable Long id,
                               @Valid @ModelAttribute("listing") UpdateListingRequestDTO request,
                               BindingResult bindingResult,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            User landlord = userService.findByEmail(authentication.getName());
            List<RentalProperty> properties = propertyService.getPropertiesByLandlordStatusNotAvailable(landlord.getId());
            model.addAttribute(LISTINGS, request);
            model.addAttribute(PROPERTIES, properties);
            model.addAttribute("actionUrl", "/landlord/listings/edit/" + id);
            model.addAttribute("listingId", id);
            return "landlord/edit-listing";
        }

        try {
            User landlord = userService.findByEmail(authentication.getName());
            listingService.updateListing(id, request, landlord);
            redirectAttributes.addFlashAttribute("successMessage", "Listing updated successfully!");
            return "redirect:/landlord/listings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update listing: " + e.getMessage());
            return "redirect:/landlord/listings/edit/" + id;
        }
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteListing(@PathVariable Long id, Authentication authentication) {
        try {
            User landlord = userService.findByEmail(authentication.getName());
            listingService.deleteListing(id, landlord);
            return ResponseEntity.ok("Listing deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete listing: " + e.getMessage());
        }
    }

    @GetMapping("/property/{propertyId}")
    @ResponseBody
    public ResponseEntity<List<ListingListItemDTO>> getListingsForProperty(@PathVariable Long propertyId, 
                                                                          Authentication authentication) {
        try {
            User landlord = userService.findByEmail(authentication.getName());
            List<ListingListItemDTO> listings = listingService.getListingsForProperty(propertyId, landlord);
            return ResponseEntity.ok(listings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
