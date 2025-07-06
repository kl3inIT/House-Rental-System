# Vietnam Address Policy Update - July 1, 2025

## Overview
This update implements Vietnam's new address policy using the official API from [vietnamlabs.com](https://vietnamlabs.com/api/vietnamprovince) to replace the old district-based system with a ward-based system.

## Changes Made

### 1. Database Schema Changes
- **Removed**: `city` field from `RentalProperty` table
- **Added**: `ward` field to `RentalProperty` table
- **Migration**: Existing city data copied to ward field

### 2. Entity Updates
- **RentalProperty.java**: Updated to use `ward` instead of `city`
- **getFullAddress()**: Now returns `streetAddress, ward, province`

### 3. DTO Updates
- **CreatePropertyRequestDTO**: Replaced `city` with `ward` field
- **SearchPropertyCriteriaDTO**: Added `province` and `ward` fields (kept `location` for backward compatibility)
- **SearchPropertyResponseDTO**: Replaced `city` with `ward` field
- **FeaturedPropertyResponseDTO**: Added `ward` and `province` fields (kept `location` for backward compatibility)

### 4. Service Layer Updates
- **PropertyServiceImpl**: Updated to use new address structure
- **RentalPropertySpecification**: Enhanced search to support province and ward filtering

### 5. Frontend Updates
- **new-listing.html**: Updated form to use "Phường/Xã" instead of "Quận/Huyện"
- **new-listing.js**: Updated to use new API structure
- **search-properties.js**: Updated location autocomplete
- **home.js**: Updated location autocomplete

### 6. API Integration
- **New API**: `https://vietnamlabs.com/api/vietnamprovince`
- **All Provinces**: `GET /api/vietnamprovince`
- **Province Wards**: `GET /api/vietnamprovince?province={province_name}`

## API Response Structure

### All Provinces Response
```json
{
  "success": true,
  "data": [
    {
      "province": "Hà Nội",
      "wards": [
        {
          "name": "Phường Hoàn Kiếm",
          "mergedFrom": [...]
        }
      ]
    }
  ],
  "pagination": {...},
  "timestamp": "2025-07-01T16:20:58.841Z"
}
```

### Province Wards Response
```json
{
  "success": true,
  "data": {
    "province": "Bắc Ninh",
    "wards": [
      {
        "name": "Xã Đại Sơn",
        "mergedFrom": [...]
      }
    ]
  },
  "timestamp": "2025-07-01T16:20:58.841Z"
}
```

## Database Migration

Run the migration script: `src/main/resources/db-migration-2025-07-01.sql`

```sql
-- Add new ward column
ALTER TABLE RentalProperty ADD COLUMN Ward NVARCHAR(100);

-- Copy existing data
UPDATE RentalProperty SET Ward = City WHERE City IS NOT NULL;

-- Make ward NOT NULL
ALTER TABLE RentalProperty ALTER COLUMN Ward NVARCHAR(100) NOT NULL;

-- Drop old city column
ALTER TABLE RentalProperty DROP COLUMN City;
```

## Backward Compatibility

The system maintains backward compatibility by:
1. Keeping the `location` field in search criteria
2. Maintaining the `location` field in response DTOs
3. Supporting both old and new search patterns

## Testing

### Property Creation
1. Navigate to `/landlord/properties/new`
2. Fill in property details
3. Select province from dropdown
4. Select ward from dropdown (populated based on province)
5. Verify form submission works

### Property Search
1. Navigate to `/properties/search`
2. Test location autocomplete
3. Verify search results display correct address format
4. Test pagination with search parameters

### Location Autocomplete
1. Type in location search field
2. Verify provinces and wards appear in suggestions
3. Test keyboard navigation (arrow keys, enter, escape)
4. Verify selection populates the search field

## Benefits

1. **Compliance**: Follows Vietnam's official address policy
2. **Accuracy**: Uses official ward names and boundaries
3. **User Experience**: Better location selection with proper Vietnamese terminology
4. **Maintainability**: Cleaner data structure with proper address hierarchy

## Future Enhancements

1. Add district-level filtering if needed
2. Implement address validation
3. Add postal code support
4. Implement address geocoding 