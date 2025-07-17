package com.rental.houserental.dto.request.property;

import com.rental.houserental.enums.PropertyStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandlordPropertyFilterDTO {
    private PropertyStatus status;    // Trạng thái tài sản (AVAILABLE, OCCUPIED, DRAFT, ARCHIVED)
    private String keyword;           // Tìm kiếm theo tên/từ khóa
    private Long categoryId;          // Lọc theo loại bất động sản
    private Integer minBedrooms;      // Lọc theo số phòng ngủ tối thiểu
    private Integer maxBedrooms;      // Lọc theo số phòng ngủ tối đa
    private Integer minBathrooms;
    private Integer maxBathrooms;
    private Integer minPrice;      // Giá thấp nhất
    private Integer maxPrice;
}
