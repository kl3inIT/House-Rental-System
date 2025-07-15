package com.rental.houserental.service.impl;

import com.rental.houserental.service.NotificationWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationWebSocketServiceImpl implements NotificationWebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendNotification(Long userId, String message, String type) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", message);
        payload.put("type", type);
        payload.put("timestamp", LocalDateTime.now().toString());
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, payload);
    }
}