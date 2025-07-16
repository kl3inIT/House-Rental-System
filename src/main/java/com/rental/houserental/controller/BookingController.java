package com.rental.houserental.controller;

import com.rental.houserental.dto.request.property.PropertyBookingRequestDTO;
import com.rental.houserental.dto.response.booking.BookingHistoryDTO;
import com.rental.houserental.dto.response.booking.BookingHistoryDetailDTO;
import com.rental.houserental.service.BookingService;
import com.rental.houserental.service.PropertyService;
import com.rental.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static com.rental.houserental.constant.ViewNamesConstant.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final PropertyService propertyService;
    private final UserService userService;
    private final BookingService bookingService;

    @GetMapping("/booking/{propertyId}")
    public String checkout(@PathVariable Long propertyId, Model model) {
        PropertyBookingRequestDTO propertyBooking =  new PropertyBookingRequestDTO();
        propertyBooking.setId(propertyId);
        model.addAttribute("propertyCheckout", propertyService.getPropertyToCheckoutById(propertyId));
        model.addAttribute("propertyBooking",propertyBooking);
        return CHECKOUT;
    }

    @PostMapping("/booking/{propertyId}")
    public String bookProperty(@PathVariable Long propertyId, PropertyBookingRequestDTO propertyBookingRequestDTO, Model model) {
        double userBalance = userService.getCurrentUser().getBalance();
        if (userBalance < propertyBookingRequestDTO.getDepositAmount()) {
            model.addAttribute("error", "Insufficient balance to book this property.");
            model.addAttribute("propertyBooking", propertyBookingRequestDTO);
            model.addAttribute("propertyCheckout", propertyService.getPropertyToCheckoutById(propertyId));
            return CHECKOUT;
        }
        bookingService.createBooking(propertyBookingRequestDTO);
        return REDIRECT_MY_BOOKING;
    }

    @GetMapping("/my-bookings")
    public String myBookings(Model model) {
        List<BookingHistoryDTO> bookingHistory = bookingService.getBookingHistory();
        model.addAttribute("bookings", bookingHistory);
        return MY_BOOKING;
    }

    @GetMapping("/my-bookings/{id}")
    public String bookingDetail(@PathVariable Long id, Model model) {
        BookingHistoryDetailDTO bookingDetail = bookingService.getBookingHistoryDetail(id);
        model.addAttribute("isRefundable", bookingService.isRefundable(id));
        model.addAttribute("bookingDetail", bookingDetail);
        return BOOKING_DETAIL;
    }

    @PostMapping("/my-bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return REDIRECT_MY_BOOKING;
    }




}
