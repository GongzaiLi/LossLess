package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.NotificationController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@WebMvcTest(NotificationController.class)
public class ManageHomeFeedFeature {

    private MockMvc mockMvc;

    private CustomUserDetails userDetails;
    private Notification notificationOne;
    private User user;

    private ResultActions result;

    private Address throwawayAddress;

    private Exception exceptionThrown;
    private final Exception expectedException = new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
            "Notification with given ID does not exist");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private NotificationService notificationService;


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
    public void createEntities() {
        createAddress();
        user = createUser("user@test.com", UserRoles.USER);
    }


    @Given("I am logged in as a user with email {string} and UserId {int}")
    public void iAmLoggedInAsAUserWithEmailAndUserId(String userEmail, Integer userId) {
        User user = userService.findUserByEmail(userEmail);
        Assertions.assertEquals(UserRoles.USER, user.getRole());
        Assertions.assertEquals(userId, user.getId());
    }

    @Given("A Notification exists with id {int}")
    public void aNotificationExistsWithId(Integer int1) {
        if (notificationService.findNotificationById(int1)==null) {
            createNotification(int1, user.getId());
        }
        notificationOne = notificationService.findNotificationById(int1);
        Assertions.assertEquals(int1, notificationOne.getId());
    }

    @When("I delete the notification with id {int}")
    public void iDeleteTheNotificationWithId(Integer int1) throws Exception {
        String url = "/users/notifications/" + int1;
        result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(APPLICATION_JSON)
                .with(user(userDetails))
                .with(csrf()));
    }

    @Then("The users notification with id {int} is deleted")
    public void theUsersNotificationWithIdIsDeleted(Integer int1) throws Exception {
        result.andExpect(status().isOk());
        try {
            notificationService.findNotificationById(int1);
        } catch (Exception e) {
            exceptionThrown = e;
        }
        Assertions.assertEquals(exceptionThrown.getClass(), expectedException.getClass());
        Assertions.assertEquals(exceptionThrown.getMessage(), expectedException.getMessage());
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

    private User createUser(String email, UserRoles role) {
        User newUser = new User();

        if (user == null) {
            newUser.setRole(UserRoles.USER);
            newUser.setEmail("user@test.com");
            newUser.setPassword(new BCryptPasswordEncoder().encode("a"));
            newUser.setDateOfBirth(LocalDate.now().minusYears(17));
            newUser.setBio("Bio");
            newUser.setFirstName("FirstName");
            newUser.setLastName("LastName");
            newUser.setHomeAddress(throwawayAddress);
            newUser.setCreated(LocalDate.now());

            userService.createUser(newUser);
        }

        userDetails = new CustomUserDetails(newUser);
        return newUser;
    }

    private Notification createNotification(Integer id, Integer userId){

        LocalDateTime created = LocalDateTime.of(2021, Month.JANUARY, 1, 0, 0, 0);

        notificationOne = new Notification();
        notificationOne.setId(id);
        notificationOne.setUserId(userId);
        notificationOne.setType(NotificationType.EXPIRED);
        notificationOne.setMessage("some message");
        notificationOne.setSubjectId(1);
        notificationOne.setCreated(created);
        notificationService.createNotification(notificationOne.getUserId(), notificationOne.getSubjectId(),
                notificationOne.getType(), notificationOne.getMessage());

        return notificationOne;
    }

}
