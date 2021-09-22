package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.CardSections;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.seng302.wasteless.TestUtils.newUserWithEmail;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(UserController.class)
public class CreateMessageFeature {

    private MockMvc mockMvc;

    private CustomUserDetails currentUserDetails;

    private ResultActions result;

    private User cardOwner;

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


    @Given("A user {string} has a card with card id {int}, section {string}, title {string}, keywords {string}")
    public void a_user_has_a_card_with_card_id_section_title_keywords(String email, Integer cardId, String section, String title, String keywords) {
        User currentUser = userService.findUserByEmail(email);
        if (currentUser == null) {
            currentUser = newUserWithEmail(email);
            addressService.createAddress(currentUser.getHomeAddress());
            userService.createUser(currentUser);
        }
        cardOwner = currentUser;

        Card card = cardService.findById(cardId);
        if (card == null) {
            card = new Card();
            card.setId(cardId);
        }
        card.setSection(CardSections.fromString(section));
        card.setTitle(title);
        card.setCreator(cardOwner);
        cardService.createCard(card);


    }


    //AC3

    @Given("A user logged in as a user with email {string}")
    public void a_user_logged_in_as_a_user_with_email(String email) {
        User currentUser = userService.findUserByEmail(email);
        if (currentUser == null) {
            currentUser = newUserWithEmail(email);
            addressService.createAddress(currentUser.getHomeAddress());
            userService.createUser(currentUser);
        }
        currentUserDetails = new CustomUserDetails(currentUser);
    }

    @When("the user send a message to the user {string} regarding card with id {int}, with the text {string}")
    public void the_user_send_a_message_to_the_user_regarding_card_with_id_with_the_text(String email, Integer cardId, String message) throws Exception {
        Assertions.assertEquals(cardOwner.getEmail(), email);
        String jsonInStringForRequest = String.format("{\"cardId\": %d, \"receiverId\": %d, \"messageText\": \"%s\"}", cardId, cardOwner.getId(), message);

        result = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/messages"))
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("A message is created")
    public void a_message_is_created() throws Exception {
        result.andExpect(status().isCreated()).andExpect(jsonPath("messageId", is(1)));
    }

}
