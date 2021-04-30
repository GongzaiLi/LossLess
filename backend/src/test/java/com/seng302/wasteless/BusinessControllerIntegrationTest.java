package com.seng302.wasteless;

import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.testconfigs.WithMockCustomUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Remove security
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties"
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Reset JPA between test
public class BusinessControllerIntegrationTest {

    private static final Logger logger = LogManager.getLogger(MainApplicationRunner.class.getName());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ProductService productService;

    String address1 = "{\n" +
            "    \"streetNumber\": \"56\",\n" +
            "    \"streetName\": \"Clyde Road\",\n" +
            "    \"city\": \"Christchurch\",\n" +
            "    \"region\": \"Canterbury\",\n" +
            "    \"country\": \"New Zealand\",\n" +
            "    \"postcode\": \"8041\"\n" +
            "  }";

    String homeAddress = "{\n" +
            "    \"streetNumber\": \"3/24\",\n" +
            "    \"streetName\": \"Ilam Road\",\n" +
            "    \"city\": \"Christchurch\",\n" +
            "    \"region\": \"Canterbury\",\n" +
            "    \"country\": \"New Zealand\",\n" +
            "    \"postcode\": \"90210\"\n" +
            "  }";

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessAndBusinessExists_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", homeAddress, "Accommodation and Food Services", "I am a business");

        mockMvc.perform(get("/businesses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("Business")))
                .andExpect(jsonPath("description", is("I am a business")));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessAndBusinessNotExists_then406Response() throws Exception {
        createOneBusiness("Business", homeAddress, "Accommodation and Food Services", "I am a business");

        mockMvc.perform(get("/businesses/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessAndMultipleBusinessExists_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", homeAddress, "Accommodation and Food Services", "I am a business");
        createOneBusiness("Business2", address1, "Non-profit organisation", "I am a business 2");

        mockMvc.perform(get("/businesses/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Business2")))
                .andExpect(jsonPath("description", is("I am a business 2")));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessAndMultipleBusinessExists_andNonAdminAccountLoggedIn_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", address1, "Accommodation and Food Services", "I am a business");
        createOneBusiness("Business2", address1, "Non-profit organisation", "I am a business 2");
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");

        mockMvc.perform(get("/businesses/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Business2")))
                .andExpect(jsonPath("description", is("I am a business 2")))
                .andExpect(jsonPath("administrators").doesNotExist())
                .andExpect(jsonPath("primaryAdministratorId").doesNotExist());

    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.GLOBAL_APPLICATION_ADMIN)
    public void whenGetRequestToBusinessAndMultipleBusinessExists_andApplicationAdminAccountLoggedIn_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", address1, "Accommodation and Food Services", "I am a business");
        createOneBusiness("Business2", address1, "Non-profit organisation", "I am a business 2");

        mockMvc.perform(get("/businesses/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Business2")))
                .andExpect(jsonPath("description", is("I am a business 2")))
                .andExpect(jsonPath("administrators").exists())
                .andExpect(jsonPath("primaryAdministratorId",is(2)));

    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessAndMultipleBusinessExists_andBusinessAdminUserLoggedIn_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", address1, "Accommodation and Food Services", "I am a business");
        createOneBusiness("Business2", address1, "Non-profit organisation", "I am a business 2");

