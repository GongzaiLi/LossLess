package com.seng302.wasteless.unitTest;


import com.seng302.wasteless.controller.NotificationController;
import com.seng302.wasteless.model.Notification;
import com.seng302.wasteless.model.NotificationType;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.service.NotificationService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import static com.seng302.wasteless.TestUtils.newUserWithEmail;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private NotificationService notificationService;

    User loggedInUser;
    Notification notification;

    User userForNotificationTwo;
    User admin;


    @BeforeEach
    void setUp() {
        loggedInUser = newUserWithEmail("james@gmail.com");
        loggedInUser.setId(1);

        notification = NotificationService.createNotification(loggedInUser.getId(), 1, NotificationType.LIKEDLISTING, "");
        notification.setId(1);
        notificationService.saveNotification(notification);

        Mockito.when(userService.getCurrentlyLoggedInUser())
                .thenReturn(loggedInUser);

        userForNotificationTwo = newUserWithEmail("notDemo@gmail.com");
        userForNotificationTwo.setId(2);
        userForNotificationTwo.setRole(UserRoles.USER);

        admin = newUserWithEmail("admin@gmail.com");
        admin.setId(10);
        admin.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);

        Mockito.when(notificationService.findNotificationById(any(Integer.class)))
                .thenReturn(notification);

    }

    @Test
    void whenDeleteRequestForNotification_andNotificationExists_AndUserSelf_then200Response() throws Exception {
        notification.setUserId(1);
        Mockito.when(notificationService.findNotificationById(1))
                .thenReturn(notification);

        mockMvc.perform(MockMvcRequestBuilders.delete("/notifications/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDeleteRequestForNotification_andNotificationExists_AndUserAdmin_then200Response() throws Exception {
        notification.setUserId(2);
        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(admin);

        mockMvc.perform(MockMvcRequestBuilders.delete("/notifications/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDeleteRequestForNotification_andNotificationDoesNotExist_AndUserSelf_then406Response() throws Exception {
        Mockito
                .when(notificationService.findNotificationById(1))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No notification exists with the given ID"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/notifications/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteRequestForNotification_andNotificationExists_AndUserOther_then403Response() throws Exception {
        notification.setUserId(2);

        mockMvc.perform(MockMvcRequestBuilders.delete("/notifications/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenPatchRequestToNotification_andLoggedInUserDoesNotOwnNotification_then403Response() throws Exception {
        notification.setUserId(2);
        Mockito.when(notificationService.findNotificationById(any(Integer.class)))
                .thenReturn(notification);

        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/1")
                .content("{\"read\": true}")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenPatchRequestToNotification_andLoggedInUserDoesNotOwnNotificationButIsAdmin_then200Response() throws Exception {
        notification.setUserId(2);
        loggedInUser.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);
        Mockito.when(notificationService.findNotificationById(any(Integer.class)))
                .thenReturn(notification);

        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/1")
                .content("{\"read\": true}")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"read\": true}", "{\"read\": true,\"starred\": true,\"archived\": false}", "{}"})
    void whenPatchRequestToNotification_andLoggedInUserOwnsNotification_andRequestValid_then200Response(String request) throws Exception {
        notification.setUserId(loggedInUser.getId());
        Mockito.when(notificationService.findNotificationById(any(Integer.class)))
                .thenReturn(notification);

        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/1")
                .content(request)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenPatchRequestToNotification_andLoggedInUserOwnsNotification_andInvalidValue_then400Response() throws Exception {
        notification.setUserId(loggedInUser.getId());
        Mockito.when(notificationService.findNotificationById(any(Integer.class)))
                .thenReturn(notification);

        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/1")
                .content("{\"read\": \"asdf\"}")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
