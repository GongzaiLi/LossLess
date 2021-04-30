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
public class LoginFeature {
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

    @Given("The user with email {string} exists and has password {string}")
    public void the_user_with_email_exists_and_has_password(String email, String password) {
        currentExistingUser = new User();
        currentExistingUser.setEmail(email);
        currentExistingUser.setPassword(new BCryptPasswordEncoder().encode(password));
        currentExistingUser.setFirstName("Blah");
        currentExistingUser.setLastName("Blah");
        currentExistingUser.setRole(UserRoles.USER);
        currentExistingUser.setId(100);
    }

    @Given("The user with email {string} does not exist")
    public void the_user_with_email_does_not_exist(String string) {
        currentExistingUser = null;
    }

    @When("The user logs in with user email {string} and password {string}")
    public void the_user_logs_in_with_user_email_and_password(String email, String password) throws Exception {
        if (currentExistingUser != null) {
            doReturn(currentExistingUser).when(userService).findUserByEmail(currentExistingUser.getEmail());
        }

        String user = String.format("{\"email\": \"%s\", \"password\" : \"%s\"}", email, password);

        result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .content(user)
                .contentType(APPLICATION_JSON));
    }

    @When("The user logs in with user email {string} and without a password")
    public void theUserLogsInWithUserEmailAndWithoutAPassword(String email) throws Exception {
        if (currentExistingUser != null) {
            doReturn(currentExistingUser).when(userService).findUserByEmail(currentExistingUser.getEmail());
        }

        String user = String.format("{\"email\": \"%s\"}", email);

        result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .content(user)
                .contentType(APPLICATION_JSON));
    }

    @When("The user logs in without an email and with password {string}")
    public void theUserLogsInWithoutAnEmailAndWithPassword(String password) throws Exception {
        if (currentExistingUser != null) {
            doReturn(currentExistingUser).when(userService).findUserByEmail(currentExistingUser.getEmail());
        }

        String user = String.format("{\"password\": \"%s\"}", password);

        result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .content(user)
                .contentType(APPLICATION_JSON));
    }

    @Then("The user will receive an error message of {string}")
    public void the_user_will_receive_an_error_message_of(String message) throws Exception {
        // Write code here that turns the phrase above into concrete actions
        result.andExpect(status().is4xxClientError());
        result.andExpect(content().string(message));
    }

    @Then("The user will be logged in as themselves")
    public void the_user_will_be_logged_in_as_themselves() throws Exception {
        result.andExpect(status().isOk());
        result.andExpect(content().string(String.format("{\"userId\":%d}", currentExistingUser.getId())));
    }
}
