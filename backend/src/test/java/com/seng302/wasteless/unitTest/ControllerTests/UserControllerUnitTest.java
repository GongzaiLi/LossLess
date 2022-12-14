package com.seng302.wasteless.unitTest.ControllerTests;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.NotificationService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.testconfigs.MockUserServiceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@Import(MockUserServiceConfig.class)
@AutoConfigureMockMvc(addFilters = false) //Disable spring security for the unit tests
class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AddressService addressService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    private User user;

    private User admin;

    private User defaultAdmin;

    private Address address;

    @BeforeEach void setUp() {

        admin = mock(User.class);
        admin.setId(2);
        admin.setEmail("GAA@gmail.com");
        admin.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);
        admin.setDateOfBirth(LocalDate.now().minusYears(20));
        admin.setPassword("13371777");

        defaultAdmin = mock(User.class);
        defaultAdmin.setId(3);
        defaultAdmin.setEmail("DGAA@gmail.com");
        defaultAdmin.setRole(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN);
        defaultAdmin.setDateOfBirth(LocalDate.now().minusYears(20));
        defaultAdmin.setPassword("13371777");

        user = mock(User.class);
        user.setId(1);
        user.setEmail("james@gmail.com");
        user.setRole(UserRoles.USER);
        user.setDateOfBirth(LocalDate.now().minusYears(20));
        user.setPassword("13371777");

        address = mock(Address.class);
        address.setId(1);
        address.setCity("Auckland");
        address.setStreetNumber("22");
        address.setStreetName("Ilam Road");
        address.setCountry("New Zealand");
        address.setPostcode("3570");

        Notification notification = new Notification();
        notification.setType(NotificationType.EXPIRED);
        notification.setSubjectId("1");
        notification.setMessage("Your card has expired");
        notification.setUserId(1);
        List<Notification> notifs = new ArrayList<>();
        notifs.add(notification);

        Mockito
                .when(notificationService.filterNotifications(anyInt(), any(), any()))
                .thenReturn(notifs);

        // See https://stackoverflow.com/questions/360520/unit-testing-with-spring-security
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(user);

        doReturn(true).when(user).checkUserGlobalAdmin();
        doReturn(UserRoles.USER).when(user).getRole();
        doReturn(LocalDate.now().minusYears(20)).when(user).getDateOfBirth();
        doReturn(true).when(user).checkDateOfBirthValid();
        doReturn(address).when(user).getHomeAddress();
        doReturn("13371777").when(user).getPassword();

        Mockito
                .when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        Mockito
                .when(userService.checkEmailAlreadyUsed(anyString()))
                .thenReturn(false);

        Mockito
                .when(userService.checkEmailValid(anyString()))
                .thenReturn(true);

        Mockito
                .when(userService.checkEmailValid(anyString()))
                .thenReturn(true);

        Mockito
                .when(addressService.createAddress(any(Address.class)))
                .thenReturn(address);

        Mockito
                .when(userService.createUser(any(User.class)))
                .thenReturn(user);

        Mockito
                .when(userService.findUserById(anyInt()))
                .thenReturn(user);

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(user);

        Mockito
                .when(userService.getUserToModify(anyInt()))
                .thenReturn(user);

        Mockito.when(userService.findUserByEmail(anyString()))
                .thenReturn(user);

        doReturn(1).when(user).getId();
        doReturn(2).when(admin).getId();
        doReturn(3).when(defaultAdmin).getId();
        CustomUserDetails customUser = new CustomUserDetails(user);
        Mockito.when(authentication.getPrincipal()).thenReturn(customUser);
    }

    @Test
    void whenPutRequestToUser_andValidRequestWithAllFieldsIdenticalToCurrent_then200Response() throws Exception {

        String modifiedUser = "{\"firstName\": \"James\", " +
                "\"lastName\" : \"Harris\", " +
                "\"email\": \"jeh128@uclive.ac.nz\", " +
                "\"dateOfBirth\": \"2000-10-27\", " +
                "\"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, " +
                "\"password\": \"13371777\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(modifiedUser)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenPutRequestToUser_andValidRequestWithAllFieldsIdenticalToCurrent_andNewPassword_then200Response() throws Exception {

        String modifiedUser = "{\"firstName\": \"James\",\n" +
                "\"lastName\" : \"Harris\",\n" +
                "\"email\": \"jeh128@uclive.ac.nz\",\n" +
                "\"dateOfBirth\": \"2000-10-27\",\n" +
                "\"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\n" +
                "\"password\": \"13371777\",\n" +
                "\"newPassword\": \"1338\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(modifiedUser)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenPutRequestToUser_andEmailAlreadyTaken_then400Response() throws Exception {

        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Attempted to update user with already used email"))
                .when(userService)
                .updateUserEmail(any(User.class), anyString());

        String modifiedUser = "{\"firstName\": \"James\",\n" +
                "\"lastName\" : \"Harris\",\n" +
                "\"email\": \"jeh128@uclive.ac.nz\",\n" +
                "\"dateOfBirth\": \"2000-10-27\",\n" +
                "\"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\n" +
                "\"password\": \"13371777\",\n" +
                "\"newPassword\": \"1338\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(modifiedUser)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void whenPutRequestToUser_andNotTheUserOrDGAA_then403Response() throws Exception {
        Mockito
                .when(userService.getUserToModify(anyInt()))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to make change for this user"));

        String modifiedUser = "{\"firstName\": \"James\",\n" +
                "\"lastName\" : \"Harris\",\n" +
                "\"email\": \"jeh128@uclive.ac.nz\",\n" +
                "\"dateOfBirth\": \"2000-10-27\",\n" +
                "\"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\n" +
                "\"newPassword\": \"1338\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(modifiedUser)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenPutRequestToUser_andRequestToChangePassword_andGAA_then200Response() throws Exception {
        Mockito.when(userService.getCurrentlyLoggedInUser())
                .thenReturn(admin);

        String modifiedUser = "{\"firstName\": \"James\",\n" +
                "\"lastName\" : \"Harris\",\n" +
                "\"email\": \"jeh128@uclive.ac.nz\",\n" +
                "\"dateOfBirth\": \"2000-10-27\",\n" +
                "\"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\n" +
                "\"newPassword\": \"1338\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(modifiedUser)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void whenPutRequestToUser_andRequestToChangePassword_andDGAA_then200Response() throws Exception {
        Mockito.when(userService.getCurrentlyLoggedInUser())
                .thenReturn(defaultAdmin);

        String modifiedUser = "{\"firstName\": \"James\",\n" +
                "\"lastName\" : \"Harris\",\n" +
                "\"email\": \"jeh128@uclive.ac.nz\",\n" +
                "\"dateOfBirth\": \"2000-10-27\",\n" +
                "\"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\n" +
                "\"newPassword\": \"1338\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(modifiedUser)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenPutRequestToUser_andRequestToChangeEmail_andGAA_then200Response() throws Exception {
        Mockito.when(userService.getCurrentlyLoggedInUser())
                .thenReturn(admin);

        String modifiedUser = "{\"firstName\": \"James\",\n" +
                "\"lastName\" : \"Harris\",\n" +
                "\"email\": \"jeh1281@uclive.ac.nz\",\n" +
                "\"dateOfBirth\": \"2000-10-27\",\n" +
                "\"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(modifiedUser)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenPutRequestToUser_andRequestToChangePassword_andNoPasswordField_then400Response() throws Exception {
        String modifiedUser = "{\"firstName\": \"James\",\n" +
                "\"lastName\" : \"Harris\",\n" +
                "\"email\": \"jeh128@uclive.ac.nz\",\n" +
                "\"dateOfBirth\": \"2000-10-27\",\n" +
                "\"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\n" +
                "\"newPassword\": \"1338\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .content(modifiedUser)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPutRequestToUser_andRequestToChangePassword_andCurrentPasswordIncorrect_then400Response() throws Exception {
        String modifiedUser = "{\"firstName\": \"James\",\n" +
                "\"lastName\" : \"Harris\",\n" +
                "\"email\": \"jeh128@uclive.ac.nz\",\n" +
                "\"dateOfBirth\": \"2000-10-27\",\n" +
                "\"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\n" +
                "\"newPassword\": \"1338\"\n" +
                "\"password\": \"INCORRECTPASSWORD!!!!\",\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .content(modifiedUser)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPutRequestToUser_andRequestToChangePassword_andNoPasswordField_butIsDGAA_then200Response() throws Exception {
        Mockito.when(userService.getCurrentlyLoggedInUser())
                .thenReturn(admin);

        String modifiedUser = "{\"firstName\": \"James\",\n" +
                "\"lastName\" : \"Harris\",\n" +
                "\"email\": \"jeh128@uclive.ac.nz\",\n" +
                "\"dateOfBirth\": \"2000-10-27\",\n" +
                "\"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\n" +
                "\"newPassword\": \"1338\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .content(modifiedUser)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenPutRequestToUser_andRequestToChangeEmail_andDGAA_then200Response() throws Exception {
        Mockito.when(userService.getCurrentlyLoggedInUser())
                .thenReturn(defaultAdmin);

        String modifiedUser = "{\"firstName\": \"James\",\n" +
                "\"lastName\" : \"Harris\",\n" +
                "\"email\": \"jeh1281@uclive.ac.nz\",\n" +
                "\"dateOfBirth\": \"2000-10-27\",\n" +
                "\"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(modifiedUser)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenPutRequestToUser_andInvalidEmail_then400Response() throws Exception {

        Mockito
                .when(userService.checkEmailValid(anyString()))
                .thenReturn(false);

        String modifiedUser = "{\"firstName\": \"James\",\n" +
                "\"lastName\" : \"Harris\",\n" +
                "\"email\": \"jeh128@uclive.ac.nz\",\n" +
                "\"dateOfBirth\": \"2000-10-27\",\n" +
                "\"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\n" +
                "\"password\": \"1336\",\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(modifiedUser)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void whenPutRequestToUser_andInvalidDateOfBirth_then400Response() throws Exception {

        doReturn(false).when(user).checkDateOfBirthValid();

        String modifiedUser = "{\"firstName\": \"James\",\n" +
                "\"lastName\" : \"Harris\",\n" +
                "\"email\": \"jeh128@uclive.ac.nz\",\n" +
                "\"dateOfBirth\": \"2000-10-27\",\n" +
                "\"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  },\n" +
                "\"password\": \"1336\",\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(modifiedUser)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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
                "  }, \"password\": \"13312312317\"}";
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
                "  }, \"password\": \"13312312317\"}";
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
                "  }, \"password\": \"13371777\"}";
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
                "  }, \"password\": \"13371777\"}";
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
                "  }, \"password\": \"13371777\"}";
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
                "  }, \"password\": \"13371777\"}";
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
                "  }, \"password\": \"13371777\"}";
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
                "  }: \"236a Blenheim Road\", \"password\": \"13371777\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPostRequestToUsersAndUserInvalidDueToMissingHomeAddress_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"2000-10-27\", \"password\": \"13371777\"}";
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
                "  }", "13312312317");

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
                "  },\"password\": \"13312312317\"}";
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
                "  },\"password\": \"13312312317\"}";
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
                "  },\"password\": \"13312312317\"}";
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
                "  },\"password\":\"13312312317\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetRequestToUserHasCardsExpired_AndUserIsSelf_thenExpiredReturned() throws Exception {
        User currentUser = userService.findUserById(1);

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
