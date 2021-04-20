package com.seng302.wasteless;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.BusinessTypes;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.testconfigs.WithMockCustomUser;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ProductService productService;

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessAndBusinessExists_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");

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
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");

        mockMvc.perform(get("/businesses/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessAndMultipleBusinessExists_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");
        createOneBusiness("Business2", "Location2", "Non-profit organisation", "I am a business 2");

        mockMvc.perform(get("/businesses/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Business2")))
                .andExpect(jsonPath("description", is("I am a business 2")));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPostRequestToBusiness_andInvalidBusiness_dueToIllegalBusinessType_then400Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : \"Peanut Lane\", \"businessType\": \"Oil Company\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private void createOneBusiness(String name, String address, String businessType, String description) {

        String business = String.format("{\"name\": \"%s\", \"address\" : \"%s\", \"businessType\": \"%s\", \"description\": \"%s\"}", name, address, businessType, description);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                    .content(business)
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated());

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }

    private void createOneProduct(String name, String description, String recommendedRetailPrice) {

        String product = String.format("{\"name\": \"%s\", \"description\" : \"%s\", \"recommendedRetailPrice\": \"%s\"}", name, description, recommendedRetailPrice);

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

        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

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
        business.setAddress("Home");
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);

        businessService.createBusiness(business);

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPostRequestToBusinessProducts_AndProductAlreadyExists_then400Response() throws Exception {

        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");
        createOneProduct("Chocolate Bar", "Example Product First Edition", "4.0");

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPostRequestToBusinessProducts_AndUserIsBusinessAdminAndProductIsValid_then201Response() throws Exception {

        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

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
        business.setAddress("Home");
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);

        businessService.createBusiness(business);

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
                .content(product)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndBusinessNotExists_then403Response() throws Exception {

        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");

        String product = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

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
        business.setAddress("Home");
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);

        businessService.createBusiness(business);

        Product product = new Product();

        product.setName("Kit Kat");
        product.setId("1");
        product.setBusinessId(business.getId());

        productService.createProduct(product);

        String editProduct = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndSuccess_then200Response() throws Exception {
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");
        createOneProduct("Chocolate Bar", "Example Product First Edition", "4.0");

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndNameIsTheSame_then200Response() throws Exception {
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");
        createOneProduct("Chocolate Bar", "Example Product First Edition", "4.0");

        String editProduct = "{\"name\": \"Chocolate Bar\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndNameChanges_thenProductCodeChange_thenPutRequestAgainChangedProductCode_then200Response() throws Exception {
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");
        createOneProduct("Chocolate Bar", "Example Product First Edition", "4.0");

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON));

        String editProduct2 = "{\"name\": \"Raisin\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-KIT-KAT")
                .content(editProduct2)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndNameChanges_thenProductCodeChange_thenPutRequestAgainOnPastCode_then200Response() throws Exception {
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");
        createOneProduct("Chocolate Bar", "Example Product First Edition", "4.0");

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON));

        String editProduct2 = "{\"name\": \"Raisin\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

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
        business.setAddress("Home");
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);
        businessService.createBusiness(business);

        Product product = new Product();
        product.setName("Kit Kat");
        product.setId("1");
        product.setBusinessId(business.getId());
        productService.createProduct(product);

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

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
        business.setAddress("Home");
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);
        businessService.createBusiness(business);

        Product product = new Product();
        product.setName("Kit Kat");
        product.setId("1");
        product.setBusinessId(business.getId());
        productService.createProduct(product);

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndNameMissing_then400Response() throws Exception {
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");
        createOneProduct("Chocolate Bar", "Example Product First Edition", "4.0");

        String editProduct = "{\"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndNameIsBlank_then400Response() throws Exception {
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");
        createOneProduct("Chocolate Bar", "Example Product First Edition", "4.0");

        String editProduct = "{\"name\": \"\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-CHOCOLATE-BAR")
                .content(editProduct)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPutRequestToBusinessProducts_AndSuccess_AndAllDataUpdates_thenAllChangesShouldBeMade() throws Exception {
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");
        createOneProduct("Chocolate Bar", "Example Product First Edition", "4.0");

        String editProduct = "{\"name\": \"Kit Kat\", \"description\" : \"Example Product\", \"recommendedRetailPrice\": \"2.0\", \"id\": \"ToBeIgnored\"}";

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
