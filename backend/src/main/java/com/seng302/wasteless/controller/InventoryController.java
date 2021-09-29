package com.seng302.wasteless.controller;


import com.seng302.wasteless.dto.GetInventoryDto;
import com.seng302.wasteless.dto.PostInventoryDto;
import com.seng302.wasteless.dto.mapper.PostInventoryDtoMapper;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.InventoryService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.UserService;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * InventoryController is used for mapping all Restful API requests starting with the address "/businesses".
 */
@RestController
public class InventoryController {
    private static final Logger logger = LogManager.getLogger(InventoryController.class.getName());

    private final BusinessService businessService;
    private final UserService userService;
    private final InventoryService inventoryService;
    private final ProductService productService;

    @Autowired
    public InventoryController(BusinessService businessService, UserService userService, ProductService productService, InventoryService inventoryService) {
        this.businessService = businessService;
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.productService = productService;

    }

    /**
     * Handle POST request to /businesses/{id}/inventory endpoint for creating new inventory item for business
     *
     * Checks business with id from path exists
     * Checks user making request exists, and has privileges (admin of business, or (D)GAA)
     * Checks product with id exists
     * Other validation of inventory handled by the inventory entity class
     *
     *
     * @param businessId    The id of the business to create inventory item for
     * @param inventoryDtoRequest     Dto containing information needed to create an inventory product
     * @return Error code detailing error or 201 create with inventoryItemId
     */
    @PostMapping("/businesses/{id}/inventory")
    public ResponseEntity<Object> postBusinessInventoryProducts(@PathVariable("id") Integer businessId, @Valid @RequestBody PostInventoryDto inventoryDtoRequest) {
        logger.debug("Post request to business INVENTORY products, business id: {}, PostInventoryDto {}", businessId, inventoryDtoRequest);

        User user = userService.getCurrentlyLoggedInUser();

        Business possibleBusiness = businessService.findBusinessById(businessId);

        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness, user);

        logger.info("Check if product with id ` {} ` exists on for business with id ` {} ` ", inventoryDtoRequest.getProductId(), businessId);
        Product possibleProduct = productService.findProductById(inventoryDtoRequest.getProductId());

        productService.checkProductBelongsToBusiness(possibleProduct, businessId);
        Inventory inventory = PostInventoryDtoMapper.postInventoryDtoToEntityMapper(inventoryDtoRequest);

        inventory.setProduct(possibleProduct);
        inventory.setBusinessId(businessId);

        inventory = inventoryService.createInventory(inventory);

        logger.info("Created new inventory item with Id {}", inventory.getId());

        JSONObject responseBody = new JSONObject();
        responseBody.put("inventoryItemId", inventory.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }



    /**
     * Handle get request to /businesses/{id}/inventory endpoint for retrieving all products in a business's inventory
     *
     * @param businessId The id of the business to get
     * @param pageable The pageable that consists of page index, size (number of pages) and sort order.
     * @param searchQuery The search query to filter the inventory items by
     * @return Http Status 200 and list of products if valid, 401 is unauthorised, 403 if forbidden, 406 if invalid id
     */
    @GetMapping("/businesses/{id}/inventory")
    public ResponseEntity<Object> getBusinessesInventoryProducts(@PathVariable("id") Integer businessId, Pageable pageable, String searchQuery){
        if (searchQuery == null) searchQuery = "";
        logger.debug("Request to search inventory with query: {}", searchQuery);

        User user = userService.getCurrentlyLoggedInUser();

        logger.debug("Retrieving business with id: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness, user);

        logger.debug("Retrieving INVENTORY products for business with Id: {}", possibleBusiness.getId());
        List<Inventory> inventoryList = inventoryService.searchInventoryFromBusinessId(businessId, searchQuery, pageable);

        Integer totalItems = inventoryService.getTotalInventoryCountByBusinessId(businessId, searchQuery);

        GetInventoryDto getInventoryDto = new GetInventoryDto()
                .setInventory(inventoryList)
                .setTotalItems(totalItems);


        logger.info("INVENTORY Products retrieved: {} for business: {}", inventoryList, possibleBusiness);
        return ResponseEntity.status(HttpStatus.OK).body(getInventoryDto);

    }

    /**
     * Handle put request to /businesses/{businessId}/inventory/{inventoryItemId] endpoint for updating a product in a business's inventory
     *
     * @param businessId The id of the business to get
     * @param itemId the id of the inventory item to be updated
     * @param editedInventoryItem The new inventory item with data that replaces old inventory item data
     * @return Http Status 200  if valid, 401 is unauthorised, 403 if forbidden, 406 if invalid id
     */
    @PutMapping("/businesses/{businessId}/inventory/{inventoryItemId}")
    public ResponseEntity<Object> putBusinessesInventoryProducts(@PathVariable("businessId") Integer businessId, @PathVariable("inventoryItemId") Integer itemId, @Valid @RequestBody PostInventoryDto editedInventoryItem) {
        logger.debug("Request to update inventory product");

        User user = userService.getCurrentlyLoggedInUser();

        Business possibleBusiness = businessService.findBusinessById(businessId);

        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness, user);

        logger.info("Check if product with id ` {} ` exists on for business with id ` {} ` ", itemId, businessId);
        Product possibleProduct = productService.findProductById(editedInventoryItem.getProductId());

        productService.checkProductBelongsToBusiness(possibleProduct, businessId);

        logger.info("Check if inventory item with id ` {} ` exists on for business with id ` {} ` ", itemId, businessId);
        Inventory inventoryItem = inventoryService.findInventoryById(itemId);

        productService.checkProductBelongsToBusiness(possibleProduct, businessId);

        inventoryService.updateQuantityUnlisted(inventoryItem, editedInventoryItem.getQuantity());

        logger.info("Creating new Inventory Item and setting data.");
        inventoryItem.setProduct(possibleProduct);
        inventoryItem.setBusinessId(businessId);
        inventoryItem.setQuantity(editedInventoryItem.getQuantity());
        inventoryItem.setPricePerItem(editedInventoryItem.getPricePerItem());
        inventoryItem.setTotalPrice(editedInventoryItem.getTotalPrice());
        inventoryItem.setManufactured(editedInventoryItem.getManufactured());
        inventoryItem.setSellBy(editedInventoryItem.getSellBy());
        inventoryItem.setBestBefore(editedInventoryItem.getBestBefore());
        inventoryItem.setExpires(editedInventoryItem.getExpires());



        logger.debug("Trying to Update INVENTORY product with ID {} for business: {}", itemId, possibleBusiness);
        inventoryService.updateInventory(inventoryItem);


        logger.info("INVENTORY Product updated with ID {} for business: {}", itemId, possibleBusiness);
        return ResponseEntity.status(HttpStatus.OK).build();

    }
}
