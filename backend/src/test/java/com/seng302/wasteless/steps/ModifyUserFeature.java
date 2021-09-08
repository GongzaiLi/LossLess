package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.CardController;
import com.seng302.wasteless.controller.ListingController;
import com.seng302.wasteless.model.Notification;
import com.seng302.wasteless.model.NotificationType;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.seng302.wasteless.TestUtils.newUserWithEmail;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(ListingController.class)
@AutoConfigureWebMvc
public class ModifyUserFeature {

    private MockMvc mockMvc;

    private final BCryptPasswordEncoder passwordEncoder;

    private CustomUserDetails currentUserDetails;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private NotificationService notificationService;

    private ResultActions responseResult;

    private int currentUserId;

    public ModifyUserFeature(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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

    @Given("I am logged in as a user with the email {string}")
    public void iAmLoggedInAsAUserWithTheEmail(String email) {
        User currentUser = userService.findUserByEmail(email);

        if (currentUser == null) {
            currentUser = newUserWithEmail(email);
            addressService.createAddress(currentUser.getHomeAddress());
            userService.createUser(currentUser);
            currentUser.setPassword(passwordEncoder.encode("demo"));
            userService.saveUserChanges(currentUser);
        }

        currentUserId = currentUser.getId();
        currentUserDetails = new CustomUserDetails(currentUser);
    }

    @When("The User modifies his profile with firstName: {string}, lastName: {string}, date of Birth: {string}, password: {string}, newPassword: {string}, middleName: {string}, nickname {string}, bio: {string}, phoneNumber: {string}, email {string}, country {string},  streetNumber {string},  streetName {string},  suburb {string}, city {string},  region {string},  postcode {string}")
    public void theUserModifiesHisProfileWithFirstNameLastNameDateOfBirthPasswordNewPasswordMiddleNameNicknameBioPhoneNumberEmailCountryStreetNumberStreetNameSuburbCityRegionPostcode(String firstName, String lastName, String dateOfBirth, String password, String newPassword, String middleName, String nickname, String bio, String phoneNumber, String email, String country, String streetNumber, String streetName, String suburb, String city, String region, String postcode) throws Exception {
        String jsonInStringForRequest = String.format("{\"firstName\": \"%s\", \"lastName\": \"%s\", \"dateOfBirth\": \"%s\", \"password\": \"%s\", \"newPassword\": \"%s\", \"middleName\": \"%s\", \"nickname\": \"%s\", \"bio\": \"%s\", \"phoneNumber\": \"%s\", \"email\": \"%s\", " +
                        "\"homeAddress\": {\n \"country\": \"%s\", \"streetNumber\": \"%s\", \"streetName\": \"%s\", \"suburb\": \"%s\", \"city\": \"%s\", \"region\": \"%s\", \"postcode\": \"%s\"}}",
                firstName, lastName, dateOfBirth, password, newPassword, middleName, nickname, bio, phoneNumber, email, country, streetNumber, streetName, suburb, city, region, postcode);
        responseResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/" + currentUserId)
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The User is modified with an ok result")
    public void theUserIsModifiedWithAnOkResult() throws Exception {
        responseResult.andExpect(status().isOk());
    }


    @When("The User modifies his profile with the date of birth: {string}")
    public void theUserModifiesHisProfileWithTheDateOfBirth(String dateOfBirth) throws Exception {
        String jsonInStringForRequest = String.format("{\"firstName\": \"%s\", \"lastName\": \"%s\", \"dateOfBirth\": \"%s\", \"email\": \"%s\", " +
                        "\"homeAddress\": {\n \"country\": \"%s\", \"streetNumber\": \"%s\", \"streetName\": \"%s\", \"suburb\": \"%s\", \"city\": \"%s\", \"region\": \"%s\", \"postcode\": \"%s\"}}",
                "John", "Smith", dateOfBirth, "c@c", "country", "streetNumber", "streetName", "suburb", "city", "region", "postcode");
        responseResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/" + currentUserId)
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The User who is modifying will receive an error")
    public void theUserWhoIsModifyingWillReceiveAnError() throws Exception {
        responseResult.andExpect(status().isBadRequest());
    }

    @When("The User modifies his profile with the email: {string}")
    public void theUserModifiesHisProfileWithTheEmail(String email) throws Exception {
        String jsonInStringForRequest = String.format("{\"firstName\": \"%s\", \"lastName\": \"%s\", \"dateOfBirth\": \"%s\", \"email\": \"%s\", " +
                        "\"homeAddress\": {\n \"country\": \"%s\", \"streetNumber\": \"%s\", \"streetName\": \"%s\", \"suburb\": \"%s\", \"city\": \"%s\", \"region\": \"%s\", \"postcode\": \"%s\"}}",
                "John", "Smith", "1999-04-27", email, "country", "streetNumber", "streetName", "suburb", "city", "region", "postcode");
        responseResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @When("The User modifies his profile with the firstname: {string}")
    public void theUserModifiesHisProfileWithTheFirstname(String firstname) throws Exception {
        String jsonInStringForRequest = String.format("{\"firstName\": \"%s\", \"lastName\": \"%s\", \"dateOfBirth\": \"%s\", \"email\": \"%s\", " +
                        "\"homeAddress\": {\n \"country\": \"%s\", \"streetNumber\": \"%s\", \"streetName\": \"%s\", \"suburb\": \"%s\", \"city\": \"%s\", \"region\": \"%s\", \"postcode\": \"%s\"}}",
                firstname, "Smith", "1999-04-27", "c@c", "country", "streetNumber", "streetName", "suburb", "city", "region", "postcode");
        responseResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @When("The User modifies his profile with the lastname: {string}")
    public void theUserModifiesHisProfileWithTheLastname(String lastname) throws Exception {
        String jsonInStringForRequest = String.format("{\"firstName\": \"%s\", \"lastName\": \"%s\", \"dateOfBirth\": \"%s\", \"email\": \"%s\", " +
                        "\"homeAddress\": {\n \"country\": \"%s\", \"streetNumber\": \"%s\", \"streetName\": \"%s\", \"suburb\": \"%s\", \"city\": \"%s\", \"region\": \"%s\", \"postcode\": \"%s\"}}",
                "John", lastname, "1999-04-27", "c@c", "country", "streetNumber", "streetName", "suburb", "city", "region", "postcode");
        responseResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/2")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @When("The User modifies his profile with the home address: {string}")
    public void theUserModifiesHisProfileWithTheHomeAddress(String homeaddress) throws Exception {
        String jsonInStringForRequest = String.format("{\"firstName\": \"%s\", \"lastName\": \"%s\", \"dateOfBirth\": \"%s\", \"email\": \"%s\", \"homeAddress\": \"%s\"}",
                "John", "Smith", "1999-04-27", "c@c", homeaddress);
        responseResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/2")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @When("The User modifies his password to {string}")
    public void theUserModifiesHisPasswordTo(String newPassword) throws Exception {
        String jsonInStringForRequest = String.format("{\"firstName\": \"%s\", \"lastName\": \"%s\", \"dateOfBirth\": \"%s\", \"email\": \"%s\", " +
                        "\"homeAddress\": {\n \"country\": \"%s\", \"streetNumber\": \"%s\", \"streetName\": \"%s\", \"suburb\": \"%s\", \"city\": \"%s\", \"region\": \"%s\", \"postcode\": \"%s\"}, " +
                        "\"password\": \"%s\", \"newPassword\": \"%s\", \"confirmPassword\": \"%s\"}",
                "John", "Smith", "1999-04-27", "c@c", "country", "streetNumber", "streetName", "suburb", "city", "region", "postcode", "wrong", newPassword, newPassword);
        responseResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/" + currentUserId)
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @When("The User modifies his email to {string}")
    public void theUserModifiesHisEmailTo(String email) throws Exception {
        String jsonInStringForRequest = String.format("{\"firstName\": \"%s\", \"lastName\": \"%s\", \"dateOfBirth\": \"%s\", \"email\": \"%s\", " +
                        "\"homeAddress\": {\n \"country\": \"%s\", \"streetNumber\": \"%s\", \"streetName\": \"%s\", \"suburb\": \"%s\", \"city\": \"%s\", \"region\": \"%s\", \"postcode\": \"%s\"}, \"password\": \"%s\"}",
                "John", "Smith", "1999-04-27", email, "country", "streetNumber", "streetName", "suburb", "city", "region", "postcode", "2145");
        responseResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/" + currentUserId)
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The User who is modifying will receive an email taken error")
    public void theUserWhoIsModifyingWillReceiveAnEmailTakenError() throws Exception {
        responseResult.andExpect(status().isConflict());
    }

    @Given("A user exists with the email {string}")
    public void aUserExistsWithTheEmail(String email) {
        User newUser = userService.findUserByEmail(email);
        if (newUser == null) {
            newUser = newUserWithEmail(email);
            addressService.createAddress(newUser.getHomeAddress());
            userService.createUser(newUser);
        }
    }

    @When("The User modifies his profile with the country: {string}")
    public void theUserModifiesHisProfileWithTheCountry(String newCountry) throws Exception {
        String jsonInStringForRequest = String.format("{\"firstName\": \"%s\", \"lastName\": \"%s\", \"dateOfBirth\": \"%s\", \"email\": \"%s\", " +
                        "\"homeAddress\": {\n \"country\": \"%s\", \"streetNumber\": \"%s\", \"streetName\": \"%s\", \"suburb\": \"%s\", \"city\": \"%s\", \"region\": \"%s\", \"postcode\": \"%s\"}}",
                "John", "Smith", "1999-04-27", "c@c", newCountry, "streetNumber", "streetName", "suburb", "city", "region", "postcode");
        responseResult = mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The User who is modifying will have a notification saved")
    public void theUserWhoIsModifyingWillHaveANotificationSaved() throws Exception {
        responseResult.andExpect(status().isOk());
        List<Notification> notificationList = notificationService.findAllNotificationsByUserId(currentUserDetails.getId());
        Assertions.assertEquals(1, notificationList.size());
        Assertions.assertEquals(NotificationType.CURRENCY_CHANGE, notificationList.get(0).getType());

    }
}

