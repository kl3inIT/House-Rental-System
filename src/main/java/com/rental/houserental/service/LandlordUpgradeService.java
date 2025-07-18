package com.rental.houserental.service;

import com.rental.houserental.entity.LandlordUpgrade;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.LandlordUpgradeRequestStatus;

import java.util.List;

public interface LandlordUpgradeService {
    LandlordUpgrade createRequest(User user, String fullName, String phone, String reason);

    List<LandlordUpgrade> getRequestsByStatus(LandlordUpgradeRequestStatus status);

    List<LandlordUpgrade> getRequestsByUser(User user);

    LandlordUpgrade getRequestById(Long id);

    LandlordUpgrade approveRequest(Long requestId);

    LandlordUpgrade rejectRequest(Long requestId, String reason);

    List<LandlordUpgrade> getAllRequests();
}