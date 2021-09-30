package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static com.seng302.wasteless.TestUtils.newBusinessWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@WebMvcTest(UserController.class)
@AutoConfigureWebMvc
public class BusinessImageFeature {

    private MockMvc mockMvc;

    private CustomUserDetails currentUserDetails;

    private Address throwawayAddress;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private BusinessService businessService;

    private User loginUser;

    private ResultActions responseResult;

    private Image businessImage;

    private Business currentBusiness;



    /**
     * Creates a throwaway address so we can use it for other step definitions.
     * For example, we can reuse this address when creating a new user.
     */
    @Before
    public void setupAddress() {
        throwawayAddress = new Address();
        throwawayAddress.setCountry("NZ");
        throwawayAddress.setSuburb("Riccarton");
        throwawayAddress.setCity("Christchurch");
        throwawayAddress.setStreetNumber("1");
        throwawayAddress.setStreetName("Ilam Rd");
        throwawayAddress.setPostcode("8041");
        addressService.createAddress(throwawayAddress);
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

    @Given("A user exists and is logged in as the user {string} and is an admin of a business called {string}")
    public void a_user_exists_and_is_logged_in_as_the_user_and_is_admin_of_business(String email, String name) {
        User currentUser = userService.findUserByEmail(email);

        if (currentUser == null) {
            currentUser = new User();
            currentUser.setRole(UserRoles.USER);
            currentUser.setEmail(email);
            currentUser.setPassword(new BCryptPasswordEncoder().encode("a"));
            currentUser.setDateOfBirth(LocalDate.now().minusYears(17));
            currentUser.setBio("Bio");
            currentUser.setFirstName("FirstName");
            currentUser.setLastName("LastName");
            currentUser.setHomeAddress(throwawayAddress);
            currentUser.setCreated(LocalDate.now());

            userService.createUser(currentUser);
        }

        currentUserDetails = new CustomUserDetails(currentUser);
        loginUser = currentUser;

        if (currentBusiness == null) {
            currentBusiness = (newBusinessWithName(name));
            addressService.createAddress(currentBusiness.getAddress());
            currentBusiness.setAdministrators(Collections.singletonList(loginUser));
            currentBusiness.setPrimaryAdministrator(loginUser);
            businessService.createBusiness(currentBusiness);
        }

        if (!currentBusiness.checkUserIsAdministrator(loginUser)) {
            loginUser.addPrimaryBusiness(currentBusiness);
            currentBusiness.getAdministrators().add(loginUser);
        }
    }

    //  AC5 - Upload an image for the user with a wrong type

    @Given("There is no image for the business")
    public void there_is_no_image_for_the_business() {
        currentBusiness.setProfileImage(null);
        Assertions.assertNull(currentBusiness.getProfileImage());
    }

    @When("Upload an image to the business with a name: {string}")
    public void upload_an_image_to_the_business_with_a_name(String imageName) throws Exception {

        String imageType;
        if (Arrays.asList("png", "jpeg", "jpg", "gif").contains(imageName.split("\\.")[1])) {
            imageType = "*/" + imageName.split("\\.")[1];
        } else {
            imageType = "text/plain";
        }

        File file = new File("src/test/java/com/seng302/wasteless/steps/resources/test.png");
        FileInputStream inputImage = new FileInputStream(file);

        MockMultipartFile image = new MockMultipartFile("filename", imageName, imageType, inputImage);

        responseResult = mockMvc.perform(
                MockMvcRequestBuilders.multipart(String.format("/businesses/%s/image", currentBusiness.getId()))
                        .file(image)
                        .with(user(currentUserDetails))
                        .with(csrf()));

        currentBusiness = businessService.findBusinessById(currentBusiness.getId());
    }


    @Then("The user will get an error message: {string}")
    public void the_user_will_get_an_error_message(String errorMessage) throws Exception {
        responseResult.andExpect(status().isBadRequest());
        responseResult.andExpect(result -> Assertions.assertEquals(errorMessage, result.getResponse().getErrorMessage()));
        Assertions.assertNull(currentBusiness.getProfileImage());
    }

    // AC5 - Upload an image for the user with a correct type

    @Then("The business will be have their image saved")
    public void the_business_will_have_their_image_saved() throws Exception {
        Assertions.assertNotNull(currentBusiness.getProfileImage());
        responseResult
                .andExpect(jsonPath("fileName").exists());
        responseResult
                .andExpect(jsonPath("thumbnailFilename").exists());
    }

    // AC5 - Upload an image for a user when they already have an image

    @Given("There is an image for the business")
    public void there_is_an_image_for_the_business() {

        Image currentImage = new Image();
        currentImage.setFileName("current");
        currentImage.setId(1);
        currentBusiness.setProfileImage(currentImage);
        Assertions.assertNotNull(currentBusiness.getProfileImage().getId());
        businessImage = currentBusiness.getProfileImage();
    }

    @Then("The business image is updated to the new one")
    public void the_business_image_is_updated_to_the_new_one() throws Exception {

        responseResult
                .andExpect(jsonPath("fileName").exists());
        responseResult
                .andExpect(jsonPath("thumbnailFilename").exists());
        Assertions.assertNotEquals(currentBusiness.getProfileImage().getId(), businessImage.getId());
    }

    @Then("The thumbnail of the business image is created")
    public void the_thumbnail_of_the_business_image_is_created() throws Exception {
        responseResult
                .andExpect(jsonPath("thumbnailFilename").exists());
    }

    @AfterEach
    public void clean_up() throws IOException {
        FileUtils.deleteDirectory(new File("./media"));
    }
}
