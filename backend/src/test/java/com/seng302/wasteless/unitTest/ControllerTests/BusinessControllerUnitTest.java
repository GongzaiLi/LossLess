package com.seng302.wasteless.unitTest.ControllerTests;


import com.seng302.wasteless.controller.BusinessController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.*;
import com.seng302.wasteless.testconfigs.MockBusinessServiceConfig;
import com.seng302.wasteless.testconfigs.MockUserServiceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BusinessController.class)
@Import({MockUserServiceConfig.class, MockBusinessServiceConfig.class})
class BusinessControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private BusinessService businessService;

    @MockBean
    private AddressService addressService;

    @MockBean
    private NotificationService notificationService;

    private User user;

    private User admin;

    private User defaultAdmin;

    private Address address;

    private Business business;

    @BeforeEach
    void setUp() {

        admin = mock(User.class);
        admin.setId(2);
        admin.setEmail("GAA@gmail.com");
        admin.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);
        admin.setDateOfBirth(LocalDate.now().minusYears(20));
        admin.setPassword("1337");

        defaultAdmin = mock(User.class);
        defaultAdmin.setId(3);
        defaultAdmin.setEmail("DGAA@gmail.com");
        defaultAdmin.setRole(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN);
        defaultAdmin.setDateOfBirth(LocalDate.now().minusYears(20));
        defaultAdmin.setPassword("1337");

        user = mock(User.class);
        user.setId(1);
        user.setEmail("james@gmail.com");
        user.setRole(UserRoles.USER);
        user.setDateOfBirth(LocalDate.now().minusYears(20));
        user.setPassword("1337");

        address = mock(Address.class);
        address.setId(1);
        address.setCity("Auckland");
        address.setStreetNumber("22");
        address.setStreetName("Ilam Road");
        address.setCountry("New Zealand");
        address.setPostcode("3570");

        business = mock(Business.class);
        business.setId(1);
        business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setCreated(LocalDate.now());

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(user);

        Mockito
                .when(userService.findUserById(anyInt()))
                .thenReturn(user);

        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenReturn(business);

        Mockito
                .when(addressService.createAddress(any(Address.class)))
                .thenReturn(address);

        Mockito
                .when(businessService.createBusiness(any(Business.class)))
                .thenReturn(business);

        Mockito
                .when(userService.checkUserAdminsBusiness(anyInt(), anyInt()))
                .thenReturn(false);

        doReturn(UserRoles.USER).when(user).getRole();
        doReturn(LocalDate.now().minusYears(20)).when(user).getDateOfBirth();
        doReturn(true).when(user).checkDateOfBirthValid();
        doReturn(address).when(user).getHomeAddress();
        doReturn(address).when(business).getAddress();
        doReturn("1337").when(user).getPassword();


        doReturn(1).when(user).getId();
        doReturn(2).when(admin).getId();
        doReturn(3).when(defaultAdmin).getId();
        doReturn(true).when(user).checkIsOverSixteen();
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToBusinessAndValidBusiness_then201Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"}," +
                "\"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                        .with(csrf())
                        .content(business)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToBusiness_andInvalidBusiness_dueToMissingName_then400Response() throws Exception {
        String business = "{\"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                        .with(csrf())
                        .content(business)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToBusiness_andUserUnder16_then400Response() throws Exception {
        doReturn(false).when(user).checkIsOverSixteen();

        String business = "{\"name\": \"James's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"}," +
                "\"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                        .with(csrf())
                        .content(business)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToBusiness_andInvalidBusiness_dueToAddress_then400Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                        .with(csrf())
                        .content(business)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToBusiness_andInvalidBusiness_dueToMissingBusinessType_then400Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                        .with(csrf())
                        .content(business)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToBusiness_andValidBusiness_withMissingDescription_then201Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"businessType\": \"Accommodation and Food Services\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                        .with(csrf())
                        .content(business)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPutRequestToBusinessMakeAdmin_andValidRequest_then200Response() throws Exception {
        String request = "{\"userId\": \"2\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/0/makeAdministrator")
                        .with(csrf())
                        .content(request)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPutRequestToBusinessMakeAdmin_andUserAllowedToMakeRequest_BecauseGlobalApplicationAdmin_then200Response() throws Exception {
        String request = "{\"userId\": \"2\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/0/makeAdministrator")
                        .with(csrf())
                        .content(request)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPutRequestToBusinessMakeAdmin_andUserAllowedToMakeRequest_BecauseDefaultGlobalApplicationAdmin_then200Response() throws Exception {
        String request = "{\"userId\": \"2\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/0/makeAdministrator")
                        .with(csrf())
                        .content(request)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPutRequestToBusiness_andUserMakesCorrectRequest_then200Response() throws Exception {
        String request = "{\"name\": \"John's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"5\",\n" +
                "    \"streetName\": \"Fitz Road\",\n" +
                "    \"suburb\": \"Auckland City\",\n" +
                "    \"city\": \"Auckland\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"America\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"description\": \"Peanuts\", \"businessType\": \"Retail Trade\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1")
                .with(csrf())
                .content(request)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPutRequestToBusiness_andUserRequiredData_then400Response() throws Exception {
        String request = "{\"address\" : {\n" +
                "    \"streetNumber\": \"5\",\n" +
                "    \"streetName\": \"Fitz Road\",\n" +
                "    \"suburb\": \"Auckland City\",\n" +
                "    \"city\": \"Auckland\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"America\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"description\": \"Peanuts\", \"businessType\": \"Retail Trade\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1")
                .with(csrf())
                .content(request)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
