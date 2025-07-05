package com.rental.houserental.repository.specification;

import com.rental.houserental.dto.request.property.SearchPropertyCriteriaDTO;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.enums.PropertyStatus;
import com.rental.houserental.exceptions.property.InvalidPropertyStatusException;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RentalPropertySpecification {

    private RentalPropertySpecification() {
        // Private constructor to prevent instantiation
    }

    public static Specification<RentalProperty> withCriteria(SearchPropertyCriteriaDTO criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getStatus() != null && !criteria.getStatus().isBlank()) {
                try {
                    PropertyStatus status = PropertyStatus.fromString(criteria.getStatus());
                    predicates.add(cb.equal(root.get("propertyStatus"), status));
                } catch (InvalidPropertyStatusException e) {
                    throw new InvalidPropertyStatusException("Invalid property status: " + criteria.getStatus());
                }
            }

            // 🔍 Location (city, province, streetAddress)
            if (criteria.getLocation() != null && !criteria.getLocation().isBlank()) {
                String likeValue = "%" + criteria.getLocation().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("city")), likeValue),
                        cb.like(cb.lower(root.get("province")), likeValue),
                        cb.like(cb.lower(root.get("streetAddress")), likeValue)
                ));
            }

            if (criteria.getPropertyType() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), criteria.getPropertyType()));
            }

            if (criteria.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("monthlyRent"), criteria.getMinPrice()));
            }

            if (criteria.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("monthlyRent"), criteria.getMaxPrice()));
            }

            if (criteria.getMinBedrooms() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("bedrooms"), criteria.getMinBedrooms()));
            }

            if (criteria.getMaxBedrooms() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("bedrooms"), criteria.getMaxBedrooms()));
            }

            if (criteria.getMinBathrooms() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("bathrooms"), criteria.getMinBathrooms()));
            }

            if (criteria.getMaxBathrooms() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("bathrooms"), criteria.getMaxBathrooms()));
            }

            if (criteria.getKeyword() != null && !criteria.getKeyword().isBlank()) {
                String likeKeyword = "%" + criteria.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), likeKeyword),
                        cb.like(cb.lower(root.get("description")), likeKeyword)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
