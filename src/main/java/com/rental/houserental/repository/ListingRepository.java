package com.rental.houserental.repository;

import com.rental.houserental.entity.Listing;
import com.rental.houserental.enums.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findByRentalPropertyIdAndLandlordId(Long propertyId, Long landlordId);
    
    long countByLandlordId(Long landlordId);
    
    long countByLandlordIdAndStartDateLessThanEqualAndEndDateGreaterThan(Long landlordId, LocalDateTime startDate, LocalDateTime endDate);
    
    long countByLandlordIdAndEndDateLessThan(Long landlordId, LocalDateTime endDate);
    
    long countByLandlordIdAndStartDateGreaterThan(Long landlordId, LocalDateTime startDate);
    
    long countByLandlordIdAndIsHighlightTrue(Long landlordId);

    List<Listing> findByLandlordIdAndEndDateBeforeAndStatusNot(Long landlordId, LocalDateTime now, ListingStatus status);

    @Query("""
        SELECT l FROM Listing l
        WHERE l.rentalProperty.landlord.id = :landlordId
        AND (
            :searchTerm IS NULL
            OR LOWER(l.rentalProperty.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(l.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        )
        AND (:status IS NULL OR l.status = :status)
        AND (:isHighlight IS NULL OR l.isHighlight = :isHighlight)
        AND (:propertyId IS NULL OR l.rentalProperty.id = :propertyId)
    """)
    Page<Listing> searchListingsForLandlord(
            @Param("landlordId") Long landlordId,
            @Param("searchTerm") String searchTerm,
            @Param("isHighlight") Boolean isHighlight,
            @Param("status") ListingStatus status,
            @Param("propertyId") Long propertyId,
            Pageable pageable
    );


} 