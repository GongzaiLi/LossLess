package com.seng302.wasteless.steps;


import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.*;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(UserController.class)
@AutoConfigureWebMvc
public class ProductImagesFeature {

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

    @Autowired
    private ProductService productService;

    private Product product;

    private ResultActions responseResult;

    private Business business;


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

    // Background: As a business administrator, I need to be able to associate images with them.

    /**
     * Creates the user with the given email, then creates sets the currentUserDetails object
     * from that user. The currentUserDetails object should be used when making mockmvc requests, so that
     * you are 'logged in' as that user, and set the user is an administrator for a business
     * See https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-mockmvc.html#test-mockmvc-securitycontextholder-rpp
     *
     * @param user       Email of the user
     * @param businessId Id of the business
     */
    @Given("We are logged in as the user {string}, and The user is an administrator for business {int}")
    public void we_are_logged_in_as_the_user_and_the_user_is_an_administrator_for_business(String user, Integer businessId) {

        User currentUser = userService.findUserByEmail(user);

        if (currentUser == null) {
            currentUser = new User();
            currentUser.setRole(UserRoles.USER);
            currentUser.setEmail(user);
            currentUser.setPassword(new BCryptPasswordEncoder().encode("a"));
            currentUser.setDateOfBirth(LocalDate.now().minusYears(17));
            currentUser.setBio("Bio");
            currentUser.setFirstName("FirstName");
            currentUser.setLastName("LastName");
            currentUser.setHomeAddress(throwawayAddress);
            currentUser.setCreated(LocalDate.now());

            userService.createUser(currentUser);
        }


        try {
            business = businessService.findBusinessById(businessId);
        } catch (ResponseStatusException e) {
            business = new Business();
            business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
            business.setId(businessId);
            business.setAdministrators(Collections.singletonList(currentUser));
            business.setName("Jimmy's clown store");
            business.setAddress(throwawayAddress);
            business.setPrimaryAdministrator(currentUser);

            businessService.createBusiness(business);
        }

        if (!business.checkUserIsAdministrator(currentUser)) {
            currentUser.addPrimaryBusiness(business);
            business.getAdministrators().add(currentUser);
        }

        currentUserDetails = new CustomUserDetails(currentUser);
    }

    /**
     * Creates a product with the given id and name if it does not exist,
     * make with the given email an admin of that business. The user should exist first.
     *
     * @param productId   Id of product
     * @param productName Name of product
     * @param businessId  Id of business
     * @throws ResponseStatusException
     */
    @And("A product with id: {string}, name: {string} exists in the catalogue for business {int}")
    public void a_product_with_id_name_exists_in_the_catalogue_for_business(String productId, String productName, Integer businessId) throws ResponseStatusException {
        try {
            product = productService.findProductById(productId);
        } catch (ResponseStatusException e) {
            product = new Product();
            product.setId(productId);
            product.setName(productName);
            product.setBusinessId(businessId);
            product.setImages(new ArrayList<>());
            product.setName("Blah");
            productService.createProduct(product);
        }

    }


    // AC1 - Upload an image for the product with a wrong type

    @Given("There are {int} images of the product with id: {string}")
    public void there_are_images_of_the_product_with_id(Integer imageSize, String productId) {
        var product = productService.findProductById(productId);
        Assertions.assertEquals(imageSize, product.getImages().size());
    }

    @When("Upload an image with a name: {string} in the product with id: {string}")
    public void upload_an_image_with_a_name_in_the_product_with_id(String imageName, String productId) throws Exception {
        String imageType;
        if (Arrays.asList("png", "jpeg", "jpg", "gif").contains(imageName.split("\\.")[1])) {
            imageType = "image/" + imageName.split("\\.")[1];
        } else {
            imageType = "text/plain";
        }

        File file = new File("src/test/java/com/seng302/wasteless/steps/resources/test.png");
        FileInputStream inputImage = new FileInputStream(file);

        MockMultipartFile image = new MockMultipartFile("filename", imageName, imageType, inputImage);

        responseResult = mockMvc.perform(
                MockMvcRequestBuilders.multipart(String.format("/businesses/%d/products/%s/images", business.getId(), productId))
                        .file(image)
                        .with(user(currentUserDetails))
                        .with(csrf()));

        product = productService.findProductById(productId);
    }

    @Then("The user will be able to see having {int} images from the product.")
    public void the_user_will_be_able_to_see_having_images_from_the_product(Integer imageSize) {
        Assertions.assertEquals(imageSize, product.getImages().size());
    }

    @And("The user will get an error message {string}")
    public void the_user_will_get_an_error_message(String errorMessage) throws Exception {
        responseResult.andExpect(status().isBadRequest());
        responseResult.andExpect(result -> Assertions.assertEquals(errorMessage, result.getResponse().getErrorMessage()));
    }

    // AC1,2,3 - Upload an image and a thumbnail for the product with a correct type

    @Then("The user will be able to see having {int} image with the id {int} from the product.")
    public void the_user_will_be_able_to_see_having_image_with_the_id_from_the_product(Integer imageSize, Integer imageId) {
        Assertions.assertEquals(imageSize, product.getImages().size());
        Assertions.assertTrue(product.getImages().stream().anyMatch(image -> image.getId().equals(imageId)));
    }

    @And("The current primary image is this product's image id: {int}")
    public void the_current_primary_image_is_this_product_s_image_id(Integer primaryImageId) {
        Assertions.assertEquals(product.getPrimaryImage().getId(), primaryImageId);
    }

    // AC1,2,3 - Upload another 4 images and thumbnails of different correct types of the product

