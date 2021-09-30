package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.NotificationService;
import com.seng302.wasteless.service.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.bs.A;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

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

    private Integer referredCardId;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CardService cardService;

    @Autowired
    private NotificationService notificationService;


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
        referredCardId = cardService.createCard(card).getId();


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
        User currentUser = userService.findUserByEmail(email);
        String jsonInStringForRequest = String.format("{\"cardId\": %d, \"receiverId\": %d, \"messageText\": \"%s\"}", referredCardId, currentUser.getId(), message);
        String jsonInStringForRequest2 = String.format("{\"cardId\": %d, \"receiverId\": %d, \"messageText\": \"%s\"}", cardId, currentUser.getId(), message);
        result = mockMvc.perform(MockMvcRequestBuilders.post("/messages")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("A message is created")
    public void a_message_is_created() throws Exception {
        result.andExpect(status().isCreated());
    }

    @When("the user read messages with card id {int}")
    public void the_user_read_messages_with_card_id(Integer cardId) throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders.get("/messages/" + referredCardId)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The user {string} get all message")
    public void the_user_get_all_message(String email, List<String> messages) throws Exception {
        User currentUser = userService.findUserByEmail(email);
        JSONObject jsonResult = new JSONObject(result.andReturn().getResponse().getContentAsString());
        JSONArray messagesArray = new JSONArray(jsonResult.getString("messages"));
        JSONObject sender = messagesArray.getJSONObject(0);
        Assertions.assertEquals(currentUser.getId().toString(), sender.getString("senderId"));
        Assertions.assertEquals(cardOwner.getId().toString(), sender.getString("receiverId"));
        Assertions.assertEquals(messages.get(0), sender.getString("messageText"));

        JSONObject receiver = messagesArray.getJSONObject(1);
        Assertions.assertEquals(cardOwner.getId().toString(), receiver.getString("senderId"));
        Assertions.assertEquals(currentUser.getId().toString(), receiver.getString("receiverId"));
        Assertions.assertEquals(messages.get(1), receiver.getString("messageText"));



    }

    @Then("The card owner {string} get all message")
    public void the_card_owner_get_all_message(String email, List<String> messages) throws Exception {
        User currentUser = userService.findUserByEmail(email);
        JSONArray allMessages = new JSONArray(result.andReturn().getResponse().getContentAsString());

        JSONObject jsonResult = allMessages.getJSONObject(0);
        JSONArray messagesArray = new JSONArray(jsonResult.getString("messages"));
        JSONObject receiver = messagesArray.getJSONObject(0);
        Assertions.assertEquals(currentUser.getId().toString(), receiver.getString("receiverId"));
        Assertions.assertEquals(messages.get(0), receiver.getString("messageText"));

        JSONObject sender = messagesArray.getJSONObject(1);
        Assertions.assertEquals(currentUser.getId().toString(), sender.getString("senderId"));
        Assertions.assertEquals(messages.get(1), sender.getString("messageText"));
    }

    @Then("The user {string} receives the notification {string}")
    public void theUserReceivesTheNotification(String email, String notificationMessage) {
        User currentUser = userService.findUserByEmail(email);
        List<Notification> notificationList = notificationService.filterNotifications(currentUser.getId(), Optional.ofNullable(null), Optional.ofNullable(null));
        Assertions.assertTrue(notificationList.stream().anyMatch(notify -> notify.getMessage().equals(notificationMessage)));

    }
}
