package com.rental.houserental.service;

import com.rental.houserental.entity.LandlordUpgradeRequest;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.LandlordUpgradeRequestStatus;

import java.util.List;

public interface LandlordUpgradeRequestService {
    LandlordUpgradeRequest createRequest(User user, String fullName, String phone, String reason);

    List<LandlordUpgradeRequest> getRequestsByStatus(LandlordUpgradeRequestStatus status);

    List<LandlordUpgradeRequest> getRequestsByUser(User user);

    LandlordUpgradeRequest getRequestById(Long id);

    LandlordUpgradeRequest approveRequest(Long requestId);

    LandlordUpgradeRequest rejectRequest(Long requestId, String reason);

    List<LandlordUpgradeRequest> getAllRequests();
}