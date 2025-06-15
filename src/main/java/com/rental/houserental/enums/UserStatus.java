package com.rental.houserental.enums;

public enum UserStatus {

    ACTIVE,         // Người dùng đang hoạt động bình thường
    SUSPENDED,      // Bị tạm khóa do vi phạm (spam, lừa đảo, v.v.)
    BANNED,         // Bị cấm vĩnh viễn (khóa tài khoản)
    PENDING,        // Đang chờ xét duyệt (đăng ký landlord, admin,...)
}
