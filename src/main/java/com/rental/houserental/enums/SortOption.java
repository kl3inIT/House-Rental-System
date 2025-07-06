package com.rental.houserental.enums;

import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public enum SortOption {
    RELEVANCE("relevance", "Relevance", 
        Sort.by(Sort.Direction.DESC, "createdAt")
            .and(Sort.by(Sort.Direction.ASC, "monthlyRent"))),
    PRICE_ASC("price_asc", "Price: Low to High", 
        Sort.by(Sort.Direction.ASC, "monthlyRent")),
    PRICE_DESC("price_desc", "Price: High to Low", 
        Sort.by(Sort.Direction.DESC, "monthlyRent")),
    AREA_ASC("area_asc", "Area: Small to Large", 
        Sort.by(Sort.Direction.ASC, "area")),
    AREA_DESC("area_desc", "Area: Large to Small", 
        Sort.by(Sort.Direction.DESC, "area")),
    NEWEST("newest", "Newest First", 
        Sort.by(Sort.Direction.DESC, "createdAt")),
    PUBLISHED_NEWEST("published_newest", "Recently Published", 
        Sort.by(Sort.Direction.DESC, "publishedAt")),
    PUBLISHED_OLDEST("published_oldest", "Oldest Published", 
        Sort.by(Sort.Direction.ASC, "publishedAt"));

    private final String value;
    private final String displayName;
    private final Sort sort;

    SortOption(String value, String displayName, Sort sort) {
        this.value = value;
        this.displayName = displayName;
        this.sort = sort;
    }

    public static SortOption fromValue(String value) {
        for (SortOption option : values()) {
            if (option.getValue().equals(value)) {
                return option;
            }
        }
        return RELEVANCE; // Default fallback
    }
} 