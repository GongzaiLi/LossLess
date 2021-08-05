package com.seng302.wasteless.integrationTest;

import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.testconfigs.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Remove security
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties"
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Reset JPA between test
class BusinessControllerIntegrationTest {


    String address1 = "{\n" +
            "    \"streetNumber\": \"56\",\n" +
            "    \"streetName\": \"Clyde Road\",\n" +
            "    \"suburb\": \"Riccarton\",\n" +
            "    \"city\": \"Christchurch\",\n" +
            "    \"region\": \"Canterbury\",\n" +
            "    \"country\": \"New Zealand\",\n" +
            "    \"postcode\": \"8041\"\n" +
            "  }";

    String homeAddress = "{\n" +
            "    \"streetNumber\": \"3/24\",\n" +
            "    \"streetName\": \"Ilam Road\",\n" +
            "    \"suburb\": \"Riccarton\",\n" +
            "    \"city\": \"Christchurch\",\n" +
            "    \"region\": \"Canterbury\",\n" +
            "    \"country\": \"New Zealand\",\n" +
            "    \"postcode\": \"90210\"\n" +
            "  }";


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToBusinessAndBusinessExists_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }", "Accommodation and Food Services", "I am a business");

        mockMvc.perform(get("/businesses/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("Business")))
                .andExpect(jsonPath("description", is("I am a business")));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToBusinessAndBusinessNotExists_then406Response() throws Exception {
        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }", "Accommodation and Food Services", "I am a business");

        mockMvc.perform(get("/businesses/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToBusinessAndMultipleBusinessExists_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }", "Accommodation and Food Services", "I am a business");
        createOneBusiness("Business2", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business 2");

        mockMvc.perform(get("/businesses/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Business2")))
                .andExpect(jsonPath("description", is("I am a business 2")));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToBusinessAndMultipleBusinessExists_andNonAdminAccountLoggedIn_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", address1, "Accommodation and Food Services", "I am a business");
        createOneBusiness("Business2", address1, "Non-profit organisation", "I am a business 2");
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");

        mockMvc.perform(get("/businesses/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Business2")))
                .andExpect(jsonPath("description", is("I am a business 2")))
                .andExpect(jsonPath("administrators").doesNotExist())
                .andExpect(jsonPath("primaryAdministratorId").doesNotExist());

    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.GLOBAL_APPLICATION_ADMIN)
    void whenGetRequestToBusinessAndMultipleBusinessExists_andApplicationAdminAccountLoggedIn_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", address1, "Accommodation and Food Services", "I am a business");
        createOneBusiness("Business2", address1, "Non-profit organisation", "I am a business 2");

        mockMvc.perform(get("/businesses/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Business2")))
                .andExpect(jsonPath("description", is("I am a business 2")))
                .andExpect(jsonPath("administrators").exists())
                .andExpect(jsonPath("primaryAdministratorId", is(2)));

    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToBusinessAndMultipleBusinessExists_andBusinessAdminUserLoggedIn_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", address1, "Accommodation and Food Services", "I am a business");
        createOneBusiness("Business2", address1, "Non-profit organisation", "I am a business 2");

        mockMvc.perform(get("/businesses/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Business2")))
                .andExpect(jsonPath("description", is("I am a business 2")))
                .andExpect(jsonPath("administrators").exists())
                .andExpect(jsonPath("primaryAdministratorId", is(2)));


    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenPostRequestToBusiness_andInvalidBusiness_dueToIllegalBusinessType_then400Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"businessType\": \"Oil Company\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                        .content(business)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenPutRequestToBusinessMakeAdmin_andValidRequest_then200Response() throws Exception {

        User user = new User();
        user.setEmail("jabob@gmail.com");
        user.setFirstName("Jacob");
        user.setPassword("Steve");
        user.setLastName("Steve");
        user.setDateOfBirth(LocalDate.now().minusYears(20));
        user.setHomeAddress(new Address()
                .setSuburb("Riccarton")
                .setCity("Thames")
                .setId(1)
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30"));

        userService.createUser(user);

        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        String request = "{\"userId\": \"1\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/makeAdministrator")
                        .content(request)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenPutRequestToBusinessMakeAdmin_andValidRequest_thenUserActuallyUpdated() throws Exception {

        User user = new User();
        user.setEmail("jabob@gmail.com");
        user.setFirstName("Jacob");
        user.setPassword("Steve");
        user.setLastName("Steve");
        user.setDateOfBirth(LocalDate.now().minusYears(20));
        user.setHomeAddress(new Address()
                .setSuburb("Riccarton")
                .setCity("Thames")
                .setId(1)
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30"));

        userService.createUser(user);

        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        String request = "{\"userId\": \"1\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/makeAdministrator")
                        .content(request)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        Business businessAfter = businessService.findBusinessById(1);

        List<User> administrators = businessAfter.getAdministrators();
        assertEquals(2, administrators.get(0).getId());
        assertEquals(1, administrators.get(1).getId());
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenPutRequestToBusinessMakeAdmin_andInvalidRequestBecauseUserAlreadyAdmin_then400Response() throws Exception {


        User user = new User();
        user.setEmail("jabob@gmail.com");
        user.setFirstName("Jacob");
        user.setPassword("Steve");
        user.setLastName("Steve");
        user.setDateOfBirth(LocalDate.now().minusYears(20));
        user.setHomeAddress(new Address()
                .setSuburb("Riccarton")
                .setCity("Thames")
                .setId(1)
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30"));

        userService.createUser(user);

        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        String request = "{\"userId\": \"2\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/makeAdministrator")
                        .content(request)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenPutRequestToBusinessRevokeAdmin_andValidRequest_then200Response() throws Exception {
        User user = new User();
        user.setEmail("jabob@gmail.com");
        user.setFirstName("Jacob");
        user.setPassword("Steve");
        user.setLastName("Steve");
        user.setDateOfBirth(LocalDate.now().minusYears(20));
        user.setHomeAddress(new Address()
                .setSuburb("Riccarton")
                .setCity("Thames")
                .setId(1)
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30"));

        userService.createUser(user);

        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        String request = "{\"userId\": \"1\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/makeAdministrator")
                .content(request)
                .contentType(APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/removeAdministrator")
                        .content(request)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenPutRequestToBusinessRevokeAdmin_andValidRequest_thenBusinessIsUpdated() throws Exception {
        User user = new User();
        user.setEmail("jabob@gmail.com");
        user.setFirstName("Jacob");
        user.setPassword("Steve");
        user.setLastName("Steve");
        user.setDateOfBirth(LocalDate.now().minusYears(20));
        user.setHomeAddress(new Address()
                .setSuburb("Riccarton")
                .setCity("Thames")
                .setId(1)
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30"));

        userService.createUser(user);

        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        String request = "{\"userId\": \"1\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/makeAdministrator")
                .content(request)
                .contentType(APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/removeAdministrator")
                .content(request)
                .contentType(APPLICATION_JSON));

        Business businessAfter = businessService.findBusinessById(1);
        assertFalse(businessAfter.getAdministrators().contains(user));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToSearchBusiness_withSearchQueryIsEmpty_then200Response() throws Exception {
        createOneBusiness("Business1", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business2", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business3", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");


        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/search")
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("businesses[0].name", is("Business1")))
                .andExpect(jsonPath("businesses[1].name", is("Business2")))
                .andExpect(jsonPath("businesses[2].name", is("Business3")))
                .andExpect(jsonPath("businesses", hasSize(3)));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToSearchBusiness_withSearchQueryBusiness1_then200Response() throws Exception {
        createOneBusiness("Business1", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business2", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business3", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");


        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/search?searchQuery=Business1")
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("businesses[0].name", is("Business1")))
                .andExpect(jsonPath("businesses", hasSize(1)));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToSearchBusiness_withSearchQueryIsAAAAAA_then200Response() throws Exception {
        createOneBusiness("Business1", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business2", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business3", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");


        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/search?searchQuery=AAAAAA")
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("businesses", hasSize(0)));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToSearchBusiness_withSearchQueryIsSymbol_then200Response() throws Exception {
        createOneBusiness("Business1", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business2", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business3", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");


        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/search?searchQuery=.*&^%$#$%^&")
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("businesses", hasSize(0)));
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToSearchBusiness_withPageOfZero_andSizeOfOne_then200Response() throws Exception {
        createOneBusiness("Business1", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business2", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business3", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");


        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/search?size=1&page=0")
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("businesses[0].name", is("Business1")))
                .andExpect(jsonPath("businesses", hasSize(1)));
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToSearchBusiness_thenCorrectTotalCount() throws Exception {
        createOneBusiness("Business1", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business2", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business3", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");


        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/search?size=3&page=0")
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("businesses[0].name", is("Business1")))
                .andExpect(jsonPath("businesses", hasSize(3)));
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToSearchBusiness_withPageOfZero_andSizeOfTwo_thenCorrectResultsReturned() throws Exception {
        createOneBusiness("Business1", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business2", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business3", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");


        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/search?size=2&page=0")
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("businesses[0].name", is("Business1")))
                .andExpect(jsonPath("businesses[1].name", is("Business2")))
                .andExpect(jsonPath("businesses", hasSize(2)));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToSearchBusiness_withPageOfZero_andSizeOfZero_then400BadRequest() throws Exception {
        createOneBusiness("Business1", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business2", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("Business3", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");


        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/search?size=0&page=0")
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("businesses[0].name", is("Business1")))
                .andExpect(jsonPath("businesses[1].name", is("Business2")))
                .andExpect(jsonPath("businesses[2].name", is("Business3")))
                .andExpect(jsonPath("businesses", hasSize(3)));
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToSearchBusiness_withSortByNameASC_thenCorrectlySortedResults() throws Exception {
        createOneBusiness("A-Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("B-Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("C-Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");


        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/search?sort=name,ASC")
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("businesses[0].name", is("A-Business")))
                .andExpect(jsonPath("businesses[1].name", is("B-Business")))
                .andExpect(jsonPath("businesses[2].name", is("C-Business")))
                .andExpect(jsonPath("businesses", hasSize(3)));
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToSearchBusiness_withSortByNameDESC_thenCorrectlySortedResults() throws Exception {
        createOneBusiness("A-Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("B-Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneBusiness("C-Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");


        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/search?sort=name,DESC")
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("businesses[0].name", is("C-Business")))
                .andExpect(jsonPath("businesses[1].name", is("B-Business")))
                .andExpect(jsonPath("businesses[2].name", is("A-Business")))
                .andExpect(jsonPath("businesses", hasSize(3)));
    }


    private void createOneBusiness(String name, String address, String businessType, String description) {
        String business = String.format("{\"name\": \"%s\", \"address\" : %s, \"businessType\": \"%s\", \"description\": \"%s\"}", name, address, businessType, description);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                            .content(business)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated());

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }

    private void createOneUser(String firstName, String lastName, String email, String dateOfBirth, String homeAddress, String password) {
        String user = String.format("{\"firstName\": \"%s\", \"lastName\" : \"%s\", \"email\": \"%s\", \"dateOfBirth\": \"%s\", \"homeAddress\": %s, \"password\": \"%s\"}", firstName, lastName, email, dateOfBirth, homeAddress, password);

        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .content(user)
                                    .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }

}
