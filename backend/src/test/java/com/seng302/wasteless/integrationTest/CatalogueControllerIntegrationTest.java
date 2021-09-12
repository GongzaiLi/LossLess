package com.seng302.wasteless.integrationTest;


import com.seng302.wasteless.TestUtils;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.repository.PurchasedListingRepository;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.testconfigs.WithMockCustomUser;
import org.junit.jupiter.api.Assertions;
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

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

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

   @Autowired
   private PurchasedListingRepository purchasedListingRepository;

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

        String product = "{\"name\": \"Chocolate Bar Plane\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

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
        createOneProduct("PRODUCT66577", "Chocolate Bar Car", "Example Product First Edition", "example manufacturer", "4.0");


        String product = "{\"id\": \"PRODUCT66577\", \"name\": \"Chocolate Bar Car\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\"}";

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

        String product = "{\"name\": \"Chocolate Bar Rice\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT_1\"}";

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

        String product = "{\"name\": \"Chocolate Bar Liquid\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT-1\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

   @Test
   @WithMockCustomUser(email = "user@test.com", role = UserRoles.GLOBAL_APPLICATION_ADMIN)
   void whenPostRequestToBusinessProducts_AndGenerateProductSales_thenSalesGenerated() throws Exception {
      createOneBusiness("Business2", "{\n" +
              "    \"streetNumber\": \"56\",\n" +
              "    \"streetName\": \"Clyde Road\",\n" +
              "    \"suburb\": \"Riccarton\",\n" +
              "    \"city\": \"Christchurch\",\n" +
              "    \"region\": \"Canterbury\",\n" +
              "    \"country\": \"New Zealand\",\n" +
              "    \"postcode\": \"8041\"\n" +
              "  }", "Non-profit organisation", "I am a business 2");

      String product = "{\"name\": \"Chocolate Bar Rice\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT_69\"}";

      mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products?generateSalesData=true")
                      .content(product)
                      .contentType(APPLICATION_JSON))
              .andExpect(status().isCreated());

      Assertions.assertFalse(purchasedListingRepository.findAllByBusinessId(1).isEmpty());
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

        createOneProduct("PRODUCT12", "Chocolate Bar Ice", "Example Product First Edition","example manufacturer", "4.0");

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

        createOneProduct("PRODUCT123", "Chocolate Bar Water", "Example Product First Edition","example manufacturer", "4.0");

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("products[0].name", is("Chocolate Bar Water")))
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

        createOneProduct("PRODUCT1234", "Chocolate Bar Fire", "Example Product First Edition","example manufacturer", "4.0");
        createOneProduct("PRODUCT2234", "Juice", "Example Product Second Edition","example manufacturer", "2.0");

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("products[0].name", is("Chocolate Bar Fire")))
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

        String product = "{\"name\": \"Chocolate Bar - 61\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT1\"}";
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

        createOneProduct("chocolate-bar-short", "Chocolate Bar Short", "Example Product First Edition","example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Kit Kat - K\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat K\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR-SHORT")
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

        createOneProduct("chocolate-bar-z", "Chocolate Bar Z", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Chocolate Bar - LONG\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"chocolate bar\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR-Z")
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
        createOneProduct("chocolate-bar-y", "Chocolate Bar Y", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Kit Kat Y\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat Y\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR-Y")
                .content(editProduct)
                .contentType(APPLICATION_JSON));

        String editProduct2 = "{\"name\": \"Raisin - T\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Raisin T\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-KIT-KAT-Y")
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
        createOneProduct("chocolate-bar-Q", "Chocolate Bar Q", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Kit Kat T\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR-Q")
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
        product.setId("10000");
        product.setBusinessId(business.getId());
        productService.createProduct(product);

        String editProduct = "{\"name\": \"Kit Kat GG\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/10000")
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
        product.setId("100001");
        product.setBusinessId(business.getId());
        productService.createProduct(product);

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/100001")
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

        createOneProduct("PRODUCT166666", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");


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
        createOneProduct("PRODUCT1777777", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

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
        createOneProduct("PRODUCT18888888", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Kit Kat W\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"PRODUCT1\"}";

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
        createOneProduct("chocolate-bar-P", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");

        String editProduct = "{\"name\": \"Kit Kat Q\", \"description\" : \"Example Product\", \"manufacturer\" : \"example manufacturer\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"Kit Kat Q\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR-P")
                .content(editProduct)
                .contentType(APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("products[0].id", is("1-KIT-KAT-Q")))
                .andExpect(jsonPath("products[0].name", is("Kit Kat Q")))
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
       createOneProduct("chocolate-bar188", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");
       createOneProduct("chocolate-bar288", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
       createOneProduct("chocolate-bar388", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");


       mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?offset=0&count=1")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("products[0].id", is("1-CHOCOLATE-BAR188")))
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
      createOneProduct("chocolate-bar77", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar277", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar377", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");


      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?offset=0&count=1")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("totalItems", is(3)));
   }

   @Test
   @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
   void whenGetRequestToBusinessProducts_withSearchQueryIsBar2_thenCorrectResultsReturned_andReturnProductNameIncludeBar2() throws Exception {
      createOneBusiness("Business", "{\n" +
              "    \"streetNumber\": \"56\",\n" +
              "    \"streetName\": \"Clyde Road\",\n" +
              "    \"suburb\": \"Riccarton\",\n" +
              "    \"city\": \"Christchurch\",\n" +
              "    \"region\": \"Canterbury\",\n" +
              "    \"country\": \"New Zealand\",\n" +
              "    \"postcode\": \"8041\"\n" +
              "  }", "Non-profit organisation", "I am a business");
      createOneProduct("chocolate-bar166", "Chocolate Bar1", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar266", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar366", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");


      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?searchQuery=Bar2")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("products[0].id", is("1-CHOCOLATE-BAR266")))
              .andExpect(jsonPath("products[0].name", is("Chocolate Bar2")))
              .andExpect(jsonPath("products", hasSize(1)));
   }

   @Test
   @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
   void whenGetRequestToBusinessProducts_withSearchQueryIsEmpty_thenCorrectResultsReturned_andReturnAllProducts() throws Exception {
      createOneBusiness("Business", "{\n" +
              "    \"streetNumber\": \"56\",\n" +
              "    \"streetName\": \"Clyde Road\",\n" +
              "    \"suburb\": \"Riccarton\",\n" +
              "    \"city\": \"Christchurch\",\n" +
              "    \"region\": \"Canterbury\",\n" +
              "    \"country\": \"New Zealand\",\n" +
              "    \"postcode\": \"8041\"\n" +
              "  }", "Non-profit organisation", "I am a business");
      createOneProduct("chocolate-bar15", "Chocolate Bar1", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar25", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar35", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");


      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?searchQuery")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("products[0].id", is("1-CHOCOLATE-BAR15")))
              .andExpect(jsonPath("products[1].id", is("1-CHOCOLATE-BAR25")))
              .andExpect(jsonPath("products[2].id", is("1-CHOCOLATE-BAR35")))
              .andExpect(jsonPath("products[0].name", is("Chocolate Bar1")))
              .andExpect(jsonPath("products[1].name", is("Chocolate Bar2")))
              .andExpect(jsonPath("products[2].name", is("Chocolate Bar3")))
              .andExpect(jsonPath("products", hasSize(3)));
   }

   @Test
   @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
   void whenGetRequestToBusinessProducts_withSearchQueryIsAAAAAA_thenCorrectResultsReturned_andReturnEmpty() throws Exception {
      createOneBusiness("Business", "{\n" +
              "    \"streetNumber\": \"56\",\n" +
              "    \"streetName\": \"Clyde Road\",\n" +
              "    \"suburb\": \"Riccarton\",\n" +
              "    \"city\": \"Christchurch\",\n" +
              "    \"region\": \"Canterbury\",\n" +
              "    \"country\": \"New Zealand\",\n" +
              "    \"postcode\": \"8041\"\n" +
              "  }", "Non-profit organisation", "I am a business");
      createOneProduct("chocolate-bar1-1122", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar2-1124", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar3-1126", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");


      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?searchQuery=AAAAAA")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("products", hasSize(0)));
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
      createOneProduct("chocolate-bar-OOOO", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar2-LLLL", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar3-GGGG", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");


      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?offset=0&count=2")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("products[0].id", is("1-CHOCOLATE-BAR-OOOO")))
              .andExpect(jsonPath("products[0].name", is("Chocolate Bar")))
              .andExpect(jsonPath("products", hasSize(2)));
   }

   @Test
   @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
   void whenGetRequestToBusinessProducts_withOffsetOfTwo_andCountOfZero_then400BadRequest() throws Exception {
      createOneBusiness("Business", "{\n" +
              "    \"streetNumber\": \"56\",\n" +
              "    \"streetName\": \"Clyde Road\",\n" +
              "    \"suburb\": \"Riccarton\",\n" +
              "    \"city\": \"Christchurch\",\n" +
              "    \"region\": \"Canterbury\",\n" +
              "    \"country\": \"New Zealand\",\n" +
              "    \"postcode\": \"8041\"\n" +
              "  }", "Non-profit organisation", "I am a business");
      createOneProduct("chocolate-bar-QWE", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar2-WER", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar3-ERT", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");

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
      createOneProduct("chocolate-bar-PPP", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar2-DDD", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar3-ZZZ", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");


      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?sortBy=ID&sortDirection=ASC")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("products[0].id", is("1-CHOCOLATE-BAR-PPP")))
              .andExpect(jsonPath("products[1].id", is("1-CHOCOLATE-BAR2-DDD")))
              .andExpect(jsonPath("products[2].id", is("1-CHOCOLATE-BAR3-ZZZ")))
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
      createOneProduct("chocolate-bar-998855", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar2-998844", "Chocolate Bar2", "Example Product First Edition", "example manufacturer", "4.0");
      createOneProduct("chocolate-bar3-998833", "Chocolate Bar3", "Example Product First Edition", "example manufacturer", "4.0");


      mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/products?sortBy=ID&sortDirection=DESC")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("products[2].id", is("1-CHOCOLATE-BAR-998855")))
              .andExpect(jsonPath("products[1].id", is("1-CHOCOLATE-BAR2-998844")))
              .andExpect(jsonPath("products[0].id", is("1-CHOCOLATE-BAR3-998833")))
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
      createOneProduct("chocolate-bar-998866", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");


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
      createOneProduct("chocolate-bar-998877", "Chocolate Bar", "Example Product First Edition", "example manufacturer", "4.0");


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
