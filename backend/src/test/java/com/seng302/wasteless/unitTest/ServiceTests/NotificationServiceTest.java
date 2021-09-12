package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.model.*;
import com.seng302.wasteless.repository.NotificationRepository;
import com.seng302.wasteless.service.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        List<Notification> notifications = notificationService.filterNotifications(1, Optional.empty(), Optional.empty());

        Assertions.assertEquals(oldNotificationId, notifications.get(0).getId());
        Assertions.assertEquals(olderNotificationId, notifications.get(1).getId());
    }


    @Test
    void whenGetNotificationForUser_AndFilterByTag_AndTagIsRed_ThenReturnAllNotificationsIsRed() {
        notificationRepository.deleteAll();

        Notification newNotification1 = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        newNotification1.setTag(NotificationTag.RED);
        newNotification1 = notificationService.saveNotification(newNotification1);

        Notification newNotification2 = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        notificationService.saveNotification(newNotification2);

        Notification newNotification3 = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        notificationService.saveNotification(newNotification3);

        List<String> tags = new ArrayList<>();
        tags.add("red");

        List<Notification> notifications = notificationService.filterNotifications(1, Optional.of(tags), Optional.empty());

        assertEquals(notifications.size(), 1);
        assertEquals(newNotification1.getId(), newNotification1.getId());
        assertTrue(notifications.stream().allMatch(notification -> notification.getTag().equals(NotificationTag.RED)));

    }

    @Test
    void whenGetNotificationForUser_AndFilterByTagRed_AndAllTagIsBlue_ThenReturnEmptyNotifications() {
        notificationRepository.deleteAll();

        Notification newNotification1 = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        newNotification1.setTag(NotificationTag.BLUE);
        notificationService.saveNotification(newNotification1);

        Notification newNotification2 = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        newNotification2.setTag(NotificationTag.BLUE);
        notificationService.saveNotification(newNotification2);

        Notification newNotification3 = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        newNotification3.setTag(NotificationTag.BLUE);
        notificationService.saveNotification(newNotification3);

        List<String> tags = new ArrayList<>();
        tags.add("red");

        List<Notification> notifications = notificationService.filterNotifications(1, Optional.of(tags), Optional.empty());

        assertTrue(notifications.isEmpty());

    }

    @Test
    void whenGetNotificationForUser_AndFilterByTagsRedAndBlue_ThenReturnAllNotificationsTagsEqualRedOrBlue() {
        notificationRepository.deleteAll();

        Notification newNotification1 = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        newNotification1.setTag(NotificationTag.RED);
        notificationService.saveNotification(newNotification1);

        Notification newNotification2 = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        newNotification2.setTag(NotificationTag.BLUE);
        notificationService.saveNotification(newNotification2);

        Notification newNotification3 = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        newNotification3.setTag(NotificationTag.BLACK);
        notificationService.saveNotification(newNotification3);

        List<String> tags = new ArrayList<>();
        tags.add("red");
        tags.add("blue");

        List<Notification> notifications = notificationService.filterNotifications(1, Optional.of(tags), Optional.empty());

        assertEquals(notifications.size(), 2);
        assertTrue(notifications.stream().allMatch(notification -> notification.getTag().equals(NotificationTag.RED) || notification.getTag().equals(NotificationTag.BLUE)));

    }

    @Test
    void whenGetArchivedNotificationForUser_ThenReturnAllNotificationsArchived() {
        notificationRepository.deleteAll();

        Notification newNotification1 = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        newNotification1.setArchived(true);
        notificationService.saveNotification(newNotification1);

        Notification newNotification2 = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        newNotification2.setArchived(false);
        notificationService.saveNotification(newNotification2);

        Notification newNotification3 = NotificationService.createNotification(1, 1, NotificationType.LIKEDLISTING, "");
        newNotification3.setArchived(false);
        notificationService.saveNotification(newNotification3);

        List<Notification> notifications = notificationService.filterNotifications(1, Optional.empty(), Optional.of(true));

        assertEquals(notifications.size(), 1);
        assertTrue(notifications.stream().allMatch(notification -> notification.getArchived().equals(true) || notification.getTag().equals(false)));
    }

}
