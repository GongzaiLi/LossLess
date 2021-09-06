package com.seng302.wasteless.unitTest;

import com.seng302.wasteless.controller.CardController;
import com.seng302.wasteless.controller.NotificationController;
import com.seng302.wasteless.dto.mapper.GetUserDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.NotificationService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(NotificationController.class)
@TestPropertySource(properties = {"max-display-period-seconds=10"})
class NotificationControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private User admin;

    @BeforeEach
    void setUp() {
        User userForNotification = new User();
        userForNotification.setId(1);
        userForNotification.setEmail("demo@gmail.com");
        userForNotification.setRole(UserRoles.USER);
        userForNotification.setCreated(LocalDate.now());
        userForNotification.setDateOfBirth(LocalDate.now());
        userForNotification.setHomeAddress(Mockito.mock(Address.class));


        LocalDateTime created = LocalDateTime.of(2021, Month.JANUARY, 1, 0, 0, 0);

        Notification notification = new Notification();
        notification.setId(1);
        notification.setUserId(userForNotification.getId());
        notification.setType(NotificationType.EXPIRED);
        notification.setMessage("some message");   //check message
        notification.setSubjectId(1);
        notification.setCreated(created);


        User userForNotificationTwo = new User();
        userForNotificationTwo.setId(2);
        userForNotificationTwo.setEmail("notDemo@gmail.com");
        userForNotificationTwo.setRole(UserRoles.USER);
        userForNotificationTwo.setCreated(LocalDate.now());
        userForNotificationTwo.setDateOfBirth(LocalDate.now());
        userForNotificationTwo.setHomeAddress(Mockito.mock(Address.class));

        Notification notificationTwo = new Notification();
        notificationTwo.setId(2);
        notificationTwo.setUserId(userForNotificationTwo.getId());
        notificationTwo.setType(NotificationType.PURCHASED_LISTING);
        notificationTwo.setMessage("some message");   //check message
        notificationTwo.setSubjectId(2);
        notificationTwo.setCreated(created);

        admin = new User();
        admin.setId(10);
        admin.setEmail("admin@gmail.com");
        admin.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);
        admin.setCreated(LocalDate.now());
        admin.setDateOfBirth(LocalDate.now());
        admin.setHomeAddress(Mockito.mock(Address.class));

        Mockito
                .when(notificationService.createNotification(any(Integer.class), any(Integer.class), any(NotificationType.class), any(String.class)))
                .thenReturn(notification);

        Mockito
                .when(authentication.getName())
                .thenReturn("demo@gmail.com");

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(userForNotification);

        Mockito
                .when(notificationService.findNotificationById(1))
                .thenReturn(notification);

        Mockito
                .when(notificationService.findNotificationById(10))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Notification with given ID does not exist"));

        Mockito
                .when(notificationService.findNotificationById(2))
                .thenReturn(notificationTwo);


    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenDeleteRequestForNotification_andNotificationExists_AndUserSelf_then200Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/notifications/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "random", roles = "GLOBAL_APPLICATION_ADMIN")
    void whenDeleteRequestForNotification_andNotificationExists_AndUserAdmin_then200Response() throws Exception {
        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(admin);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/notifications/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenDeleteRequestForNotification_andNotificationDoesNotExist_AndUserSelf_then406Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/notifications/10")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenDeleteRequestForNotification_andNotificationExists_AndUserOther_then403Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/notifications/2")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
