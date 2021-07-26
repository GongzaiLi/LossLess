package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.CardController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.CardExpiryService;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.ElementCollection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@WebMvcTest(CardController.class)
public class CardExpiryFeature {

    private MockMvc mockMvc;

    private CustomUserDetails userDetails;
    private CustomUserDetails adminDetails;
    private Card userCard;
    private Card adminCard;
    private User user;
    private User admin;

    private List<String> keywords;
    private Integer userCardId;
    private Integer adminCardId;


    private ResultActions result;

    private Address throwawayAddress;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CardService cardService;

    @Autowired
    private CardExpiryService cardExpiryService;


    /**
     * Sets up the mockMVC object by building with with webAppContextSetup.
     * We do this manually because @Autowired mockMvc doesn't work.
     */
    @Before
    public void setUpMockMvc() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity()) // This allows us to use .with(user(userDetails)).
                // See https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-mockmvc.html#test-mockmvc-securitycontextholder-rpp

                .build();
    }

    @Transactional
    @Before
    public void createUsers() {
        createAddress();
        createUser();
        createCards();
    }


    @Given("I am logged in as the user {string} with UserId {int}")
    public void iAmLoggedInAsTheUserWithUserId(String userEmail, int userId) {
        User user = userService.findUserByEmail(userEmail);
        Assertions.assertEquals(UserRoles.USER, user.getRole());
        Assertions.assertEquals(userId, user.getId());
    }

    @Transactional
    @And("a Card exists with creatorId {int}")
    public void aCardExistsCardIdWithCreatorId(int creatorId) {
        if (cardService.findById(userCardId)==null){
            userCardId = cardService.createCard(userCard).getId();
        }
        if  (cardService.findById(adminCardId)==null){
            adminCardId = cardService.createCard(adminCard).getId();
        }
        Card card = cardService.findById(userCardId);
        Assertions.assertEquals(creatorId, card.getCreator().getId());
    }

    @When("I delete the card")
    public void iDeleteTheCard() throws Exception {
        String url = "/cards/"+userCardId;
        result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(APPLICATION_JSON)
                .with(user(userDetails))
                .with(csrf()));
    }

    @Then("The users card is deleted")
    public void theUsersCardIsDeleted() throws Exception {
        result.andExpect(status().isOk());
        Card fetchedCard = cardService.findById(userCardId);
        Assertions.assertNull(fetchedCard);
    }




    @And("a Card exists with a different creatorId {int}")
    public void aCardExistsWithADifferentCreatorId(int creatorId) {
        if (cardService.findById(userCardId)==null){
            userCardId = cardService.createCard(userCard).getId();
        }
        if  (cardService.findById(adminCardId)==null){
            adminCardId = cardService.createCard(adminCard).getId();
        }
        Card card = cardService.findById(adminCardId);
        Assertions.assertEquals(creatorId, card.getCreator().getId());
    }


    @When("I delete the other card")
    public void iDeleteTheOtherCard() throws Exception {
        String url = "/cards/"+adminCardId;
        result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(APPLICATION_JSON)
                .with(user(userDetails))
                .with(csrf()));
    }

    @Then("The other card is not deleted")
    public void theOtherCardIsNotDeleted() throws Exception {
        result.andExpect(status().isForbidden());
        Card fetchedCard = cardService.findById(adminCardId);
        Assertions.assertNotNull(fetchedCard);
    }

    @Given("I am logged in as the GAA {string} with UserId {int}")
    public void iAmLoggedInAsTheGAAWithUserId(String userEmail, int userId) {
        User user = userService.findUserByEmail(userEmail);
        Assertions.assertEquals(UserRoles.GLOBAL_APPLICATION_ADMIN, user.getRole());
        Assertions.assertEquals(userId, user.getId());
    }

    @When("I delete the card as admin")
    public void iDeleteTheCardAsAdmin() throws Exception {
        String url = "/cards/"+userCardId;
        result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(APPLICATION_JSON)
                .with(user(adminDetails))
                .with(csrf()));
    }

    private void createAddress(){
        throwawayAddress = new Address();
        throwawayAddress.setCountry("NZ");
        throwawayAddress.setSuburb("Riccarton");
        throwawayAddress.setCity("Christchurch");
        throwawayAddress.setStreetNumber("1");
        throwawayAddress.setStreetName("Ilam Rd");
        throwawayAddress.setPostcode("8041");
        addressService.createAddress(throwawayAddress);
    }

    private void createUser(){
        user = userService.findUserByEmail("user@test.com");
        admin = userService.findUserByEmail("admin@test.com");

        if (user == null) {
            user = new User();
            user.setRole(UserRoles.USER);
            user.setEmail("user@test.com");
            user.setPassword(new BCryptPasswordEncoder().encode("a"));
            user.setDateOfBirth(LocalDate.now().minusYears(17));
            user.setBio("Bio");
            user.setFirstName("FirstName");
            user.setLastName("LastName");
            user.setHomeAddress(throwawayAddress);
            user.setCreated(LocalDate.now());

            userService.createUser(user);
        }
        if (admin == null) {
            admin = new User();
            admin.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);
            admin.setEmail("admin@test.com");
            admin.setPassword(new BCryptPasswordEncoder().encode("a"));
            admin.setDateOfBirth(LocalDate.now().minusYears(17));
            admin.setBio("Bio");
            admin.setFirstName("FirstName");
            admin.setLastName("LastName");
            admin.setHomeAddress(throwawayAddress);
            admin.setCreated(LocalDate.now());

            userService.createUser(admin);
        }

        userDetails = new CustomUserDetails(user);
        adminDetails = new CustomUserDetails(admin);
    }

    private void createCards(){
        keywords = new ArrayList<>();
        keywords.add("Vehicle");

        userCard = new Card();
        userCard.setId(1);
        userCard.setCreator(user);
        userCard.setSection(CardSections.FOR_SALE);
        userCard.setTitle("Sale");
        userCard.setKeywords(keywords);

        cardService.createCard(userCard);

        adminCard = new Card();
        adminCard.setId(2);
        adminCard.setCreator(admin);
        adminCard.setSection(CardSections.FOR_SALE);
        adminCard.setTitle("Sale");
        adminCard.setKeywords(keywords);
        cardService.createCard(adminCard);
    }

    @Given("I created a card that expired over {int} hours ago")
    public void iCreatedACardThatExpiredHoursAgo(int hours) {
        userCard.setDisplayPeriodEnd(LocalDateTime.now().minusHours(hours + 1));
        cardService.createCard(userCard);
    }

    @When("The system checks for any expired cards")
    public void theSystemChecksForAnyExpiredCards() {
        cardExpiryService.scheduleCheckCardExpiry();
    }

    @Then("The card is automatically deleted")
    public void theCardIsAutomaticallyDeleted() {
        Assertions.assertNull(cardService.findById(userCard.getId()));
    }

    @Then("I am notified")
    public void iAmNotified() {
        Assertions.assertTrue(userService.findUserById(user.getId()).getHasCardsDeleted());
    }
}
