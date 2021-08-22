package com.seng302.wasteless.unitTest.ServiceTests;


import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.repository.InventoryRepository;
import com.seng302.wasteless.service.InventoryService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(SpringRunner.class)
@WebMvcTest(InventoryService.class)
class InventoryServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryRepository inventoryRepository;

    @MockBean
    private UserService userService;

    @Test
    void whenFindInventoryById_AndInvalidId_ThrowException() {
        Mockito.when(inventoryRepository.findFirstById(1)).thenReturn(null);
        boolean success = true;
        try {
            InventoryService inventoryService = new InventoryService(inventoryRepository);
            inventoryService.findInventoryById(1);
        } catch (ResponseStatusException e) {
            success = false;
            assertEquals(400, e.getRawStatusCode());
        }
        Assertions.assertFalse(success);

    }


    @Test
    void whenUpdateQuantityUnlisted_AndQuantityUnlistedLessThenZero_ThrowException() {
        Inventory inventory = new Inventory();
        inventory.setQuantity(2);
        inventory.setQuantityUnlisted(0);


        boolean success = true;
        try {
            InventoryService inventoryService = new InventoryService(inventoryRepository);
            inventoryService.updateQuantityUnlisted(inventory, 1);
        } catch (ResponseStatusException e) {
            success = false;
            assertEquals(400, e.getRawStatusCode());
        }
        Assertions.assertFalse(success);

    }

    @Test
    void whenUpdateQuantityUnlisted_AndQuantityUnlistedMoreThanEqualZero() {
        Inventory inventory = new Inventory();
        inventory.setQuantity(2);
        inventory.setQuantityUnlisted(1);


        boolean success = true;
        try {
            InventoryService inventoryService = new InventoryService(inventoryRepository);
            inventoryService.updateQuantityUnlisted(inventory, 100);
        } catch (ResponseStatusException e) {
            success = false;
            assertEquals(400, e.getRawStatusCode());
        }
        Assertions.assertTrue(success);

    }




}

