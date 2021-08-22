package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static com.seng302.wasteless.TestUtils.newUserWithEmail;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(UserController.class)
public class CreateCardFeature {

    private MockMvc mockMvc;

    private CustomUserDetails currentUserDetails;

    private ResultActions result;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CardService cardService;

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

    @Given("I am logged in as the user {string}")
    public void iAmLoggedInAsTheUser(String email) {
        User currentUser = userService.findUserByEmail(email);

        if (currentUser == null) {
            currentUser = newUserWithEmail(email);
            addressService.createAddress(currentUser.getHomeAddress());
            userService.createUser(currentUser);
        }

        currentUserDetails = new CustomUserDetails(currentUser);
    }

    @When("I create a card with section {string}, title {string}, keywords {string}, {string}")
    public void iCreateACardWithSectionTitleKeywords(String section, String title, String keyword1, String keyword2) throws Exception {
        String jsonInStringForRequest = String.format("{\"section\": \"%s\", \"title\": \"%s\", \"keywords\": [\"%s\", \"%s\"]}", section, title, keyword1, keyword2);

        result = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/cards"))
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The card is created")
    public void theCardIsCreated() throws Exception {
        result.andExpect(status().isCreated());
    }

    @When("I try to create a card with section {string}, title {string}, keywords {string}")
    public void iTryToCreateACardWithSectionTitleKeywords(String section, String title, String keywords) throws Exception {
        String jsonInStringForRequest = String.format("{\"section\": \"%s\", \"title\": \"%s\", \"keywords\": [\"%s\"]}", section, title, keywords);

        result = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/cards"))
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The card is not created")
    public void theCardIsNotCreated() throws Exception {
        result.andExpect(status().isBadRequest());
    }

    @When("I try to create a card with section {string}, and a long title {string}, keywords {string}")
    public void iTryToCreateACardWithSectionAndALongTitleKeywords(String section, String title, String keywords) throws Exception {
        String jsonInStringForRequest = String.format("{\"section\": \"%s\", \"title\": \"%s\", \"keywords\": [\"%s\"]}", section, title, keywords);

        result = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/cards"))
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @When("I try to create a card with section {string}, title {string}, and too many keywords {string}, {string}, {string}, {string}, {string}, {string}")
    public void iTryToCreateACardWithSectionTitleAndTooManyKeywords(String section, String title, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5, String keyword6) throws Exception {
        String jsonInStringForRequest = String.format("{\"section\": \"%s\", \"title\": \"%s\", \"keywords\": [\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]}", section, title, keyword1, keyword2, keyword3, keyword4, keyword5, keyword6);

        result = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/cards"))
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }
}
