package com.rental.houserental.service;

public interface NotificationWebSocketService {
    void sendNotification(Long userId, String message, String type);
}