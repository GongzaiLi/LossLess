package com.seng302.wasteless.controller;


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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        logger.info("Post request to business INVENTORY products, business id: {}, PostInventoryDto {}", businessId, inventoryDtoRequest);

        User user = userService.getCurrentlyLoggedInUser();
        logger.info("Got User {}", user);

        logger.debug("Retrieving business with id: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (possibleBusiness == null) {
            logger.warn("Cannot create INVENTORY product. Business ID: {} does not exist.", businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Business does not exist");
        }
        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusiness, businessId);

        if (!possibleBusiness.checkUserIsAdministrator(user) && !user.checkUserGlobalAdmin()) {
            logger.warn("Cannot create INVENTORY product. User: {} is not global admin or business admin: {}", user, possibleBusiness);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application or this business");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user, possibleBusiness);


        logger.info("Check if product with id ` {} ` exists on for business with id ` {} ` ", inventoryDtoRequest.getProductId(), businessId);
        Product possibleProduct = productService.findProductById(inventoryDtoRequest.getProductId());

        if (possibleProduct == null) {
            logger.warn("Cannot create inventory item for product that does not exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product with given id does not exist");
        }
        if (!possibleProduct.getBusinessId().equals(businessId)){
            logger.warn("Cannot update inventory item for product that does not belong to current business");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product id does not exist for Current Business");
        }
        Inventory inventory = PostInventoryDtoMapper.postInventoryDtoToEntityMapper(inventoryDtoRequest);

        inventory.setProduct(possibleProduct);
        inventory.setBusinessId(businessId);

        inventory = inventoryService.createInventory(inventory);

        logger.info("Created new inventory item {}", inventory);

        JSONObject responseBody = new JSONObject();
        responseBody.put("inventoryItemId", inventory.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }



    /**
     * Handle get request to /businesses/{id}/inventory endpoint for retrieving all products in a business's inventory
     *
     * @param businessId The id of the business to get
     * @return Http Status 200 and list of products if valid, 401 is unauthorised, 403 if forbidden, 406 if invalid id
     */
    @GetMapping("/businesses/{id}/inventory")
    public ResponseEntity<Object> getBusinessesInventoryProducts(@PathVariable("id") Integer businessId) {
        logger.debug("Request to get business INVENTORY products");

        User user = userService.getCurrentlyLoggedInUser();

        logger.debug("Retrieving business with id: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (possibleBusiness == null) {
            logger.warn("Cannot retrieve INVENTORY products. Business ID: {} does not exist.", businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Business does not exist");
        }
        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusiness, businessId);


        if (!possibleBusiness.checkUserIsAdministrator(user) && !user.checkUserGlobalAdmin()) {
            logger.warn("Cannot retrieve INVENTORY products. User: {} is not global admin or business admin: {}", user, possibleBusiness);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application or this business");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user, possibleBusiness);


        logger.debug("Retrieving INVENTORY products for business: {}", possibleBusiness);
        List<Inventory> inventoryList = inventoryService.getInventoryFromBusinessId(businessId);


        logger.info("INVENTORY Products retrieved: {} for business: {}", inventoryList, possibleBusiness);
        return ResponseEntity.status(HttpStatus.OK).body(inventoryList);

    }

    /**
     * Handle put request to /businesses/{businessId}/inventory/{inventoryItemId] endpoint for updating a product in a business's inventory
     *
     * @param businessId The id of the business to get
     * @param itemId the id of the inventory item to be updated
     * @return Http Status 200  if valid, 401 is unauthorised, 403 if forbidden, 406 if invalid id
     */
    @PutMapping("/businesses/{businessId}/inventory/{inventoryItemId}")
    public ResponseEntity<Object> putBusinessesInventoryProducts(@PathVariable("businessId") Integer businessId, @PathVariable("inventoryItemId") Integer itemId, @Valid @RequestBody PostInventoryDto editedInventoryItem) {
        logger.debug("Request to update inventory product");

        User user = userService.getCurrentlyLoggedInUser();

        logger.debug("Request to get business with ID: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);
        if (possibleBusiness == null) {
            logger.warn("Cannot Update INVENTORY product with ID {}. Business ID: {} does not exist.", itemId, businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Business does not exist");
        }
        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusiness, businessId);


        if (!possibleBusiness.checkUserIsAdministrator(user) && !user.checkUserGlobalAdmin()) {
            logger.warn("Cannot Update INVENTORY product. User: {} is not global admin or business admin: {}" ,user, possibleBusiness);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application or this business");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user, possibleBusiness);

        logger.info("Check if product with id ` {} ` exists on for business with id ` {} ` ", itemId, businessId);
        Product possibleProduct = productService.findProductById(editedInventoryItem.getProductId());
        if (possibleProduct == null) {
            logger.warn("Cannot update inventory item for product that does not exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product with given id does not exist");
        }
        if (!possibleProduct.getBusinessId().equals(businessId)){
            logger.warn("Cannot update inventory item for product that does not belong to current business");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product id does not exist for Current Business");
        }

        logger.info("Check if inventory item with id ` {} ` exists on for business with id ` {} ` ", itemId, businessId);
        Inventory inventoryItem = inventoryService.findInventoryById(itemId);
        logger.info(inventoryItem);
        if (inventoryItem == null) {
            logger.warn("Cannot update inventory item for item that does not exist in the inventory");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Inventory item with given id does not exist");
        }
        if (!inventoryItem.getBusinessId().equals(businessId)){
            logger.warn("Cannot update inventory item for item that does not belong to current business");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Inventory item with given id does not exist for Current Business");
        }

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




    /**
     * Returns a json object of bad field found in the request
     *
     * @param exception The exception thrown by Spring when it detects invalid data
     * @return Map of field name that had the error and a message describing the error.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException exception) {
        Map<String, String> errors;
        errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
//            logger.error(errorMessage); it doesnt work I am not sure why
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }


}
