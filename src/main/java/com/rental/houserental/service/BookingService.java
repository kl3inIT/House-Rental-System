package com.rental.houserental.service;

import com.rental.houserental.dto.request.booking.BookingSearchRequestDTO;
import com.rental.houserental.dto.request.property.PropertyBookingRequestDTO;
import com.rental.houserental.dto.response.booking.BookingHistoryDTO;
import com.rental.houserental.dto.response.booking.BookingHistoryDetailDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface BookingService {

    List<BookingHistoryDTO> getBookingHistory();

    Page<BookingHistoryDTO> getBookingHistoryByLandlord(BookingSearchRequestDTO bookingSearchRequestDTO);

    BookingHistoryDetailDTO getBookingHistoryDetail(Long bookingId);

    void createBooking(PropertyBookingRequestDTO propertyBookingRequestDTO);

    void cancelBooking(Long bookingId);

    boolean isRefundable(Long bookingId);

    Map<String, Long> getBookingStatsByLandLord();

    Long countBookingsThisMonthByLandlord(Long landlordId);

    Long countActiveBookingsByLandlord(Long landlordId);

    List<BookingHistoryDTO> getCurrentRentedProperties();

    List<BookingHistoryDTO> getRentHistoryProperties();
}
