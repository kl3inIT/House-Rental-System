package com.rental.houserental.service.impl;

import com.rental.houserental.dto.request.booking.BookingSearchRequestDTO;
import com.rental.houserental.dto.request.property.PropertyBookingRequestDTO;
import com.rental.houserental.dto.response.booking.BookingHistoryDTO;
import com.rental.houserental.dto.response.booking.BookingHistoryDetailDTO;
import com.rental.houserental.entity.Booking;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.entity.Transaction;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.BookingStatus;
import com.rental.houserental.enums.PropertyStatus;
import com.rental.houserental.enums.TransactionType;
import com.rental.houserental.exceptions.booking.InvalidBookingStatusException;
import com.rental.houserental.exceptions.property.PropertyNotFoundException;
import com.rental.houserental.exceptions.user.UserNotFoundException;
import com.rental.houserental.repository.BookingRepository;
import com.rental.houserental.repository.PropertyRepository;
import com.rental.houserental.repository.TransactionRepository;
import com.rental.houserental.repository.UserRepository;
import com.rental.houserental.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.rental.houserental.constant.ErrorMessageConstant.MSG_400;


@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final TransactionRepository transactionRepository;
    @Override
    public List<BookingHistoryDTO> getBookingHistory() {
        List<Booking> bookings = bookingRepository.findByUserId(getCurrentUser().getId());
        return bookings.stream().map(booking -> {
            BookingHistoryDTO bookingHistoryDTO = new BookingHistoryDTO();
            bookingHistoryDTO.setBookingId(booking.getId());
            bookingHistoryDTO.setPropertyId(booking.getRentalProperty().getId());
            bookingHistoryDTO.setPropertyName(booking.getRentalProperty().getTitle());
            bookingHistoryDTO.setBookingDate(booking.getCreatedAt().toLocalDate().toString());
            bookingHistoryDTO.setPropertyImage(booking.getRentalProperty().getMainImageUrl());
            bookingHistoryDTO.setTotalAmount(booking.getAmount());
            bookingHistoryDTO.setPropertyAddress(booking.getRentalProperty().getFullAddress());
            bookingHistoryDTO.setBookingStatus(booking.getStatus().toString());
            return bookingHistoryDTO;
        }).toList();
    }

    @Override
    public Page<BookingHistoryDTO> getBookingHistoryByLandlord(BookingSearchRequestDTO bookingSearchRequestDTO) {
        Pageable pageable = PageRequest.of(
                bookingSearchRequestDTO.getPage(),
                bookingSearchRequestDTO.getSize()
        );

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (bookingSearchRequestDTO.getStartDate() != null) {
            startDateTime = bookingSearchRequestDTO.getStartDate().atStartOfDay();
        }

        if (bookingSearchRequestDTO.getEndDate() != null) {
            endDateTime = bookingSearchRequestDTO.getEndDate().atTime(23, 59, 59, 999_999_999);
        }

        String propertyTitle = bookingSearchRequestDTO.getPropertyTitle();
        if (propertyTitle != null && propertyTitle.isBlank()) {
            propertyTitle = null;
        }

        Page<Booking> bookings = bookingRepository.findAllBySearchRequest(
                BookingStatus.fromString(bookingSearchRequestDTO.getStatus()),
                startDateTime,
                endDateTime,
                propertyTitle,
                getCurrentUser().getId(),
                pageable
        );

        return bookings.map(this::bookingHistoryDTO);
    }

    public BookingHistoryDTO bookingHistoryDTO(Booking booking){
        return BookingHistoryDTO.builder()
                .bookingId(booking.getId())
                .propertyId(booking.getRentalProperty().getId())
                .propertyName(booking.getRentalProperty().getTitle())
                .bookingDate(booking.getCreatedAt().toLocalDate().toString())
                .propertyImage(booking.getRentalProperty().getMainImageUrl())
                .totalAmount(booking.getAmount())
                .propertyAddress(booking.getRentalProperty().getFullAddress())
                .bookingStatus(booking.getStatus().toString())
                .build();
    }

    @Override
    public BookingHistoryDetailDTO getBookingHistoryDetail(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new PropertyNotFoundException("Booking not found with ID: " + bookingId));
        BookingHistoryDetailDTO bookingDetail = new BookingHistoryDetailDTO();
        bookingDetail.setBookingId(booking.getId());
        bookingDetail.setPropertyId(booking.getRentalProperty().getId());
        bookingDetail.setPropertyName(booking.getRentalProperty().getTitle());
        bookingDetail.setPropertyAddress(booking.getRentalProperty().getFullAddress());
        bookingDetail.setStartDate(booking.getStartDate().toLocalDate().toString());
        bookingDetail.setEndDate(booking.getEndDate().toLocalDate().toString());
        bookingDetail.setTotalAmount(booking.getAmount());
        bookingDetail.setDepositAmount(booking.getDeposit());
        bookingDetail.setBookingStatus(booking.getStatus().toString());
        bookingDetail.setNote(booking.getNote());
        bookingDetail.setPropertyImage(booking.getRentalProperty().getMainImageUrl());
        bookingDetail.setLandlordName(booking.getRentalProperty().getLandlord().getName());
        bookingDetail.setLandlordEmail(booking.getRentalProperty().getLandlord().getEmail());
        bookingDetail.setLandlordPhone(booking.getRentalProperty().getLandlord().getPhone());
        bookingDetail.setRenterName(booking.getUser().getName());
        bookingDetail.setRenterEmail(booking.getUser().getEmail());
        bookingDetail.setRenterPhone(booking.getUser().getPhone());
        bookingDetail.setBookingDate(booking.getCreatedAt());
        bookingDetail.setPropertyPrice(booking.getRentalProperty().getMonthlyRent().toBigInteger().doubleValue());
        bookingDetail.setDurationInMonths(booking.getEndDate().getMonthValue() - booking.getStartDate().getMonthValue());
        return bookingDetail;
    }

    @Override
    public void createBooking(PropertyBookingRequestDTO propertyBookingRequestDTO) {
        RentalProperty property = findPropertyById(propertyBookingRequestDTO.getId());
        if(!property.getPropertyStatus().equals(PropertyStatus.AVAILABLE)) {
            throw new InvalidBookingStatusException("Booking status not available");
        }

        Booking newBooking = new Booking();
        LocalDate startLocalDate = LocalDate.parse(propertyBookingRequestDTO.getStartDate());
        LocalDateTime startDate = startLocalDate.atStartOfDay();
        LocalDateTime endDate = startDate.plusMonths(propertyBookingRequestDTO.getDurationInMonths());

        newBooking.setRentalProperty(property);
        newBooking.setUser(getCurrentUser());
        newBooking.setStartDate(startDate);
        newBooking.setEndDate(endDate);
        newBooking.setDeposit(propertyBookingRequestDTO.getDepositAmount());
        newBooking.setAmount(propertyBookingRequestDTO.getTotalAmount());
        newBooking.setNote(propertyBookingRequestDTO.getNote());
        newBooking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(newBooking);

        property.setPropertyStatus(PropertyStatus.BOOKED);
        propertyRepository.save(property);

        User currentUser = getCurrentUser();
        User landlord = property.getLandlord();
        currentUser.setBalance(currentUser.getBalance() - propertyBookingRequestDTO.getDepositAmount());
        landlord.setBalance(landlord.getBalance() + propertyBookingRequestDTO.getDepositAmount());
        userRepository.save(getCurrentUser());
        userRepository.save(landlord);

        Transaction userTransaction = new Transaction();
        userTransaction.setUser(getCurrentUser());
        userTransaction.setAmount(propertyBookingRequestDTO.getDepositAmount());
        userTransaction.setType(TransactionType.PAYMENT);
        userTransaction.setDescription("Booking deposit for property ID: " + property.getId());
        userTransaction.setBalanceAfter(currentUser.getBalance());
        transactionRepository.save(userTransaction);

        Transaction landlordTransaction = new Transaction();
        landlordTransaction.setUser(landlord);
        landlordTransaction.setAmount(propertyBookingRequestDTO.getDepositAmount());
        landlordTransaction.setType(TransactionType.RECEIVE_PAYMENT);
        landlordTransaction.setDescription("Booking received for property ID: " + property.getId());
        landlordTransaction.setBalanceAfter(landlord.getBalance());
        transactionRepository.save(landlordTransaction);

    }

    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new PropertyNotFoundException("Booking not found with ID: " + bookingId));

        if(isRefundable(bookingId)) {
            booking.setStatus(BookingStatus.CANCELED);
            bookingRepository.save(booking);
            User currentUser = getCurrentUser();
            User landlord = booking.getRentalProperty().getLandlord();
            currentUser.setBalance(currentUser.getBalance() + booking.getDeposit());
            landlord.setBalance(landlord.getBalance() - booking.getDeposit());
            userRepository.save(getCurrentUser());
            userRepository.save(landlord);

            Transaction userTransaction = new Transaction();
            userTransaction.setUser(getCurrentUser());
            userTransaction.setAmount(booking.getDeposit());
            userTransaction.setType(TransactionType.RECEIVE_REFUND);
            userTransaction.setDescription("Received refund");
            userTransaction.setBalanceAfter(currentUser.getBalance());
            transactionRepository.save(userTransaction);

            Transaction landlordTransaction = new Transaction();
            landlordTransaction.setUser(landlord);
            landlordTransaction.setAmount(booking.getDeposit());
            landlordTransaction.setType(TransactionType.REFUND);
            landlordTransaction.setDescription("Refund for booking");
            landlordTransaction.setBalanceAfter(landlord.getBalance());
            transactionRepository.save(landlordTransaction);


        }
    }

    @Override
    public boolean isRefundable(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new PropertyNotFoundException("Booking not found with ID: " + bookingId));
        return LocalDateTime.now().isBefore(booking.getCreatedAt().plusHours(24)) && booking.getStatus() == BookingStatus.CONFIRMED;
    }

    public RentalProperty findPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + id));
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found", MSG_400));
    }


}
