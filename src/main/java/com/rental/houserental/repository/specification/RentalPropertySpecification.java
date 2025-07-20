package com.rental.houserental.repository.specification;

import com.rental.houserental.dto.request.property.SearchPropertyCriteriaDTO;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.enums.PropertyStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RentalPropertySpecification {

    private static final Logger log = LoggerFactory.getLogger(RentalPropertySpecification.class);

    private RentalPropertySpecification() {
        // Private constructor to prevent instantiation
    }

    public static Specification<RentalProperty> withCriteria(SearchPropertyCriteriaDTO criteria) {
        return (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            // Location search (province, ward, streetAddress)
            if (criteria.getLocation() != null && !criteria.getLocation().trim().isEmpty()) {
                String locationPattern = "%" + criteria.getLocation().trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("province")), locationPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("ward")), locationPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("streetAddress")), locationPattern)
                ));
            }

            // Province filter
            if (criteria.getProvince() != null && !criteria.getProvince().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("province"), criteria.getProvince().trim()));
            }

            // Ward filter
            if (criteria.getWard() != null && !criteria.getWard().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("ward"), criteria.getWard().trim()));
            }

            // Property Type (Category) filter
            if (criteria.getPropertyTypes() != null && !criteria.getPropertyTypes().isEmpty()) {
                log.info("Filtering by property types: {}", criteria.getPropertyTypes());
                predicates.add(root.get("category").get("id").in(criteria.getPropertyTypes()));
            }

            // Price Range filters
            if (criteria.getPriceRanges() != null && !criteria.getPriceRanges().isEmpty()) {
                log.info("Processing price ranges: {}", criteria.getPriceRanges());
                List<jakarta.persistence.criteria.Predicate> pricePredicates = new ArrayList<>();
                
                for (String range : criteria.getPriceRanges()) {
                    if (range != null && !range.trim().isEmpty()) {
                        String[] parts = range.split("-");
                        if (parts.length == 2) {
                            try {
                                BigDecimal minPrice = new BigDecimal(parts[0]);
                                BigDecimal maxPrice = new BigDecimal(parts[1]);
                                
                                log.info("Processing price range: {} - {} (min: {}, max: {})", parts[0], parts[1], minPrice, maxPrice);
                                
                                if (maxPrice.compareTo(BigDecimal.valueOf(999999999)) == 0) {
                                    // "Above X" case - greater than minPrice
                                    log.info("Creating 'Above {}' predicate for price > {}", minPrice, minPrice);
                                    pricePredicates.add(criteriaBuilder.greaterThan(
                                        root.get("monthlyRent"), minPrice));
                                } else {
                                    // Range case - between minPrice and maxPrice (inclusive)
                                    log.info("Creating range predicate: {} <= price <= {}", minPrice, maxPrice);
                                    pricePredicates.add(criteriaBuilder.and(
                                        criteriaBuilder.greaterThanOrEqualTo(root.get("monthlyRent"), minPrice),
                                        criteriaBuilder.lessThanOrEqualTo(root.get("monthlyRent"), maxPrice)
                                    ));
                                }
                            } catch (NumberFormatException e) {
                                log.warn("Invalid price range format: {}", range, e);
                            }
                        } else {
                            log.warn("Invalid price range format (expected min-max): {}", range);
                        }
                    }
                }
                
                if (!pricePredicates.isEmpty()) {
                    predicates.add(criteriaBuilder.or(pricePredicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
                    log.info("Added {} price predicates", pricePredicates.size());
                }
            }

            // Area Range filters
            if (criteria.getAreaRanges() != null && !criteria.getAreaRanges().isEmpty()) {
                log.info("Processing area ranges: {}", criteria.getAreaRanges());
                List<jakarta.persistence.criteria.Predicate> areaPredicates = new ArrayList<>();
                
                for (String range : criteria.getAreaRanges()) {
                    if (range != null && !range.trim().isEmpty()) {
                        String[] parts = range.split("-");
                        if (parts.length == 2) {
                            try {
                                BigDecimal minArea = new BigDecimal(parts[0]);
                                BigDecimal maxArea = new BigDecimal(parts[1]);
                                
                                log.info("Processing area range: {} - {} (min: {}, max: {})", parts[0], parts[1], minArea, maxArea);
                                
                                if (maxArea.compareTo(BigDecimal.valueOf(999999)) == 0) {
                                    // "Above X" case - greater than minArea
                                    log.info("Creating 'Above {}' area predicate", minArea);
                                    areaPredicates.add(criteriaBuilder.greaterThan(
                                        root.get("area"), minArea));
                                } else {
                                    // Range case - between minArea and maxArea (inclusive)
                                    log.info("Creating area range predicate: {} <= area <= {}", minArea, maxArea);
                                    areaPredicates.add(criteriaBuilder.and(
                                        criteriaBuilder.greaterThanOrEqualTo(root.get("area"), minArea),
                                        criteriaBuilder.lessThanOrEqualTo(root.get("area"), maxArea)
                                    ));
                                }
                            } catch (NumberFormatException e) {
                                log.warn("Invalid area range format: {}", range, e);
                            }
                        } else {
                            log.warn("Invalid area range format (expected min-max): {}", range);
                        }
                    }
                }
                
                if (!areaPredicates.isEmpty()) {
                    predicates.add(criteriaBuilder.or(areaPredicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
                    log.info("Added {} area predicates", areaPredicates.size());
                }
            }

            // Bedrooms filter
            if (criteria.getMinBedrooms() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("bedrooms"), criteria.getMinBedrooms()));
            }
            if (criteria.getMaxBedrooms() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("bedrooms"), criteria.getMaxBedrooms()));
            }

            // Bathrooms filter
            if (criteria.getMinBathrooms() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("bathrooms"), criteria.getMinBathrooms()));
            }
            if (criteria.getMaxBathrooms() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("bathrooms"), criteria.getMaxBathrooms()));
            }



            // Keyword search (title, description)
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keywordPattern = "%" + criteria.getKeyword().trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), keywordPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), keywordPattern)
                ));
            }

            // Status filter
            if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
                try {
                    PropertyStatus status = PropertyStatus.valueOf(criteria.getStatus().toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("propertyStatus"), status));
                } catch (IllegalArgumentException e) {
                    // Invalid status, ignore this filter
                }
            } else if (!criteria.getStatuses().isEmpty()) {
                // Handle multiple statuses
                List<PropertyStatus> validStatuses = new ArrayList<>();
                for (String statusStr : criteria.getStatuses()) {
                    try {
                        PropertyStatus status = PropertyStatus.valueOf(statusStr.toUpperCase());
                        validStatuses.add(status);
                    } catch (IllegalArgumentException e) {
                        // Invalid status, ignore this one
                    }
                }
                if (!validStatuses.isEmpty()) {
                    predicates.add(root.get("propertyStatus").in(validStatuses));
                }
            } else {
                // Default: show AVAILABLE, BOOKED, and RENTED properties if no status filter is specified
                predicates.add(root.get("propertyStatus").in(
                    PropertyStatus.AVAILABLE, 
                    PropertyStatus.BOOKED, 
                    PropertyStatus.RENTED
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }


}
