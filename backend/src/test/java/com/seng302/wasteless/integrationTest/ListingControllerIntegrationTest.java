package com.seng302.wasteless.integrationTest;


import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.InventoryService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.testconfigs.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Remove security
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties"
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Reset JPA between test
public class ListingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    BusinessService businessService;

    @Autowired
    InventoryService inventoryService;


    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.GLOBAL_APPLICATION_ADMIN)
    public void whenPostRequestToBusinessListings_AndUserIsBusinessAdminAndInventoryIsValid_then201Response() throws Exception {

        Address address = new Address()
                .setCity("Thames")
                .setCountry("Nz")
                .setPostcode("3500")
                .setRegion("Waikato")
                .setStreetName("Queen Street")
                .setStreetNumber("30");

        Business business = new Business();

        business.setName("New Business");
        business.setAddress(address);
        business.setBusinessType(BusinessTypes.NON_PROFIT_ORGANISATION);
        Integer businessId = businessService.createBusiness(business).getId();


        Product product = new Product();
        product.setBusinessId(businessId);
        product.setName("P1");
        product.setId("1-1");
        product.setCreated(LocalDate.now());
        product.setDescription("nothing");
        product.setRecommendedRetailPrice(5.5);

        productService.createProduct(product);

        Inventory inventory = new Inventory();
        inventory.setQuantity(50);
        inventory.setBusinessId(1);
        inventory.setBestBefore( LocalDate.parse("2022-05-12"));
        inventory.setExpires( LocalDate.parse("2022-05-12"));
        inventory.setManufactured( LocalDate.parse("2020-05-12"));
        inventory.setPricePerItem(2);
        inventory.setProduct(product);
        inventory.setSellBy( LocalDate.parse("2022-05-12"));
        inventory.setTotalPrice(10.5);

        Integer inventoryId = inventoryService.createInventory(inventory).getId();


//        createOneInventory("1-1", 50, 6.5, 22.99, LocalDate.parse("2020-05-12"), LocalDate.parse("3021-05-12"), LocalDate.parse("3021-05-12"), LocalDate.parse("3021-05-12"));

        String listing = String.format("{\"inventoryItemId\": %s, \"quantity\": 50, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12\"}", inventoryId);

        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/businesses/%d/listings", businessId))
                .content(listing)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("listingId", is(1)));

    }

//    @Test
//    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
//    public void whenPostRequestToBusinessListings_AndUserIsBusinessAdminAndInventoryIsValid_butListingQuantityStaysValid_then201Response() throws Exception {
//
//        createOneBusiness("Business2", "{\n" +
//                "    \"streetNumber\": \"56\",\n" +
//                "    \"streetName\": \"Clyde Road\",\n" +
//                "    \"city\": \"Christchurch\",\n" +
//                "    \"region\": \"Canterbury\",\n" +
//                "    \"country\": \"New Zealand\",\n" +
//                "    \"postcode\": \"8041\"\n" +
//                "  }", "Non-profit organisation", "I am a business 2");
//
////        createOneProduct("1", "Chocolate Bar", "Example Product First Edition","example manufacturer", "4.0");
//
//        Product product = new Product();
//        product.setBusinessId(1);
//        product.setName("P1");
//        product.setId("1-1");
//        product.setCreated(LocalDate.now());
//        product.setDescription("nothing");
//        product.setRecommendedRetailPrice(5.5);
//
//        productService.createProduct(product);
//
//        createOneInventory("1-1", 50, 6.5, 22.99, LocalDate.parse("2020-05-12"), LocalDate.parse("3021-05-12"), LocalDate.parse("3021-05-12"), LocalDate.parse("3021-05-12"));
//
//        String listing = "{\"inventoryItemId\": 1, \"quantity\": 30, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12\"}";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
//                .content(listing)
//                .contentType(APPLICATION_JSON));
////                .andExpect(status().isCreated());
//
//
//        String listing2 = "{\"inventoryItemId\": 1, \"quantity\": 20, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12\"}";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
//                .content(listing2)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isCreated());
//    }

//    @Test
//    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
//    public void whenPostRequestToBusinessListings_AndUserIsBusinessAdminAndInventoryIsValid_butListingQuantityChangesToInvalid_then400Response() throws Exception {
//
//        createOneBusiness("Business2", "{\n" +
//                "    \"streetNumber\": \"56\",\n" +
//                "    \"streetName\": \"Clyde Road\",\n" +
//                "    \"city\": \"Christchurch\",\n" +
//                "    \"region\": \"Canterbury\",\n" +
//                "    \"country\": \"New Zealand\",\n" +
//                "    \"postcode\": \"8041\"\n" +
//                "  }", "Non-profit organisation", "I am a business 2");
//
//        createOneProduct("1", "Chocolate Bar", "Example Product First Edition","example manufacturer", "4.0");
//
//        createOneInventory("1-1", 50, 6.5, 22.99, LocalDate.parse("2020-05-12"), LocalDate.parse("3021-05-12"), LocalDate.parse("3021-05-12"), LocalDate.parse("3021-05-12"));
//
//        String listing = "{\"inventoryItemId\": 1, \"quantity\": 30, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12\"}";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
//                .content(listing)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isCreated());
//
//
//        String listing2 = "{\"inventoryItemId\": 1, \"quantity\": 30, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12\"}";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
//                .content(listing2)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }



//    private void createOneInventory(String id, Integer quantity, Double pricePerItem, Double totalPrice, LocalDate manufactured, LocalDate sellBy, LocalDate bestBefore, LocalDate expires) {
//
//        String inventory = String.format("{\"productId\": \"%s\", \"quantity\": %s, \"pricePerItem\": %s, \"totalPrice\": %s, \"manufactured\": \"%s\", \"sellBy\": \"%s\",\"bestBefore\": \"%s\",\"expires\": \"%s\"}", id, quantity, pricePerItem, totalPrice,  manufactured,  sellBy,  bestBefore, expires) ;;
//
//        try {
//            mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/inventory")
//                    .content(inventory)
//                    .contentType(APPLICATION_JSON))
//                    .andExpect(status().isCreated());
//
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Invalid Request", e);
//        }
//    }




//    private void createOneBusiness(String name, String address, String businessType, String description) {
//        String business = String.format("{\"name\": \"%s\", \"address\" : %s, \"businessType\": \"%s\", \"description\": \"%s\"}", name, address, businessType, description);
//
//        try {
//            mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
//                    .content(business)
//                    .contentType(APPLICATION_JSON))
//                    .andExpect(status().isCreated());
//
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Invalid Request", e);
//        }
//    }

//    private void createOneProduct(String id, String name, String description, String manufacturer, String recommendedRetailPrice) {
//
//        String product = String.format("{\"id\" : \"%s\", \"name\": \"%s\", \"description\" : \"%s\", \"manufacturer\" : \"%s\", \"recommendedRetailPrice\": \"%s\"}", id, name, description, manufacturer, recommendedRetailPrice);
//
//        try {
//            mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/products")
//                    .content(product)
//                    .contentType(APPLICATION_JSON))
//                    .andExpect(status().isCreated());
//
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Invalid Request", e);
//        }
//    }

}
