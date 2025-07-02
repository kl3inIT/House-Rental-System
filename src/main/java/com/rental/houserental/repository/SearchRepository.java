package com.rental.houserental.repository;

import com.rental.houserental.dto.request.property.SearchPropertyCriteriaDTO;
import com.rental.houserental.entity.RentalProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchRepository {

    Page<RentalProperty> searchProperties(SearchPropertyCriteriaDTO criteria, Pageable pageable);

}
