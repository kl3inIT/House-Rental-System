package com.rental.houserental.repository;

import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.PropertyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository
                extends JpaRepository<RentalProperty, Long>, JpaSpecificationExecutor<RentalProperty> {

        @Query("""
                        SELECT p FROM RentalProperty p
                         WHERE p.propertyStatus IN :statuses
                        AND EXISTS (SELECT 1 FROM PropertyImage pi WHERE pi.rentalProperty = p)
                        ORDER BY p.createdAt DESC
                        """)
        List<RentalProperty> findFeaturedProperties(@Param("statuses") List<PropertyStatus> statuses,
                        Pageable pageable);

        List<RentalProperty> findByLandlordIdAndPropertyStatusNot(Long landlordId, PropertyStatus propertyStatus);

        List<RentalProperty> findByLandlordIdAndPropertyStatus(Long landlordId, PropertyStatus propertyStatus);

        @Query("SELECT p FROM RentalProperty p " +
                        "WHERE p.landlord.id = :landlordId " +
                        "AND (:status IS NULL OR p.propertyStatus = :status) " +
                        "AND (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                        "AND (:minPrice IS NULL OR p.monthlyRent >= :minPrice) " +
                        "AND (:maxPrice IS NULL OR p.monthlyRent <= :maxPrice)")
        Page<RentalProperty> searchByLandlord(
                        @Param("landlordId") Long landlordId,
                        @Param("status") PropertyStatus status,
                        @Param("keyword") String keyword,
                        @Param("minPrice") Integer minPrice,
                        @Param("maxPrice") Integer maxPrice,
                        Pageable pageable);

        @Query("SELECT " +
                        "COUNT(p), " + // 0: Tổng số bất động sản
                        "SUM(CASE WHEN p.propertyStatus = 'AVAILABLE' THEN 1 ELSE 0 END), " + // 1
                        "SUM(CASE WHEN p.propertyStatus = 'RENTED' THEN 1 ELSE 0 END), " + // 2
                        "SUM(CASE WHEN p.propertyStatus = 'BOOKED' THEN 1 ELSE 0 END), " + // 3
                        "SUM(CASE WHEN p.propertyStatus = 'DRAFT' THEN 1 ELSE 0 END), " + // 4
                        "SUM(CASE WHEN p.propertyStatus = 'UNAVAILABLE' THEN 1 ELSE 0 END), " + // 5
                        "SUM(CASE WHEN p.propertyStatus = 'EXPIRED' THEN 1 ELSE 0 END), " + // 6
                        "SUM(CASE WHEN p.propertyStatus = 'ADMIN_HIDDEN' THEN 1 ELSE 0 END), " + // 7
                        "SUM(CASE WHEN p.propertyStatus = 'ADMIN_BANNED' THEN 1 ELSE 0 END), " + // 8
                        "COALESCE(SUM(p.views), 0), " + // 9
                        "COALESCE(SUM(CASE WHEN p.propertyStatus IN ('RENTED') THEN p.monthlyRent ELSE 0 END), 0) " + // 10
                        "FROM RentalProperty p " +
                        "WHERE p.landlord.id = :landlordId " +
                        "AND (:status IS NULL OR p.propertyStatus = :status) " +
                        "AND (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                        "AND (:minPrice IS NULL OR p.monthlyRent >= :minPrice) " +
                        "AND (:maxPrice IS NULL OR p.monthlyRent <= :maxPrice)")
        List<Object[]> getStatsByLandlord(
                        @Param("landlordId") Long landlordId,
                        @Param("status") PropertyStatus status,
                        @Param("keyword") String keyword,
                        @Param("minPrice") Integer minPrice,
                        @Param("maxPrice") Integer maxPrice);

        List<RentalProperty> findByCategoryIdAndIdNotAndPropertyStatus(
                        Long categoryId,
                        Long propertyId,
                        PropertyStatus propertyStatus,
                        Pageable pageable);

        List<RentalProperty> findByLandlordId(Long landlord);

        Long countByLandlordIdAndPropertyStatus(Long landlordId, PropertyStatus propertyStatus);

        @Query("SELECT SUM(p.monthlyRent) FROM RentalProperty p WHERE p.propertyStatus = 'RENTED'")
        Long countRentedProperties();

        @Query("SELECT DISTINCT p FROM RentalProperty p LEFT JOIN FETCH p.bookings WHERE p.landlord.id = :landlordId")
        List<RentalProperty> findByLandlordIdWithBookings(@Param("landlordId") Long landlordId);
}
