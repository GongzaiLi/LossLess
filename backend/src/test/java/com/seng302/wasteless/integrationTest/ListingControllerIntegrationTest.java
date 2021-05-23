package com.seng302.wasteless.integrationTest;


import com.seng302.wasteless.model.UserRoles;
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

import static org.springframework.http.MediaType.APPLICATION_JSON;
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



    private void createOneInventory(String id, Integer quantity, Double pricePerItem, Double totalPrice, LocalDate manufactured, LocalDate sellBy, LocalDate bestBefore, LocalDate expires) {

        String inventory = String.format("{\"productId\": \"%s\", \"quantity\": %s, \"pricePerItem\": %s, \"totalPrice\": %s, \"manufactured\": \"%s\", \"sellBy\": \"%s\",\"bestBefore\": \"%s\",\"expires\": \"%s\"}", id, quantity, pricePerItem, totalPrice,  manufactured,  sellBy,  bestBefore, expires) ;;

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/inventory")
                    .content(inventory)
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated());

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
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