    @Given("There is {int} image of the product with id: {string}")
    public void there_is_image_of_the_product_with_id(Integer imageSize, String productId) {
        Assertions.assertEquals(productId, product.getId());
        Assertions.assertEquals(imageSize, product.getImages().size());
    }

    @When("Upload four images with name: {string}, {string}, {string}, {string} in the product with id: {string}")
    public void upload_images_with_name_in_the_product_with_id(String imageName1, String imageName2, String imageName3, String imageName4, String productId) throws Exception {
        Assertions.assertEquals(productId, product.getId());

        String[] imageNames = {imageName1, imageName2, imageName3, imageName4};

        for (String imageName : imageNames) {
            String imageType = "image/" + imageName.split("\\.")[1];
            File file = new File("src/test/java/com/seng302/wasteless/steps/resources/test.png");
            FileInputStream inputImage = new FileInputStream(file);

            MockMultipartFile image = new MockMultipartFile("filename", imageName1, imageType, inputImage);

            responseResult = mockMvc.perform(
                    MockMvcRequestBuilders.multipart(String.format("/businesses/%d/products/%s/images", business.getId(), productId))
                            .file(image)
                            .with(user(currentUserDetails))
                            .with(csrf()));
        }

        product = productService.findProductById(productId);
    }


    @Then("The user will be able to see having {int} images with the id {int}, {int}, {int}, {int} from the product.")
    public void the_user_will_be_able_to_see_having_images_with_the_id_from_the_product(Integer imageSize, Integer imageId1, Integer imageId2, Integer imageId3, Integer imageId4) {
        Assertions.assertEquals(imageSize, product.getImages().size());
        Assertions.assertTrue(product.getImages().stream().anyMatch(image -> image.getId().equals(imageId1)));
        Assertions.assertTrue(product.getImages().stream().anyMatch(image -> image.getId().equals(imageId2)));
        Assertions.assertTrue(product.getImages().stream().anyMatch(image -> image.getId().equals(imageId3)));
        Assertions.assertTrue(product.getImages().stream().anyMatch(image -> image.getId().equals(imageId4)));
    }

    // AC2 - Change the primary image in the product

    @And("The product with id: {string} and the current primary image with id: {int}")
    public void the_product_with_id_and_the_current_primary_image_with_id(String productId, Integer primaryImageId) {
        Assertions.assertEquals(productId, product.getId());
        Assertions.assertEquals(primaryImageId, product.getPrimaryImage().getId());

    }

    @When("Set the image with id: {int} is a new primary image of the product with id: {string}")
    public void set_the_image_with_id_is_a_new_primary_image_of_the_product_with_id(Integer primaryImageId, String productId) throws Exception {
        Assertions.assertEquals(productId, product.getId());
        Assertions.assertNotEquals(product.getPrimaryImage().getId(), primaryImageId);

        mockMvc.perform(MockMvcRequestBuilders.put(String.format("/businesses/%d/products/%s/images/%d/makeprimary", business.getId(), productId, primaryImageId))
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()))
                .andExpect(status().isOk());
        Assertions.assertNotEquals(product.getPrimaryImage().getId(), primaryImageId);
        product = productService.findProductById(productId);

        Assertions.assertEquals(product.getPrimaryImage().getId(), primaryImageId);

    }


    @When("Delete an image with id: {int} in the product with id: {string}")
    public void delete_an_image_with_id_in_the_product_with_id(Integer imageId, String productId) throws Exception {
        Assertions.assertEquals(productId, product.getId());
        Assertions.assertTrue(product.getImages().stream().anyMatch(image -> image.getId().equals(imageId)));

        mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/businesses/%d/products/%s/images/%d", business.getId(), productId, imageId))
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()))
                .andExpect(status().isOk());

        product = productService.findProductById(productId);

        Assertions.assertTrue(product.getImages().stream().noneMatch(image -> image.getId().equals(imageId)));
    }

    // AC3 - A thumbnail of the primary image is created automatically.

    @Then("The thumbnail of the image is created")
    public void theThumbnailOfTheImageIsCreated() throws Exception {
        responseResult
                .andExpect(jsonPath("thumbnailFilename").exists());
    }


    //AC4 - Delete a product's primary image

    @Given("There are {int} images of the product with id: {string}, and the current primary image with id: {int}")
    public void there_are_images_of_the_product_with_id_and_the_current_primary_image_with_id(Integer imageSize, String productId, Integer primaryImageId) {
        Assertions.assertEquals(productId, product.getId());
        Assertions.assertEquals(imageSize, product.getImages().size());
        Assertions.assertEquals(primaryImageId, product.getPrimaryImage().getId());
    }

    //AC4 - Delete all product's image

    @When("Delete all images in the product with id: {string}")
    public void delete_all_images_in_the_product_with_id(String productId) throws Exception {
        Assertions.assertEquals(productId, product.getId());

        for (ProductImage productImage : product.getImages()) {
            Assertions.assertTrue(product.getImages().stream().anyMatch(image -> image.getId().equals(productImage.getId())));
            mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/businesses/%d/products/%s/images/%d", business.getId(), productId, productImage.getId()))
                    .contentType(APPLICATION_JSON)
                    .with(user(currentUserDetails))
                    .with(csrf()))
                    .andExpect(status().isOk());
            product = productService.findProductById(productId);
            Assertions.assertTrue(product.getImages().stream().noneMatch(image -> image.getId().equals(productImage.getId())));
        }

    }


    @Then("The current primary image is this product's image will be empty")
    public void the_current_primary_image_is_this_product_s_image_id() {
        Assertions.assertNull(product.getPrimaryImage());
    }

}
