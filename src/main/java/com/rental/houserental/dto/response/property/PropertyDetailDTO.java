package com.rental.houserental.dto.response.property;

import com.rental.houserental.enums.FurnishingType;
import com.rental.houserental.enums.PropertyStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDetailDTO {
    private Long id;
    private String title;
    private String description;

    // Địa chỉ chi tiết
    private String streetAddress;
    private String ward;
    private String province;
    private String fullAddress;  // Nếu cần ghép nhanh

    private Long categoryId;
    private String categoryName;

    private BigDecimal monthlyRent;
    private Integer bedrooms;
    private Integer bathrooms;
    private BigDecimal area;

    // Trạng thái hiện tại của property
    private PropertyStatus propertyStatus;

    private String mainImageUrl;
    private List<String> imageUrls; // Các ảnh khác nếu có

    // Đồ nội thất
    private FurnishingType furnishing; // FULL, BASIC, NONE

    // Thông tin đặt cọc
    private Integer depositPercentage; // Phần trăm đặt cọc

    // Tọa độ
    private Double latitude;
    private Double longitude;

    // Thông tin hệ thống
    private Integer views;       // Số lượt xem
    private Double rating;       // Đánh giá trung bình

    // Thời gian
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Các trường mở rộng (tuỳ yêu cầu)
    private String landlordName;
    private Long landlordId;
    private List<String> amenities; // Tiện ích đi kèm
}
