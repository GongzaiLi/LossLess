package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.model.Notification;
import com.seng302.wasteless.model.NotificationType;
import com.seng302.wasteless.repository.NotificationRepository;
import com.seng302.wasteless.service.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class NotificationServiceTest {
    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationRepository notificationRepository;

    @Test
    void whenFindNotificationById_AndNoNotificationExists_ThrowException() {
        assertThrows(ResponseStatusException.class, () -> notificationService.findNotificationById(43598));
    }

    @Test
    void whenFindNotificationById_NotificationExists_ThenNotificationIsReturned() {
        Integer createdNotificationId = notificationService.saveNotification(
                NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, ""))
                .getId();

        assertNotNull(notificationService.findNotificationById(createdNotificationId));
    }

    @Test
    void whenGetNotificationForUser_AndOlderNotificationIsStarred_ThenStarredAppearsOnTop() {
        notificationRepository.deleteAll();

        Notification newNotification = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        notificationService.saveNotification(newNotification);

        Notification oldStarredNotification = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        oldStarredNotification.setCreated(LocalDateTime.now().minusYears(10));
        oldStarredNotification.setStarred(true);
        Integer oldNotificationId = notificationService.saveNotification(oldStarredNotification).getId();

        Notification olderStarredNotification = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        olderStarredNotification.setCreated(LocalDateTime.now().minusYears(20));
        olderStarredNotification.setStarred(true);
        Integer olderNotificationId = notificationService.saveNotification(olderStarredNotification).getId();

        List<Notification> notifications = notificationService.findAllUnArchivedNotificationsByUserId(1);

        Assertions.assertEquals(oldNotificationId, notifications.get(0).getId());
        Assertions.assertEquals(olderNotificationId, notifications.get(1).getId());
    }

}
