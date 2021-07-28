package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.testconfigs.MockitoUserServiceConfig;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(UserController.class)
@AutoConfigureWebMvc
@Import(MockitoUserServiceConfig.class)
public class RegisterFeature {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    private ResultActions result;

    private User currentExistingUser;

    /**
     * For some reason this works but @Autowired mockMvc doesn't. I have no idea why and I'm too tired to find out.
     */
    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }


    @Given("User is not registered and is on the register page")
    public void userIsNotRegisteredAndIsOnTheRegisterPage() {
    }

    @When("User tries to create an account with no first name, last name {string}, email {string}, date of birth {string}, country {string},  streetNumber {string},  streetName {string},  suburb {string},  city {string},  region {string},  postcode {string} and password {string}")
    public void userTriesToCreateAnAccountWithNoFirstNameLastNameEmailDateOfBirthCountryStreetNumberStreetNameCityRegionPostcodeAndPassword(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9, String arg10) throws Exception {
        String user = String.format("{\"lastName\" : \"%s\", \"email\": \"%s\", \"dateOfBirth\": \"%s\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"%s\",\n" +
                "    \"streetName\": \"%s\",\n" +
                "    \"suburb\": \"%s\",\n" +
                "    \"city\": \"%s\",\n" +
                "    \"region\": \"%s\",\n" +
                "    \"country\": \"%s\",\n" +
                "    \"postcode\": \"%s\"\n" +
                "  }, \"password\": \"%s\"}", arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);

        result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @When("User tries to create an account with first name {string}, no last name, email {string}, date of birth {string}, country {string},  streetNumber {string},  streetName {string},  suburb {string}, city {string},  region {string},  postcode {string} and password {string}")
    public void userTriesToCreateAnAccountWithFirstNameNoLastNameEmailDateOfBirthCountryStreetNumberStreetNameCityRegionPostcodeAndPassword(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9, String arg10) throws Exception {
        String user = String.format("{\"firstName\" : \"%s\", \"email\": \"%s\", \"dateOfBirth\": \"%s\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"%s\",\n" +
                "    \"streetName\": \"%s\",\n" +
                "    \"suburb\": \"%s\",\n" +
                "    \"city\": \"%s\",\n" +
                "    \"region\": \"%s\",\n" +
                "    \"country\": \"%s\",\n" +
                "    \"postcode\": \"%s\"\n" +
                "  }, \"password\": \"%s\"}", arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);

        result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @When("User tries to create an account with first name {string}, last name {string}, no email, date of birth {string}, country {string},  streetNumber {string},  streetName {string},  suburb {string}, city {string},  region {string},  postcode {string} and password {string}")
    public void userTriesToCreateAnAccountWithFirstNameLastNameNoEmailDateOfBirthCountryStreetNumberStreetNameCityRegionPostcodeAndPassword(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9, String arg10) throws Exception {
        String user = String.format("{\"firstName\" : \"%s\", \"lastName\" : \"%s\", \"dateOfBirth\": \"%s\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"%s\",\n" +
                "    \"streetName\": \"%s\",\n" +
                "    \"suburb\": \"%s\",\n" +
                "    \"city\": \"%s\",\n" +
                "    \"region\": \"%s\",\n" +
                "    \"country\": \"%s\",\n" +
                "    \"postcode\": \"%s\"\n" +
                "  }, \"password\": \"%s\"}", arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);

        result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @When("User tries to create an account with first name {string}, last name {string}, email {string}, no date of birth, country {string},  streetNumber {string},  streetName {string},  suburb {string}, city {string},  region {string},  postcode {string} and password {string}")
    public void userTriesToCreateAnAccountWithFirstNameLastNameEmailNoDateOfBirthCountryStreetNumberStreetNameCityRegionPostcodeAndPassword(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9, String arg10) throws Exception {

        String user = String.format("{\"firstName\" : \"%s\", \"lastName\" : \"%s\",\"email\": \"%s\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"%s\",\n" +
                "    \"streetName\": \"%s\",\n" +
                "    \"suburb\": \"%s\",\n" +
                "    \"city\": \"%s\",\n" +
                "    \"region\": \"%s\",\n" +
                "    \"country\": \"%s\",\n" +
                "    \"postcode\": \"%s\"\n" +
                "  }, \"password\": \"%s\"}", arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);

        result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @When("User tries to create an account with first name {string}, last name {string}, email {string}, date of birth {string}, country {string},  streetNumber {string},  streetName {string},  suburb {string}, city {string},  region {string},  postcode {string} and password {string}")
    public void userTriesToCreateAnAccountWithFirstNameLastNameEmailDateOfBirthCountryStreetNumberStreetNameCityRegionPostcodeAndPassword(String firstName, String lastName, String email, String dateOfBirth, String country, String streetNumber, String streetName, String suburb, String city, String region, String postcode, String password) throws Exception {
        String user = String.format("{" +
                "\"firstName\" : \"%s\", " +
                "\"lastName\" : \"%s\", " +
                "\"email\": \"%s\", " +
                "\"dateOfBirth\": \"%s\", " +
                "\"homeAddress\": {\n" +
                "\"streetNumber\": \"%s\",\n" +
                "\"streetName\": \"%s\",\n" +
                "\"suburb\": \"%s\",\n" +
                "\"city\": \"%s\",\n" +
                "\"region\": \"%s\",\n" +
                "\"country\": \"%s\",\n" +
                "\"postcode\": \"%s\"\n" +
                "  }, \"password\": \"%s\"}", firstName, lastName, email, dateOfBirth, streetNumber, streetName, suburb, city, region, country, postcode, password);

        result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }


    @Given("The user with email {string} has an account and user is on register page")
    public void theUserWithEmailHasAnAccountAndUserIsOnRegisterPage(String email) throws Exception {
        String user = String.format("{" +
                "\"firstName\" : \"name\", " +
                "\"lastName\" : \"name\", " +
                "\"email\": \"%s\", " +
                "\"dateOfBirth\": \"1998-04-27\", " +
                "\"homeAddress\": {\n" +
                "\"streetNumber\": \"number\",\n" +
                "\"streetName\": \"name\",\n" +
                "\"suburb\": \"suburb\",\n" +
                "\"city\": \"city\",\n" +
                "\"region\": \"region\",\n" +
                "\"country\": \"country\",\n" +
                "\"postcode\": \"9201\"\n" +
                "  }, \"password\": \"password\"}", email);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON));
    }


    @When("User tries to create an account with first name {string}, last name {string}, email {string}, date of birth {string}, no home address and password {string}")
    public void userTriesToCreateAnAccountWithFirstNameLastNameEmailDateOfBirthNoHomeAddressAndPassword(String arg0, String arg1, String arg2, String arg3, String arg4) throws Exception {

        String user = String.format("{\"firstName\" : \"%s\", \"lastName\" : \"%s\",\"email\": \"%s\", \"dateOfBirth\": \"%s\", \"password\": \"%s\"}", arg0, arg1, arg2, arg3, arg4);

        result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Then("The registering user will receive an error")
    public void theRegisteringUserWillReceiveAnError() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        result.andExpect(status().is4xxClientError());
    }
}
