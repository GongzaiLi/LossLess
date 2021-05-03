package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.testconfigs.MockUserServiceConfig;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@Import(MockUserServiceConfig.class)
public class LoginFeature {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Given("The user with email {string} exists and has password {string}")
    public void the_user_with_email_exists_and_has_password(String email, String password) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("The user with email {string} does not exist")
    public void the_user_with_email_does_not_exist(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("The user logs in with user email {string} and password {string}")
    public void the_user_logs_in_with_user_email_and_password(String email, String password) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The user will receive an error message of {string}")
    public void the_user_will_receive_an_error_message_of(String email) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The user will be logged in as themselves")
    public void the_user_will_be_logged_in_as_themselves() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("The user logs in with user email {string} and without a password")
    public void theUserLogsInWithUserEmailAndWithoutAPassword(String arg0) {
    }

    @When("The user logs in without an email and with password {string}")
    public void theUserLogsInWithoutAnEmailAndWithPassword(String arg0) {
    }
}
