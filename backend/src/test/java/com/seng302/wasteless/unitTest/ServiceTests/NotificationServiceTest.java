package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.model.NotificationTag;
import com.seng302.wasteless.model.NotificationType;
import com.seng302.wasteless.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class NotificationServiceTest {
    @Autowired
    NotificationService notificationService;

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
    void whenCheckValidTag_ValidTag_ThenReturnEnumTag() {
        NotificationTag tag = notificationService.checkValidTag("GREEN");
        assertEquals(NotificationTag.GREEN, tag);
    }

    @Test
    void whenCheckValidTag_nullTag_ThenReturnNull() {
        NotificationTag tag = notificationService.checkValidTag(null);
        assertNull(tag);
    }

    @Test
    void whenCheckValidTag_InvalidTag_ThenReturnEnumTag() {
        assertThrows(ResponseStatusException.class, () -> notificationService.checkValidTag("VIOLET"));
    }

}
