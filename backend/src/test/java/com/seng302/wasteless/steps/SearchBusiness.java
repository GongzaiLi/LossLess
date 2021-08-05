package com.seng302.wasteless.steps;


import com.jayway.jsonpath.JsonPath;
import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDate;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(UserController.class)
@AutoConfigureWebMvc
public class SearchBusiness {
    private MockMvc mockMvc;

    private CustomUserDetails currentUserDetails;

    private Address throwawayAddress;

    private ResultActions responseResult;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;



    /**
     * Creates a throwaway address so we can use it for other step definitions.
     * For example, we can reuse this address when creating a new user.
     */
    @Before
    public void setupAddress() {
        throwawayAddress = new Address();
        throwawayAddress.setCountry("NZ");
        throwawayAddress.setSuburb("Riccarton");
        throwawayAddress.setCity("Christchurch");
        throwawayAddress.setStreetNumber("1");
        throwawayAddress.setStreetName("Ilam Rd");
        throwawayAddress.setPostcode("8041");
        addressService.createAddress(throwawayAddress);
    }

    /**
     * Sets up the mockMVC object by building with with webAppContextSetup.
     * We do this manually because @Autowired mockMvc doesn't work.
     */
    @Before
    public void setUpMockMvc() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity()) // This allows us to use .with(user(currentUserDetails)).
                // See https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-mockmvc.html#test-mockmvc-securitycontextholder-rpp
                .build();
    }

    @Given("A user is logged in as the user {string}")
    public void a_user_is_logged_in_as_the_user(String userEmail) {
        User currentUser = userService.findUserByEmail(userEmail);

        if (currentUser == null) {
            currentUser = new User();
            currentUser.setRole(UserRoles.USER);
            currentUser.setEmail(userEmail);
            currentUser.setPassword(new BCryptPasswordEncoder().encode("a"));
            currentUser.setDateOfBirth(LocalDate.now().minusYears(17));
            currentUser.setBio("Bio");
            currentUser.setFirstName("FirstName");
            currentUser.setLastName("LastName");
            currentUser.setHomeAddress(throwawayAddress);
            currentUser.setCreated(LocalDate.now());

            userService.createUser(currentUser);
        }

        currentUserDetails = new CustomUserDetails(currentUser);
    }


    @And("There are {int} businesses in the database with names business {int} through business {int}")
    public void there_are_businesses_in_the_database_with_names_business_through_business(Integer totalBusinesses, Integer startId, Integer endId) {

        while (startId <= endId) {
            createOneBusiness(String.format("business-%s", startId++), "{\n" +
                    "    \"streetNumber\": \"56\",\n" +
                    "    \"streetName\": \"Clyde Road\",\n" +
                    "    \"city\": \"Christchurch\",\n" +
                    "    \"country\": \"New Zealand\",\n" +
                    "    \"postcode\": \"8041\"\n" +
                    "  }", "Non-profit organisation", "I am a business");
        }
    }

    private void createOneBusiness(String name, String address, String businessType, String description) {

        String business = String.format("{\"name\": \"%s\", \"address\" : %s, \"businessType\": \"%s\", \"description\": \"%s\"}", name, address, businessType, description);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                            .content(business)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(user(currentUserDetails))
                            .with(csrf()))
                    .andExpect(status().isCreated());

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }

    // AC2: I can enter an empty as a search term.

    @When("The User searches businesses with an empty: {string} searchQuery with default pagination and sorting")
    public void the_user_searches_businesses_with_an_empty_search_query_with_default_pagination_and_sorting(String query) throws Exception {
        responseResult = mockMvc.perform(MockMvcRequestBuilders.get("/businesses/search?searchQuery=" + query)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .with(user(currentUserDetails)))
                .andExpect(status().isOk());
    }


    @Then("The first {int} matching businesses are returned")
    public void the_first_matching_businesses_are_returned(Integer numOfBusinesses) throws Exception {
        responseResult.andExpect(jsonPath("businesses", hasSize(numOfBusinesses)));
    }

    // AC2: I can enter a full name as a search term.

    @When("The User searches businesses with full name: {string} searchQuery with default pagination and sorting")
    public void the_user_searches_businesses_with_full_name_search_query_with_default_pagination_and_sorting(String query) throws Exception {
        responseResult = mockMvc.perform(MockMvcRequestBuilders.get("/businesses/search?searchQuery=" + query)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .with(user(currentUserDetails)))
                .andExpect(status().isOk());
    }

    @Then("The one matching business with name {string} is returned")
    public void the_one_matching_business_with_name_is_returned(String businessName) throws Exception {
        List<Object> businesses = JsonPath.read(responseResult.andReturn().getResponse().getContentAsString(), "$.businesses");
        Assertions.assertTrue(businesses.stream().allMatch(business -> JsonPath.read(business, "$.name").toString().equals(businessName)));
    }

    // AC2: I can enter a partial name as a search term.

    @When("The User searches businesses with partial name: {string} searchQuery with default pagination and sorting")
    public void the_user_searches_businesses_with_partial_name_search_query_with_default_pagination_and_sorting(String query) throws Exception {
        responseResult = mockMvc.perform(MockMvcRequestBuilders.get("/businesses/search?searchQuery=" + query)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .with(user(currentUserDetails)))
                .andExpect(status().isOk());
    }

    // AC2: I can enter a partial name as a search term and paginate to return 5 results

    @When("The User searches businesses with partial name: {string} searchQuery with size of {int} and default page and sorting")
    public void the_user_searches_businesses_with_partial_name_search_query_with_size_of_and_default_page_and_sorting(String query, Integer size) throws Exception {
        responseResult = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/businesses/search?searchQuery=%s&size=%d", query, size))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .with(user(currentUserDetails)))
                .andExpect(status().isOk());
    }

    // AC2: I can enter a partial name as a search term and paginate to return 5 results on page 2

    @When("The User searches businesses with partial name: {string} searchQuery with size of {int} and page {int} and default sorting")
    public void the_user_searches_businesses_with_partial_name_search_query_with_size_of_and_page_and_default_sorting(String query, Integer size, Integer page) throws Exception {
        responseResult = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/businesses/search?searchQuery=%s&size=%d&page=%d", query, size, page))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .with(user(currentUserDetails)))
                .andExpect(status().isOk());
    }

    @Then("The second {int} matching businesses are returned")
    public void the_second_matching_businesses_are_returned(Integer numOfBusinesses) throws Exception {
        responseResult.andExpect(jsonPath("businesses", hasSize(numOfBusinesses)));
    }

    // AC2: I can enter a partial name as a search term and paginate to return 5 results on page 2 and sort by name asc

    @When("The User searches businesses with partial name: {string} searchQuery with default pagination and sort by {string} sort direction {string}")
    public void the_user_searches_businesses_with_partial_name_search_query_with_default_pagination_and_sort_by_sort_direction(String query, String sort, String sortDirection) throws Exception {
        responseResult = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/businesses/search?searchQuery=%s&sort=%s,%s", query, sort, sortDirection))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .with(user(currentUserDetails)))
                .andExpect(status().isOk());
    }

    @Then("The {int} matching businesses are returned in ascending name order")
    public void the_matching_businesses_are_returned_in_ascending_name_order(Integer size) throws Exception {
        List<Object> businesses = JsonPath.read(responseResult.andReturn().getResponse().getContentAsString(), "$.businesses");
        System.out.println(JsonPath.read(responseResult.andReturn().getResponse().getContentAsString(), "$.businesses").toString());
        for (int i = 0; i <= size; i++) {
            System.out.println(JsonPath.read(businesses.get(1), "$.name").toString());
            Assertions.assertTrue(
                    JsonPath.read(businesses.get(i + 1), "$.name")
                            .toString()
                            .compareTo(JsonPath.read(businesses.get(i), "$.name").toString()) >= 0);
        }
    }

    @Then("The {int} matching businesses are returned in descending name order")
    public void the_matching_businesses_are_returned_in_descending_name_order(Integer size) throws Exception {
        List<Object> businesses = JsonPath.read(responseResult.andReturn().getResponse().getContentAsString(), "$.businesses");
        for (int i = 0; i <= size; i++) {
            Assertions.assertTrue(
                    JsonPath.read(businesses.get(i), "$.name")
                            .toString()
                            .compareTo(JsonPath.read(businesses.get(i + 1), "$.name").toString()) >= 0);
        }
    }


}
