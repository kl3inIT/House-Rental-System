package com.rental.houserental.repository.impl;

import com.rental.houserental.dto.request.property.SearchPropertyCriteriaDTO;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.repository.SearchRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SearchRepositoryImpl implements SearchRepository {

    private final EntityManager entityManager;

    @Override
    public Page<RentalProperty> searchProperties(SearchPropertyCriteriaDTO criteria, Pageable pageable) {
        return null;
    }
}
