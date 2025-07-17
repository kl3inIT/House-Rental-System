package com.rental.houserental.controller;

import com.rental.houserental.entity.Notification;
import com.rental.houserental.entity.User;
import com.rental.houserental.service.NotificationService;
import com.rental.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public List<Notification> getMyNotifications() {
        User user = userService.getCurrentUser();
        return notificationService.getUserNotifications(user);
    }

    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}