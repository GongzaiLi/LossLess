package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.ListingController;
import com.seng302.wasteless.model.Notification;
import com.seng302.wasteless.model.NotificationType;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import static com.seng302.wasteless.TestUtils.newUserWithEmail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(ListingController.class)
@AutoConfigureWebMvc
public class ManageMyFeedFeature {

    private MockMvc mockMvc;

    private User currentUser;

    private CustomUserDetails currentUserDetails;

    private Notification notification;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private NotificationService notificationService;

    private ResultActions responseResult;

    private Exception exceptionThrown;

    private final Exception expectedException = new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No notification exists with the given ID");

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

    @Given("We are logged in as a person with email {string}")
    public void weAreLoggedInAsAPersonWithEmail(String email) {
        currentUser = userService.findUserByEmail(email);

        if (currentUser == null) {
            currentUser = newUserWithEmail(email);
            addressService.createAddress(currentUser.getHomeAddress());
            userService.createUser(currentUser);
        }

        currentUserDetails = new CustomUserDetails(currentUser);
    }

    @And("We have a notification")
    public void weHaveANotification() {
        notification = NotificationService.createNotification(currentUser.getId(), 1, NotificationType.LIKEDLISTING, "");
        notificationService.saveNotification(notification);
    }

    @Given("My notification has not been read")
    public void myNotificationHasNotBeenRead() {
        notification.setRead(false);
        notificationService.saveNotification(notification);
    }

    @When("I delete the notification with id {int}")
    public void iDeleteTheNotificationWithId(Integer int1) throws Exception {
        notification.setId(int1);
        notificationService.saveNotification(notification);
        String url = "/notifications/" + int1;
        responseResult = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The users notification with id {int} is deleted")
    public void theUsersNotificationWithIdIsDeleted(Integer int1) throws Exception {
        responseResult.andExpect(status().isOk());
        try {
            notificationService.findNotificationById(int1);
        } catch (Exception e) {
            exceptionThrown = e;
        }
        Assertions.assertEquals(exceptionThrown.getClass(), expectedException.getClass());
        Assertions.assertEquals(exceptionThrown.getMessage(), expectedException.getMessage());
    }

    @When("I mark it as read")
    public void iMarkItAsRead() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/" + notification.getId())
                .content("{\"read\": true}")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(currentUserDetails)))
                .andExpect(status().isOk());
    }

    @Then("The notification appears as read")
    public void theNotificationAppearsAsRead() throws Exception {
        getNotifications().andExpect(
            jsonPath(String.format("$[?(@.id==%d && @.read==true)]", notification.getId()))   // This JSONPath filters the notifs for only those with id equal to our notif's id and .read property equal to true
            .isNotEmpty());
    }

    @Given("My notification had not been starred and is not the newest notification")
    public void myNotificationHadNotBeenStarred() {
        notification.setStarred(false);
        notificationService.saveNotification(notification);

        // Create newer notification
        notificationService.saveNotification(
                NotificationService.createNotification(currentUser.getId(), 1, NotificationType.LIKEDLISTING, "")
        );
    }

    @When("I star it")
    public void iStarIt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/" + notification.getId())
                .content("{\"starred\": true}")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(currentUserDetails)))
                .andExpect(status().isOk());
    }

    @Then("The starred notification is at the top of my feed")
    public void theNotificationAppearsAtTheTopOfMyFeed() throws Exception {
        getNotifications()
            .andExpect(jsonPath("$[0].id").value(notification.getId()))
            .andExpect(jsonPath("$[0].starred").value(true));
    }

    @Given("My notification has been starred")
    public void myNotificationHasBeenStarred() throws Exception {
        iStarIt();
    }

    @When("A new notification arrives")
    public void aNewNotificationArrives() {
        notificationService.saveNotification(
                NotificationService.createNotification(currentUser.getId(), 1, NotificationType.LIKEDLISTING, "")
        );
    }

    @Given("My notification had not been archived")
    public void myNotificationHadNotBeenArchived() {
        notification.setArchived(false);
        notificationService.saveNotification(notification);
    }

    @When("I archive it")
    public void iArchiveIt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/" + notification.getId())
                .content("{\"archived\": true}")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(currentUserDetails)))
                .andExpect(status().isOk());
    }

    @Then("The notification no longer appears in my feed")
    public void theNotificationNoLongerAppearsInMyFeed() throws Exception {
        getNotifications().andExpect(
            jsonPath(String.format("$[?(@.id==%d)]", notification.getId()))
            .isEmpty());
    }

    private ResultActions getNotifications() throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get("/users/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(currentUserDetails)))
                .andExpect(status().isOk());
    }
}
