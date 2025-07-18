package com.rental.houserental.controller;

import com.rental.houserental.dto.request.booking.BookingSearchRequestDTO;
import com.rental.houserental.dto.response.booking.BookingHistoryDTO;
import com.rental.houserental.dto.response.booking.BookingHistoryDetailDTO;
import com.rental.houserental.enums.BookingStatus;
import com.rental.houserental.service.BookingService;
import com.rental.houserental.service.PropertyService;
import com.rental.houserental.service.TransactionService;
import com.rental.houserental.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.userdetails.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rental.houserental.constant.ViewNamesConstant.BOOKING_DETAIL;
import static com.rental.houserental.constant.ViewNamesConstant.LANDLORD_DASHBOARD;

@Controller
@RequestMapping("/landlord")
@PreAuthorize("hasRole('LANDLORD')")
@RequiredArgsConstructor
@Slf4j
public class LandlordDashboardController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());

        // Lấy landlordId từ user hiện tại
        Long landlordId = userService.getCurrentUser().getId();

        // Lấy tổng số property
        Long totalProperties = propertyService.countTotalRentalProperty();
        // Lấy số booking trong tháng
        Long bookingsThisMonth = bookingService.countBookingsThisMonthByLandlord(landlordId);
        // Lấy tổng doanh thu
        Long monthlyRevenue = propertyService.countRevenueRentalProperty();

        Long activeBookings = bookingService.countActiveBookingsByLandlord(landlordId);

        // Đưa vào dashboardStats
        Map<String, Object> dashboardStats = new HashMap<>();
        dashboardStats.put("totalProperties", totalProperties);
        dashboardStats.put("bookingsThisMonth", bookingsThisMonth);
        dashboardStats.put("activeBookings", activeBookings);
        dashboardStats.put("monthlyRevenue", monthlyRevenue);

        model.addAttribute("dashboardStats", dashboardStats);
        return LANDLORD_DASHBOARD;
    }

    @GetMapping("/bookings")
    public String bookings(
            @Valid @ModelAttribute("bookingSearchRequestDTO") BookingSearchRequestDTO bookingSearchRequestDTO,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request) {

        Page<BookingHistoryDTO> bookings;

        if (bindingResult.hasErrors()) {
            BookingSearchRequestDTO defaultRequest = new BookingSearchRequestDTO();
            bookings = bookingService.getBookingHistoryByLandlord(defaultRequest);
            model.addAttribute("error", bindingResult);
        } else {
            bookings = bookingService.getBookingHistoryByLandlord(bookingSearchRequestDTO);
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("types", BookingStatus.getAllTypes());
        model.addAttribute("bookingStats", bookingService.getBookingStatsByLandLord());
        model.addAttribute("bookingSearchRequestDTO", bookingSearchRequestDTO);
        model.addAttribute("currentUri", request.getRequestURI());
        return "landlord/bookings";

    }

    @GetMapping("/bookings/{id}")
    public String bookingDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        BookingHistoryDetailDTO bookingDetail = bookingService.getBookingHistoryDetail(id);
        model.addAttribute("isRefundable", bookingService.isRefundable(id));
        model.addAttribute("bookingDetail", bookingDetail);
        model.addAttribute("currentUri", request.getRequestURI());
        return "landlord/booking-detail";
    }

}