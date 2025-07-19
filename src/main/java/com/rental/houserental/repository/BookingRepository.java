package com.rental.houserental.repository;

import com.rental.houserental.entity.Booking;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.entity.Transaction;
import com.rental.houserental.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

        List<Booking> findByUserId(Long userId);

        @Query("SELECT t FROM Booking t " +
                        "JOIN t.rentalProperty p " +
                        "WHERE (:status IS NULL OR t.status = :status) AND " +
                        "(:propertyTitle IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :propertyTitle, '%'))) AND " +
                        "(:dateFrom IS NULL OR t.createdAt >= :dateFrom) AND " +
                        "(:dateTo IS NULL OR t.createdAt <= :dateTo) AND " +
                        "(:landlordId IS NULL OR t.rentalProperty.landlord.id = :landlordId) " +
                        "ORDER BY t.createdAt DESC")
        Page<Booking> findAllBySearchRequest(
                        @Param("status") BookingStatus status,
                        @Param("dateFrom") LocalDateTime dateFrom,
                        @Param("dateTo") LocalDateTime dateTo,
                        @Param("propertyTitle") String propertyTitle,
                        @Param("landlordId") Long landlordId,
                        Pageable pageable);

        List<Booking> findByRentalProperty_Landlord_Id(Long id);

        @Query("SELECT COUNT(b) FROM Booking b WHERE b.rentalProperty.landlord.id = :landlordId AND MONTH(b.createdAt) = MONTH(CURRENT_DATE) AND YEAR(b.createdAt) = YEAR(CURRENT_DATE)")
        Long countBookingsThisMonthByLandlord(@Param("landlordId") Long landlordId);

        @Query("SELECT COUNT(b) FROM Booking b WHERE b.rentalProperty.landlord.id = :landlordId AND b.status = :status")
        Long countActiveBookingsByLandlord(@Param("landlordId") Long landlordId,
                        @Param("status") BookingStatus status);

        @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = :status")
        List<Booking> findByUserIdAndStatus(@Param("userId") Long userId,
                        @Param("status") BookingStatus status);

        @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = :status")
        List<Booking> findByUserIdAndStatusIsCompleted(Long userId, BookingStatus status);

        List<Booking> findByRentalPropertyId(Long propertyId);

        @Query("SELECT b FROM Booking b WHERE b.rentalProperty.landlord.id = :landlordId ORDER BY b.createdAt DESC")
        List<Booking> findRecentByLandlordId(@Param("landlordId") Long landlordId,
                        Pageable pageable);
}
