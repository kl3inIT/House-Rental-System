package com.rental.houserental.service.impl;

import com.rental.houserental.entity.LandlordUpgrade;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.LandlordUpgradeRequestStatus;
import com.rental.houserental.enums.UserRole;
import com.rental.houserental.repository.LandlordUpgradeRepository;
import com.rental.houserental.repository.UserRepository;
import com.rental.houserental.service.LandlordUpgradeService;
import com.rental.houserental.service.EmailService;
import com.rental.houserental.service.NotificationService;
import com.rental.houserental.service.NotificationWebSocketService;
import com.rental.houserental.exceptions.user.LandlordRequestAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LandlordUpgradeServiceImpl implements LandlordUpgradeService {
    @Autowired
    private LandlordUpgradeRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationWebSocketService notificationWebSocketService;

    @Override
    @Transactional
    public LandlordUpgrade createRequest(User user, String fullName, String phone, String reason) {
        if (requestRepository.findByUserAndStatus(user, LandlordUpgradeRequestStatus.PENDING).isPresent()) {
            throw new LandlordRequestAlreadyExistsException(
                    "You already have a pending upgrade request. Please wait for admin review.");
        }
        LandlordUpgrade request = new LandlordUpgrade();
        request.setUser(user);
        request.setStatus(LandlordUpgradeRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        request.setFullName(fullName);
        request.setPhone(phone);
        request.setReason(reason);
        return requestRepository.save(request);
    }

    @Override
    public List<LandlordUpgrade> getRequestsByStatus(LandlordUpgradeRequestStatus status) {
        return requestRepository.findAllByStatus(status);
    }

    @Override
    public List<LandlordUpgrade> getRequestsByUser(User user) {
        return requestRepository.findAllByUser(user);
    }

    @Override
    public LandlordUpgrade getRequestById(Long id) {
        return requestRepository.findById(id).orElse(null);
    }

    @Override
    public List<LandlordUpgrade> getAllRequests() {
        return requestRepository.findAll();
    }

    @Override
    @Transactional
    public LandlordUpgrade approveRequest(Long requestId) {
        LandlordUpgrade request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (request.getStatus() != LandlordUpgradeRequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }
        User user = request.getUser();
        user.setRole(UserRole.LANDLORD);
        userRepository.save(user);
        request.setStatus(LandlordUpgradeRequestStatus.APPROVED);
        request.setUpdatedAt(LocalDateTime.now());

        // Gửi email
        emailService.sendLandlordRequestApprovalEmail(user);

        // Lưu notification
        String message = "Yêu cầu nâng cấp tài khoản của bạn đã được chấp nhận.";
        notificationService.createNotification(user, message, "upgrade-request-approved");
        // Gửi notification realtime
        notificationWebSocketService.sendNotification(user.getId(), message, "upgrade-request-approved");

        return requestRepository.save(request);
    }

    @Override
    @Transactional
    public LandlordUpgrade rejectRequest(Long requestId, String reason) {
        LandlordUpgrade request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (request.getStatus() != LandlordUpgradeRequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }
        request.setStatus(LandlordUpgradeRequestStatus.REJECTED);
        request.setUpdatedAt(LocalDateTime.now());
        request.setReason(reason);

        // Gửi email
        emailService.sendLandlordRequestRejectionEmail(request.getUser(), reason);

        // Lưu notification
        String message = "Yêu cầu nâng cấp tài khoản của bạn đã bị từ chối. Lý do: " + reason;
        notificationService.createNotification(request.getUser(), message, "upgrade-request-rejected");
        // Gửi notification realtime
        notificationWebSocketService.sendNotification(request.getUser().getId(), message, "upgrade-request-rejected");

        return requestRepository.save(request);
    }
}