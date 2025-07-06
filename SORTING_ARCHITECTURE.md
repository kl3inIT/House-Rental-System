# Sorting Architecture Design

## Best Practices for Sorting in Spring Boot Applications

### 1. **Separation of Concerns**

#### ❌ Bad Design (Previous Implementation)
```java
// DTO contains sorting logic
public class SearchPropertyCriteriaDTO {
    @Builder.Default
    private String sortBy = "relevance"; // Sorting in DTO
}

// Controller handles sorting
@GetMapping("/properties/search")
public String searchProperties(@ModelAttribute SearchPropertyCriteriaDTO criteria, ...) {
    Pageable sortedPageable = applySorting(pageable, criteria.getSortBy()); // Sorting in controller
    return propertyService.searchProperties(criteria, sortedPageable);
}
```

#### ✅ Good Design (Current Implementation)
```java
// DTO only contains search criteria
public class SearchPropertyCriteriaDTO {
    // Only search filters, no sorting
}

// Controller delegates sorting to service
@GetMapping("/properties/search")
public String searchProperties(
    @ModelAttribute SearchPropertyCriteriaDTO searchCriteria,
    @RequestParam String sortBy, // Separate parameter
    Pageable pageable) {
    
    return propertyService.searchPropertiesWithSorting(searchCriteria, sortBy, pageable);
}

// Service handles sorting logic
@Service
public class PropertyServiceImpl {
    public Page<SearchPropertyResponseDTO> searchPropertiesWithSorting(
        SearchPropertyCriteriaDTO criteria, String sortBy, Pageable pageable) {
        
        SortOption sortOption = SortOption.fromValue(sortBy);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), 
            pageable.getPageSize(), sortOption.getSort());
        
        return searchProperties(criteria, sortedPageable);
    }
}
```

### 2. **Enum-Based Sorting**

#### ✅ Using Enum for Sort Options
```java
@Getter
public enum SortOption {
    RELEVANCE("relevance", "Relevance", 
        Sort.by(Sort.Direction.DESC, "createdAt")
            .and(Sort.by(Sort.Direction.ASC, "monthlyRent"))),
    PRICE_ASC("price_asc", "Price: Low to High", 
        Sort.by(Sort.Direction.ASC, "monthlyRent")),
    PRICE_DESC("price_desc", "Price: High to Low", 
        Sort.by(Sort.Direction.DESC, "monthlyRent"));

    private final String value;
    private final String displayName;
    private final Sort sort;

    public static SortOption fromValue(String value) {
        for (SortOption option : values()) {
            if (option.getValue().equals(value)) {
                return option;
            }
        }
        return RELEVANCE; // Default fallback
    }
}
```

### 3. **Architecture Layers**

#### **Controller Layer**
- **Responsibility**: Handle HTTP requests, validate input, delegate to service
- **Sorting**: Only pass sorting parameter to service
- **No Business Logic**: Should not contain sorting logic

#### **Service Layer**
- **Responsibility**: Business logic, data processing, orchestration
- **Sorting**: Handle all sorting logic here
- **Validation**: Validate sorting parameters
- **Transformation**: Convert sorting options to database queries

#### **Repository Layer**
- **Responsibility**: Data access, database operations
- **Sorting**: Execute sorted queries from service layer
- **No Business Logic**: Pure data access

### 4. **Benefits of This Design**

#### **Maintainability**
- Sorting logic centralized in service layer
- Easy to add new sorting options
- Clear separation of concerns

#### **Testability**
- Service methods can be unit tested independently
- Controller tests focus on HTTP handling
- Repository tests focus on data access

#### **Flexibility**
- Easy to change sorting implementation
- Support for complex sorting (multiple fields)
- Backward compatibility

#### **Performance**
- Sorting applied at database level
- Efficient query execution
- Proper indexing support

### 5. **Frontend Integration**

#### **Template (Thymeleaf)**
```html
<select name="sortBy" class="...">
    <option th:each="sortOption : ${sortOptions}" 
            th:value="${sortOption.value}" 
            th:text="'Sort by: ' + ${sortOption.displayName}"
            th:selected="${sortBy == sortOption.value}">
    </option>
</select>
```

#### **JavaScript**
```javascript
function initializeSortingHandler() {
    const sortSelect = document.querySelector('select[name="sortBy"]');
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            const url = new URL(window.location);
            url.searchParams.set('sortBy', this.value);
            window.location.href = url.toString();
        });
    }
}
```

### 6. **URL Structure**

#### **Clean URLs**
```
/properties/search?location=Hanoi&sortBy=price_asc&page=1
/properties/search?propertyTypes=1,2&sortBy=newest&page=2
```

#### **Pagination with Sorting**
```html
<!-- Preserve sorting in pagination links -->
<a th:href="@{/properties/search(page=${pageNum}, sortBy=${sortBy}, ...)}">
    Page {{pageNum}}
</a>
```

### 7. **Future Enhancements**

#### **Advanced Sorting**
```java
// Support for multiple sort fields
public enum SortOption {
    PRICE_THEN_AREA("price_then_area", "Price then Area", 
        Sort.by(Sort.Direction.ASC, "monthlyRent")
            .and(Sort.by(Sort.Direction.ASC, "area")));
}
```

#### **Dynamic Sorting**
```java
// Allow custom sort fields
public Page<SearchPropertyResponseDTO> searchPropertiesWithCustomSort(
    SearchPropertyCriteriaDTO criteria, 
    List<Sort.Order> customSorts, 
    Pageable pageable) {
    // Implementation
}
```

### 8. **Summary**

The current implementation follows Spring Boot best practices:

1. **DTO**: Contains only search criteria, no sorting
2. **Controller**: Handles HTTP requests, delegates sorting to service
3. **Service**: Contains all business logic including sorting
4. **Enum**: Provides type-safe sorting options
5. **Repository**: Executes sorted queries efficiently

This design ensures:
- ✅ Clean separation of concerns
- ✅ Maintainable and testable code
- ✅ Scalable architecture
- ✅ Type-safe sorting options
- ✅ Efficient database queries 