package com.seng302.wasteless.unitTest;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.Notification;
import com.seng302.wasteless.model.NotificationType;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.NotificationService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.testconfigs.MockUserServiceConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@Import(MockUserServiceConfig.class)
 class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @MockBean
    private AddressService addressService;

    @MockBean
    private NotificationService notificationService;

    @BeforeEach
            void setUp() {
        Notification notification = new Notification();
        notification.setType(NotificationType.EXPIRED);
        notification.setSubjectId(1);
        notification.setMessage(String.format("Your card has expired"));
        notification.setUserId(1);
        List<Notification> notifs = new ArrayList<>();
        notifs.add(notification);

        Mockito
                .when(notificationService.findAllNotificationsByUserId(1))
                .thenReturn(notifs);
    }

    @Test
     void whenPostRequestToUsersAndValidUser_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void whenPostRequestToUsers_AndEmailWithoutATSymbol_thenBadResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"1\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPostRequestToUsers_AndEmailWithATSymbolAtTheEnd_thenBadResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"1@\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPostRequestToUsers_AndEmailWithATSymbolAtTheStart_thenBadResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"@1\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
     void whenPostRequestToUsersAndUserInvalidDueToMissingFirstName_thenCorrectResponse() throws Exception {
        String user = "{\"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"27-10-2000\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
     void whenPostRequestToUsersAndUserInvalidDueToMissingLastName_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
     void whenPostRequestToUsersAndUserInvalidDueToMissingEmail_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
     void whenPostRequestToUsersAndUserInvalidDueToMissingDateOfBirth_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }: \"236a Blenheim Road\", \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
     void whenPostRequestToUsersAndUserInvalidDueToMissingHomeAddress_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"2000-10-27\", \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
     void whenPostRequestToUsersAndUserInvalidDueToMissingPassword_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
     void whenPostRequestToUsersAndUserInvalidDueToAlreadyUsedEmail_thenCorrectResponse() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", "{\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }", "1337");

        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
     void whenPostRequestToUsersAndUserInvalidDueToDateOfBirthTooOld_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"1800-20-10\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPostRequestToUsersAndUserInvalidDueToDateOfBirthUnderThirteen_thenCorrectResponse() throws Exception {
        LocalDate today = LocalDate.now();
        String minimumDOB = today.minusYears(12).toString();
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \""+minimumDOB+"\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    void whenPostRequestToUsersAndUserValidOnBirthday() throws Exception {
        LocalDate today = LocalDate.now();
        String minimumDOB = today.minusYears(13).toString();
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \""+minimumDOB+"\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
     void whenPostRequestToUsersAndUserInvalidDueToMalformedDateOfBirth_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"10/27/1000\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\"password\":\"a\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetRequestToUserHasCardsExpired_AndUserIsSelf_thenExpiredReturned() throws Exception {
        User currentUser = userService.findUserById(1);
        System.out.println(notificationService.findAllNotificationsByUserId(1));
        mockMvc.perform(MockMvcRequestBuilders.get("/users/notifications")
                .with(user(new CustomUserDetails(currentUser)))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message", is("Your card has expired")));
    }

    void createOneUser(String firstName, String lastName, String email, String dateOfBirth, String homeAddress, String password) {
        String user = String.format("{\"firstName\": \"%s\", \"lastName\" : \"%s\", \"email\": \"%s\", \"dateOfBirth\": \"%s\", \"homeAddress\": %s, \"password\": \"%s\"}", firstName, lastName, email, dateOfBirth, homeAddress, password);

        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/users")
                            .content(user)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated());

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }

}

