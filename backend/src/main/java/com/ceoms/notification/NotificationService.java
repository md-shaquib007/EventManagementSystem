package com.ceoms.notification;

import com.ceoms.entity.Notification;
import com.ceoms.entity.User;
import com.ceoms.entity.enums.NotificationType;
import com.ceoms.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Transactional
    public void send(User user, String title, String message, NotificationType type, Long referenceId) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .build();
        notificationRepository.save(notification);
        emailService.sendNotificationEmail(user.getEmail(), title, message);
    }
}
