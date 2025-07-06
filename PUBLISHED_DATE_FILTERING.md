# Published Date Filtering Feature

## Overview

The application now supports filtering properties by their published date using the new `publishedAt` field in the `RentalProperty` entity.

## Database Schema

### RentalProperty Entity
```java
@Entity
public class RentalProperty extends BaseEntity {
    // ... other fields
    
    @Column(name = "PublishedAt")
    private LocalDateTime publishedAt;
    
    // ... other fields
}
```

## Backend Implementation

### 1. SearchPropertyCriteriaDTO
```java
public class SearchPropertyCriteriaDTO {
    // Published date filters
    private LocalDate publishedFrom;
    private LocalDate publishedTo;
    private List<String> publishedRanges; // e.g., ["today", "week", "month", "3months"]
}
```

### 2. SortOption Enum
```java
public enum SortOption {
    PUBLISHED_NEWEST("published_newest", "Recently Published", 
        Sort.by(Sort.Direction.DESC, "publishedAt")),
    PUBLISHED_OLDEST("published_oldest", "Oldest Published", 
        Sort.by(Sort.Direction.ASC, "publishedAt"));
}
```

### 3. RentalPropertySpecification
```java
// Published Date filters
if (criteria.getPublishedRanges() != null && !criteria.getPublishedRanges().isEmpty()) {
    List<Predicate> publishedPredicates = new ArrayList<>();
    
    for (String range : criteria.getPublishedRanges()) {
        LocalDateTime fromDate = getPublishedFromDate(range);
        LocalDateTime toDate = getPublishedToDate(range);
        
        if (fromDate != null && toDate != null) {
            publishedPredicates.add(criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo(root.get("publishedAt"), fromDate),
                criteriaBuilder.lessThanOrEqualTo(root.get("publishedAt"), toDate)
            ));
        }
    }
    
    if (!publishedPredicates.isEmpty()) {
        predicates.add(criteriaBuilder.or(publishedPredicates.toArray(new Predicate[0])));
    }
}
```

### 4. Supported Date Ranges
```java
private static LocalDateTime getPublishedFromDate(String range) {
    LocalDate today = LocalDate.now();
    
    switch (range.toLowerCase()) {
        case "today":
            return today.atStartOfDay();
        case "week":
            return today.minusWeeks(1).atStartOfDay();
        case "month":
            return today.minusMonths(1).atStartOfDay();
        case "3months":
            return today.minusMonths(3).atStartOfDay();
        case "6months":
            return today.minusMonths(6).atStartOfDay();
        case "year":
            return today.minusYears(1).atStartOfDay();
        default:
            return null;
    }
}
```

## Frontend Implementation

### 1. Filter UI
```html
<!-- Published Date -->
<div>
    <h4 class="font-medium text-gray-700 mb-3">Published Date</h4>
    <div class="space-y-2">
        <label class="flex items-center">
            <input type="checkbox" name="publishedRanges" value="today" 
                   th:checked="${#lists.contains(searchCriteria?.publishedRanges, 'today')}">
            <span class="ml-2 text-sm text-gray-600">Today</span>
        </label>
        <label class="flex items-center">
            <input type="checkbox" name="publishedRanges" value="week">
            <span class="ml-2 text-sm text-gray-600">This Week</span>
        </label>
        <!-- ... more options -->
    </div>
</div>
```

### 2. JavaScript Logic
```javascript
function handlePublishedDateFilterChange(checkbox) {
    const form = checkbox.closest('form');
    if (!form) return;
    
    // Get all published date checkboxes
    const publishedCheckboxes = form.querySelectorAll('input[name="publishedRanges"]');
    const checkedValues = Array.from(publishedCheckboxes)
        .filter(cb => cb.checked)
        .map(cb => cb.value);
    
    // If this is a "shorter" time range being selected, uncheck "longer" ranges
    const timeRanges = ['today', 'week', 'month', '3months', '6months', 'year'];
    const currentIndex = timeRanges.indexOf(checkbox.value);
    
    if (checkbox.checked && currentIndex >= 0) {
        // Uncheck longer time ranges
        publishedCheckboxes.forEach(cb => {
            const cbIndex = timeRanges.indexOf(cb.value);
            if (cbIndex > currentIndex) {
                cb.checked = false;
            }
        });
    }
    
    // Submit the form
    setTimeout(() => {
        form.submit();
    }, 100);
}
```

### 3. Search Results Display
```html
<span class="flex items-center" th:if="${property.publishedAt != null}">
    <i class="fas fa-calendar-alt mr-1 text-blue-500"></i>
    <span th:text="${#temporals.format(property.publishedAt, 'dd/MM/yyyy')}">01/01/2024</span>
</span>
```

## Features

### 1. **Predefined Date Ranges**
- **Today**: Properties published today
- **This Week**: Properties published in the last 7 days
- **This Month**: Properties published in the last 30 days
- **Last 3 Months**: Properties published in the last 90 days
- **Last 6 Months**: Properties published in the last 180 days
- **Last Year**: Properties published in the last 365 days

### 2. **Smart Filter Logic**
- Multiple ranges can be selected (OR logic)
- Shorter ranges automatically uncheck longer ranges
- Form auto-submits when filters change

### 3. **Sorting Options**
- **Recently Published**: Sort by published date (newest first)
- **Oldest Published**: Sort by published date (oldest first)

### 4. **URL Parameters**
```
/properties/search?publishedRanges=today&publishedRanges=week&sortBy=published_newest
```

## Usage Examples

### 1. **Find Recent Properties**
```
GET /properties/search?publishedRanges=today&publishedRanges=week&sortBy=published_newest
```

### 2. **Find Properties from Last Month**
```
GET /properties/search?publishedRanges=month&sortBy=published_newest
```

### 3. **Combine with Other Filters**
```
GET /properties/search?location=Hanoi&publishedRanges=month&priceRanges=1000000-3000000&sortBy=published_newest
```

## Database Considerations

### 1. **Indexing**
```sql
-- Add index for better performance
CREATE INDEX idx_rental_property_published_at ON RentalProperty(PublishedAt);
```

### 2. **Data Migration**
```sql
-- Update existing properties with publishedAt = createdAt
UPDATE RentalProperty 
SET PublishedAt = CreatedAt 
WHERE PublishedAt IS NULL;
```

## Performance Optimization

### 1. **Query Optimization**
- Use database indexes on `publishedAt` field
- Leverage JPA Specifications for efficient queries
- Apply date range filters at database level

### 2. **Caching Strategy**
- Cache frequently used date ranges
- Consider Redis caching for search results

## Future Enhancements

### 1. **Advanced Date Filters**
- Custom date range picker
- Relative date filters (e.g., "last business day")
- Time-based filters (morning, afternoon, evening)

### 2. **Analytics**
- Track most popular date ranges
- Property publication trends
- User behavior analysis

### 3. **Notifications**
- Alert users about new properties in their preferred date range
- Email notifications for recent listings 