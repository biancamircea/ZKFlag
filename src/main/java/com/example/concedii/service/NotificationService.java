package com.example.concedii.service;

import com.example.concedii.model.Notification;
import com.example.concedii.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void createNotification(String message, String redirectPath, Long recipientId) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setRedirectPath(redirectPath);
        notification.setRecipientId(recipientId);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForEmployee(Long userId) {
        return notificationRepository.findByRecipientIdAndIsRead(userId, false);
    }

    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}

