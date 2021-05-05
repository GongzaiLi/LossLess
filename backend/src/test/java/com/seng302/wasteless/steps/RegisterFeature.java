package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.testconfigs.MockUserServiceConfig;
import com.seng302.wasteless.testconfigs.MockitoUserServiceConfig;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
    @When("User tries to create an account with no first name, last name {string}, email {string}, date of birth {string}, country {string},  streetNumber {string},  streetName {string},  city {string},  region {string},  postcode {string} and password {string}")
    public void userTriesToCreateAnAccountWithNoFirstNameLastNameEmailDateOfBirthCountryStreetNumberStreetNameCityRegionPostcodeAndPassword(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9) throws Exception {
        String user = String.format("{\"lastName\" : \"%s\", \"email\": \"%s\", \"dateOfBirth\": \"%s\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"%s\",\n" +
                "    \"streetName\": \"%s\",\n" +
                "    \"city\": \"%s\",\n" +
                "    \"region\": \"%s\",\n" +
                "    \"country\": \"%s\",\n" +
                "    \"postcode\": \"%s\"\n" +
                "  }, \"password\": \"%s\"}", arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);

        result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @When("User tries to create an account with first name {string}, no last name, email {string}, date of birth {string}, home address {string}country{string}New Zealand{string}streetNumber{string}{int}\\/{int}{string}streetName{string}Ilam Road{string}city{string}Christchurch{string}region{string}Canterbury{string}postcode{string}{int}{string} and password {string}")
    public void userTriesToCreateAnAccountWithFirstNameNoLastNameEmailDateOfBirthHomeAddressCountryNewZealandStreetNumberStreetNameIlamRoadCityChristchurchRegionCanterburyPostcodeAndPassword(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, int arg7, int arg8, String arg9, String arg10, String arg11, String arg12, String arg13, String arg14, String arg15, String arg16, int arg17, String arg18, String arg19) {
    }

    @When("User tries to create an account with first name {string}, no last name, email {string}, date of birth {string}, country {string},  streetNumber {string},  streetName {string},  city {string},  region {string},  postcode {string} and password {string}")
    public void userTriesToCreateAnAccountWithFirstNameNoLastNameEmailDateOfBirthCountryStreetNumberStreetNameCityRegionPostcodeAndPassword(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9) throws Exception {
        String user = String.format("{\"lastName\" : \"%s\", \"email\": \"%s\", \"dateOfBirth\": \"%s\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"%s\",\n" +
                "    \"streetName\": \"%s\",\n" +
                "    \"city\": \"%s\",\n" +
                "    \"region\": \"%s\",\n" +
                "    \"country\": \"%s\",\n" +
                "    \"postcode\": \"%s\"\n" +
                "  }, \"password\": \"%s\"}", arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);

        result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
