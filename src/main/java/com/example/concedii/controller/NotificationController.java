package com.example.concedii.controller;

import com.example.concedii.model.Notification;
import com.example.concedii.repository.NotificationRepository;
import com.example.concedii.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{employeeId}")
    public List<Notification> getNotifications(@PathVariable Long employeeId) {
        return notificationService.getNotificationsForEmployee(employeeId);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markNotificationAsRead(id);;
    }
}

