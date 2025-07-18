package com.rental.houserental.component;

import com.rental.houserental.entity.Booking;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.enums.BookingStatus;
import com.rental.houserental.enums.PropertyStatus;
import com.rental.houserental.repository.BookingRepository;
import com.rental.houserental.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
@RequiredArgsConstructor
public class BookingSchedule {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;

    @Scheduled(cron = "0 0/50 * * * ?")
    public void updateAllBookingStatus(){
        List<Booking> bookings = bookingRepository.findAll();
        for (Booking booking : bookings) {
            updateBookingStatus(booking);
        }
    }

    public void updateBookingStatus(Booking booking) {
        if (!booking.getStatus().isCancelled()) {
            if(booking.getStartDate().isBefore(LocalDateTime.now()) && booking.getEndDate().isAfter(LocalDateTime.now())) {
                booking.setStatus(BookingStatus.ACTIVE);
            } else {
                booking.setStatus(BookingStatus.COMPLETED);
                RentalProperty rentalProperty = propertyRepository.findById(booking.getRentalProperty().getId()).get();
                rentalProperty.setPropertyStatus(PropertyStatus.AVAILABLE);
                propertyRepository.save(rentalProperty);
            }
            bookingRepository.save(booking);
        }
    }
}
