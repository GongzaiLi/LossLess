package com.seng302.wasteless.integrationTest;


import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.testconfigs.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Remove security
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties"
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Reset JPA between test
 class CatalogueControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    private User user;


    @BeforeEach
    void setup() {
       user = mock(User.class);
       user.setId(1);
       user.setEmail("james@gmail.com");
       user.setRole(UserRoles.USER);

    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
     void whenPostRequestToBusinessProducts_AndBusinessNotExists_then406Response() throws Exception {

        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/99/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
     void whenPostRequestToBusinessProducts_AndProductAlreadyExists_then400Response() throws Exception {

        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneProduct("PRODUCT1", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");


        String product = "{\"id\": \"PRODUCT1\", \"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenPostRequestToBusinessProducts_AndProductCodeInvalid_then400Response() throws Exception {

        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        String product = "{\"id\": \"Invalid Product ';%\", \"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
     void whenPostRequestToBusinessProducts_AndUserIsBusinessAdminAndProductIsValid_then201Response() throws Exception {

        createOneBusiness("Business2", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business 2");

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT_1\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.GLOBAL_APPLICATION_ADMIN)
     void whenPostRequestToBusinessProducts_AndUserIsGlobalAdminButNotBusinessAdminAndProductIsValid_then201Response() throws Exception {

        Business business = new Business();

        business.setName("New Business");
        business.setAddress(new Address()
                .setSuburb("Riccarton")
                .setCity("Thames")
                .setId(1)
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30"));
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);

        businessService.createBusiness(business);

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT-1\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
     void whenGetRequestToBusinessProducts_AndBusinessNotExists_then406Response() throws Exception {

        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        createOneProduct("PRODUCT1", "Chocolate Bar", "Example Product First Edition","example manufacturer", "4.0");

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/99/products")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
     void whenGetRequestToBusinessProducts_AndUserIsBusinessAdminAndProductsExist_then200Response() throws Exception {

        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        createOneProduct("PRODUCT1", "Chocolate Bar", "Example Product First Edition","example manufacturer", "4.0");

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("products[0].name", is("Chocolate Bar")))
                .andExpect(jsonPath("products[0].description", is("Example Product First Edition")));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.GLOBAL_APPLICATION_ADMIN)
     void whenGetRequestToBusinessProducts_AndUserIsGlobalAdminButNotBusinessAdminAndProductsExist_then200Response() throws Exception {

        Business business = new Business();

        business.setName("New Business");
        business.setAddress(new Address()
                .setSuburb("Riccarton")
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
                .andExpect(jsonPath("products[0].name", is("Chocolate Bar")))
                .andExpect(jsonPath("products[0].description", is("Example Product First Edition")))
                .andExpect(jsonPath("products[1].name", is("Juice")))
                .andExpect(jsonPath("products[1].description", is("Example Product Second Edition")));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
     void whenGetRequestToBusinessProducts_AndUserIsBusinessAdminAndNoProductsExist_then200Response() throws Exception {

        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"products\":[],\"totalItems\":0}"));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
     void whenPutRequestToBusinessProducts_AndBusinessNotExists_then406Response() throws Exception {

        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT1\"}";
//         doThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Business with given ID does not exist")).when(businessService).findBusinessById(1);
        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/99/products/1")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
     void whenPutRequestToBusinessProducts_AndSuccess_then200Response() throws Exception {
        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        createOneProduct("chocolate-bar", "Chocolate Bar", "Example Product First Edition","example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
     void whenPutRequestToBusinessProducts_AndNameIsTheSame_then200Response() throws Exception {
        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        createOneProduct("chocolate-bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"chocolate bar\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
     void whenPutRequestToBusinessProducts_AndNameChanges_thenProductCodeChange_thenPutRequestAgainChangedProductCode_then200Response() throws Exception {
        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneProduct("chocolate-bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

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
     void whenPutRequestToBusinessProducts_AndNameChanges_thenProductCodeChange_thenPutRequestAgainOnPastCode_then400Response() throws Exception {
        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneProduct("chocolate-bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

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
     void whenPutRequestToBusinessProducts_AndIsNotAdminToBusiness_ButIsGlobalAdmin_then200Response() throws Exception {
        Business business = new Business();
        business.setName("New Business");
        business.setAddress(new Address()
                .setSuburb("Riccarton")
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
     void whenPutRequestToBusinessProducts_AndIsNotAdminToBusiness_ButIsDGAA_then200Response() throws Exception {
        Business business = new Business();
        business.setName("New Business");
        business.setAddress(new Address()
                .setSuburb("Riccarton")
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
     void whenPutRequestToBusinessProducts_AndNameMissing_then400Response() throws Exception {
        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");

        createOneProduct("PRODUCT1", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");


        String editProduct = "{\"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT1\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
     void whenPutRequestToBusinessProducts_AndNameIsBlank_then400Response() throws Exception {
        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneProduct("PRODUCT1", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT1\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
     void whenPutRequestToBusinessProducts_AndRecommendedRetailPriceIsLetter_then400Response() throws Exception {
        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneProduct("PRODUCT1", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"AB\", \"id\": \"PRODUCT1\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
     void whenPutRequestToBusinessProducts_AndSuccess_AndAllDataUpdates_thenAllChangesShouldBeMade() throws Exception {
        createOneBusiness("Business", "{\n" +
                "    \"streetNumber\": \"56\",\n" +
                "    \"streetName\": \"Clyde Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"8041\"\n" +
                "  }", "Non-profit organisation", "I am a business");
        createOneProduct("chocolate-bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("products[0].id", is("1-KIT-KAT")))
                .andExpect(jsonPath("products[0].name", is("Kit Kat")))
                .andExpect(jsonPath("products[0].description", is("Example Product")))
                .andExpect(jsonPath("products[0].recommendedRetailPrice", is(2.0)));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    void whenGetRequestToBusinessProducts_withOffsetOfZero_andCountOfOne_thenCorrectResultsReturned() throws Exception {
       createOneBusiness("Business", "{\n" +
               "    \"streetNumber\": \"56\",\n" +
               "    \"streetName\": \"Clyde Road\",\n" +
               "    \"suburb\": \"Riccarton\",\n" +
               "    \"city\": \"Christchurch\",\n" +
               "    \"region\": \"Canterbury\",\n" +
               "    \"country\": \"New Zealand\",\n" +
               "    \"postcode\": \"8041\"\n" +
               "  }", "Non-profit organisation", "I am a business");
       createOneProduct("chocolate-bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");
       createOneProduct("chocolate-bar2", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
       createOneProduct("chocolate-bar3", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");


       mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?offset=0&count=1")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("products[0].id", is("1-CHOCOLATE-BAR")))
               .andExpect(jsonPath("products[0].name", is("Chocolate Bar")))
               .andExpect(jsonPath("products[0].description", is("Example Product First Edition")))
               .andExpect(jsonPath("products[0].recommendedRetailPrice", is(4.0)))
               .andExpect(jsonPath("products", hasSize(1)));
    }

   @Test
   @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
   void whenGetRequestToBusinessProducts_thenCorrectTotalCount() throws Exception {
      createOneBusiness("Business", "{\n" +
              "    \"streetNumber\": \"56\",\n" +
              "    \"streetName\": \"Clyde Road\",\n" +
              "    \"suburb\": \"Riccarton\",\n" +
              "    \"city\": \"Christchurch\",\n" +
              "    \"region\": \"Canterbury\",\n" +
              "    \"country\": \"New Zealand\",\n" +
              "    \"postcode\": \"8041\"\n" +
              "  }", "Non-profit organisation", "I am a business");
      createOneProduct("chocolate-bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar2", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar3", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");


      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?offset=0&count=1")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("totalItems", is(3)));
   }

   @Test
   @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
   void whenGetRequestToBusinessProducts_withOffsetOfZero_andCountOfTwo_thenCorrectResultsReturned() throws Exception {
      createOneBusiness("Business", "{\n" +
              "    \"streetNumber\": \"56\",\n" +
              "    \"streetName\": \"Clyde Road\",\n" +
              "    \"suburb\": \"Riccarton\",\n" +
              "    \"city\": \"Christchurch\",\n" +
              "    \"region\": \"Canterbury\",\n" +
              "    \"country\": \"New Zealand\",\n" +
              "    \"postcode\": \"8041\"\n" +
              "  }", "Non-profit organisation", "I am a business");
      createOneProduct("chocolate-bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar2", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar3", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");


      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?offset=0&count=2")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("products[0].id", is("1-CHOCOLATE-BAR")))
              .andExpect(jsonPath("products[0].name", is("Chocolate Bar")))
              .andExpect(jsonPath("products", hasSize(2)));
   }

   @Test
   @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
   void whenGetRequestToBusinessProducts_withOffsetOfZero_andCountOfZero_then400BadRequest() throws Exception {
      createOneBusiness("Business", "{\n" +
              "    \"streetNumber\": \"56\",\n" +
              "    \"streetName\": \"Clyde Road\",\n" +
              "    \"suburb\": \"Riccarton\",\n" +
              "    \"city\": \"Christchurch\",\n" +
              "    \"region\": \"Canterbury\",\n" +
              "    \"country\": \"New Zealand\",\n" +
              "    \"postcode\": \"8041\"\n" +
              "  }", "Non-profit organisation", "I am a business");
      createOneProduct("chocolate-bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar2", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar3", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");

      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?offset=2&count=0")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isBadRequest());
   }


   @Test
   @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
   void whenGetRequestToBusinessProducts_withSortByIDASC_thenCorrectlySortedResults() throws Exception {
      createOneBusiness("Business", "{\n" +
              "    \"streetNumber\": \"56\",\n" +
              "    \"streetName\": \"Clyde Road\",\n" +
              "    \"suburb\": \"Riccarton\",\n" +
              "    \"city\": \"Christchurch\",\n" +
              "    \"region\": \"Canterbury\",\n" +
              "    \"country\": \"New Zealand\",\n" +
              "    \"postcode\": \"8041\"\n" +
              "  }", "Non-profit organisation", "I am a business");
      createOneProduct("chocolate-bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar2", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar3", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");


      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?sortBy=ID&sortDirection=ASC")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("products[0].id", is("1-CHOCOLATE-BAR")))
              .andExpect(jsonPath("products[1].id", is("1-CHOCOLATE-BAR2")))
              .andExpect(jsonPath("products[2].id", is("1-CHOCOLATE-BAR3")))
              .andExpect(jsonPath("products", hasSize(3)));
   }

   @Test
   @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
   void whenGetRequestToBusinessProducts_withSortByIDDESC_thenCorrectlySortedResults() throws Exception {
      createOneBusiness("Business", "{\n" +
              "    \"streetNumber\": \"56\",\n" +
              "    \"streetName\": \"Clyde Road\",\n" +
              "    \"suburb\": \"Riccarton\",\n" +
              "    \"city\": \"Christchurch\",\n" +
              "    \"region\": \"Canterbury\",\n" +
              "    \"country\": \"New Zealand\",\n" +
              "    \"postcode\": \"8041\"\n" +
              "  }", "Non-profit organisation", "I am a business");
      createOneProduct("chocolate-bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar2", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar3", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");


      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?sortBy=ID&sortDirection=DESC")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("products[2].id", is("1-CHOCOLATE-BAR")))
              .andExpect(jsonPath("products[1].id", is("1-CHOCOLATE-BAR2")))
              .andExpect(jsonPath("products[0].id", is("1-CHOCOLATE-BAR3")))
              .andExpect(jsonPath("products", hasSize(3)));
   }


   @Test
   @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
   void whenGetRequestToBusinessProducts_withNegativeOffset_then400BadRequest() throws Exception {
      createOneBusiness("Business", "{\n" +
              "    \"streetNumber\": \"56\",\n" +
              "    \"streetName\": \"Clyde Road\",\n" +
              "    \"suburb\": \"Riccarton\",\n" +
              "    \"city\": \"Christchurch\",\n" +
              "    \"region\": \"Canterbury\",\n" +
              "    \"country\": \"New Zealand\",\n" +
              "    \"postcode\": \"8041\"\n" +
              "  }", "Non-profit organisation", "I am a business");
      createOneProduct("chocolate-bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");


      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?offset=-1&count=2")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isBadRequest());
   }

   @Test
   @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
   void whenGetRequestToBusinessProducts_withNegativeCount_then400BadRequest() throws Exception {
      createOneBusiness("Business", "{\n" +
              "    \"streetNumber\": \"56\",\n" +
              "    \"streetName\": \"Clyde Road\",\n" +
              "    \"suburb\": \"Riccarton\",\n" +
              "    \"city\": \"Christchurch\",\n" +
              "    \"region\": \"Canterbury\",\n" +
              "    \"country\": \"New Zealand\",\n" +
              "    \"postcode\": \"8041\"\n" +
              "  }", "Non-profit organisation", "I am a business");
      createOneProduct("chocolate-bar", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");


      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?offset=1&count=-2")
              .contentType(MediaType.APPLICATION_JSON))
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

}
