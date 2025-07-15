package com.rental.houserental.service;

import com.rental.houserental.entity.Notification;
import com.rental.houserental.entity.User;

import java.util.List;

public interface NotificationService {
    Notification createNotification(User user, String message, String type);

    List<Notification> getUserNotifications(User user);

    void markAsRead(Long notificationId);

    List<Notification> getUnreadNotifications(User user);
}