        mockMvc.perform(get("/businesses/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Business2")))
                .andExpect(jsonPath("description", is("I am a business 2")))
                .andExpect(jsonPath("administrators").exists())
                .andExpect(jsonPath("primaryAdministratorId", is(2)));


    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPostRequestToBusiness_andInvalidBusiness_dueToIllegalBusinessType_then400Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"businessType\": \"Oil Company\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private void createOneBusiness(String name, String address, String businessType, String description) {
        String business = String.format("{\"name\": \"%s\", \"address\" : %s, \"businessType\": \"%s\", \"description\": \"%s\"}", name, address, businessType, description);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                    .content(business)
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated());

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }

    private void createOneUser(String firstName, String lastName, String email, String dateOfBirth, String homeAddress, String password) {
        String user = String.format("{\"firstName\": \"%s\", \"lastName\" : \"%s\", \"email\": \"%s\", \"dateOfBirth\": \"%s\", \"homeAddress\": %s, \"password\": \"%s\"}", firstName, lastName, email, dateOfBirth, homeAddress, password);

        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/users")
                            .content(user)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }

    private void createOneProduct(String id, String name, String description, String manufacturer, String recommendedRetailPrice) {

        String product = String.format("{\"id\" : \"%s\", \"name\": \"%s\", \"description\" : \"%s\", \"manufacturer\" : \"%s\", \"recommendedRetailPrice\": \"%s\"}", id, name, description, manufacturer, recommendedRetailPrice);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
                    .content(product)
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated());

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPostRequestToBusinessProducts_AndBusinessNotExists_then406Response() throws Exception {

        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/99/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPostRequestToBusinessProducts_AndNotAdminOfBusinessOrGlobalAdmin_then403Response() throws Exception {

        Business business = new Business();

        business.setName("New Business");
        business.setAddress(new Address()
                .setCity("Thames")
                .setId(1)
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30"));
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);

        businessService.createBusiness(business);

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPostRequestToBusinessProducts_AndProductAlreadyExists_then400Response() throws Exception {

        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");
        createOneProduct("PRODUCT1", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");


        String product = "{\"id\": \"PRODUCT1\", \"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPostRequestToBusinessProducts_AndUserIsBusinessAdminAndProductIsValid_then201Response() throws Exception {

        createOneBusiness("Business2", address1, "Non-profit organisation", "I am a business 2");

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT1\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.GLOBAL_APPLICATION_ADMIN)
    public void whenPostRequestToBusinessProducts_AndUserIsGlobalAdminButNotBusinessAdminAndProductIsValid_then201Response() throws Exception {

        Business business = new Business();

        business.setName("New Business");
        business.setAddress(new Address()
                .setCity("Thames")
                .setId(1)
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30"));
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);

        businessService.createBusiness(business);

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT1\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessProducts_AndBusinessNotExists_then406Response() throws Exception {

        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");

        createOneProduct("PRODUCT1", "Chocolate Bar", "Example Product First Edition","example manufacturer", "4.0");

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/99/products")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessProducts_AndNotAdminOfBusinessOrGlobalAdmin_then403Response() throws Exception {

        Business business = new Business();

        business.setName("New Business");
        business.setAddress(new Address()
                .setCity("Thames")
                .setId(1)
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30"));
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);

        businessService.createBusiness(business);

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessProducts_AndUserIsBusinessAdminAndProductsExist_then200Response() throws Exception {

        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");

        createOneProduct("PRODUCT1", "Chocolate Bar", "Example Product First Edition","example manufacturer", "4.0");

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("Chocolate Bar")))
                .andExpect(jsonPath("$[0].description", is("Example Product First Edition")));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.GLOBAL_APPLICATION_ADMIN)
    public void whenGetRequestToBusinessProducts_AndUserIsGlobalAdminButNotBusinessAdminAndProductsExist_then200Response() throws Exception {

        Business business = new Business();

        business.setName("New Business");
        business.setAddress(new Address()
                .setCity("Thames")
                .setId(1)
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30"));
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);

        businessService.createBusiness(business);

        createOneProduct("PRODUCT1", "Chocolate Bar", "Example Product First Edition","example manufacturer", "4.0");
        createOneProduct("PRODUCT2", "Juice", "Example Product Second Edition","example manufacturer", "2.0");

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("Chocolate Bar")))
                .andExpect(jsonPath("$[0].description", is("Example Product First Edition")))
                .andExpect(jsonPath("$[1].name", is("Juice")))
                .andExpect(jsonPath("$[1].description", is("Example Product Second Edition")));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessProducts_AndUserIsBusinessAdminAndNoProductsExist_then200Response() throws Exception {

        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndBusinessNotExists_then403Response() throws Exception {

        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT1\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/99/products/1")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndNotAdminToBusiness_then403Response() throws Exception {

        Business business = new Business();

        business.setName("New Business");
        business.setAddress(new Address()
                .setCity("Thames")
                .setId(1)
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30"));
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);

        businessService.createBusiness(business);

        Product product = new Product();

        product.setName("Kit Kat");
        product.setId("1");
        product.setBusinessId(business.getId());

        productService.createProduct(product);

        String editProduct = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT1\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndSuccess_then200Response() throws Exception {
        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");

        createOneProduct("chocolate bar", "Chocolate Bar", "Example Product First Edition","example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndNameIsTheSame_then200Response() throws Exception {
        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");

        createOneProduct("chocolate bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"chocolate bar\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndNameChanges_thenProductCodeChange_thenPutRequestAgainChangedProductCode_then200Response() throws Exception {
        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");
        createOneProduct("chocolate bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON));

        String editProduct2 = "{\"name\": \"Raisin\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Raisin\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-KIT-KAT")
                .content(editProduct2)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndNameChanges_thenProductCodeChange_thenPutRequestAgainOnPastCode_then400Response() throws Exception {
        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");
        createOneProduct("chocolate bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON));

        String editProduct2 = "{\"name\": \"Raisin\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Raisin\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct2)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.GLOBAL_APPLICATION_ADMIN)
    public void whenPutRequestToBusinessProducts_AndIsNotAdminToBusiness_ButIsGlobalAdmin_then200Response() throws Exception {
        Business business = new Business();
        business.setName("New Business");
        business.setAddress(new Address()
                .setCity("Thames")
                .setId(1)
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30"));
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);
        businessService.createBusiness(business);

        Product product = new Product();
        product.setName("Kit Kat");
        product.setId("1");
        product.setBusinessId(business.getId());
        productService.createProduct(product);

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
    public void whenPutRequestToBusinessProducts_AndIsNotAdminToBusiness_ButIsDGAA_then200Response() throws Exception {
        Business business = new Business();
        business.setName("New Business");
        business.setAddress(new Address()
                .setCity("Thames")
                .setId(1)
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30"));
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);
        businessService.createBusiness(business);

        Product product = new Product();
        product.setName("Kit Kat");
        product.setId("1");
        product.setBusinessId(business.getId());
        productService.createProduct(product);

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndNameMissing_then400Response() throws Exception {
        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");

        createOneProduct("PRODUCT1", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");


        String editProduct = "{\"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT1\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndNameIsBlank_then400Response() throws Exception {
        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");
        createOneProduct("PRODUCT1", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT1\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndRecommendedRetailPriceIsLetter_then400Response() throws Exception {
        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");
        createOneProduct("PRODUCT1", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"AB\", \"id\": \"PRODUCT1\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndSuccess_AndAllDataUpdates_thenAllChangesShouldBeMade() throws Exception {
        createOneBusiness("Business", address1, "Non-profit organisation", "I am a business");
        createOneProduct("chocolate bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is("1-KIT-KAT")))
                .andExpect(jsonPath("$[0].name", is("Kit Kat")))
                .andExpect(jsonPath("$[0].description", is("Example Product")))
                .andExpect(jsonPath("$[0].recommendedRetailPrice", is(2.0)));
    }


}
