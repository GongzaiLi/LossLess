package com.seng302.wasteless.controller;


import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.UserService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BusinessController is used for mapping all Restful API requests starting with the address "/businesses".
 */
@RestController
public class InventoryController {
    private static final Logger logger = LogManager.getLogger(InventoryController.class.getName());

    private final BusinessService businessService;
    private final UserService userService;
    //private final InventoryService inventoryService;

    private final ProductService inventoryService;    //Change to InventoryService

    @Autowired
    public InventoryController(BusinessService businessService, UserService userService, ProductService productService) {
        this.businessService = businessService;
        this.userService = userService;
//        this.inventoryService = inventoryService;

        this.inventoryService = productService;    //Needs inventory service


    }

    /**
     * Handle get request to /businesses/{id}/inventory endpoint for retrieving all products in a business's inventory
     *
     * @param businessId The id of the business to get
     * @return Http Status 200 and list of products if valid, 401 is unauthorised, 403 if forbidden, 406 if invalid id
     */
    @GetMapping("/businesses/{id}/inventory")
    public ResponseEntity<Object> getBusinessesInventoryProducts(@PathVariable("id") Integer businessId, HttpServletRequest request) {

        logger.debug("Request to get business INVENTORY products");


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        logger.debug("Validating user with Email: {}", currentPrincipalEmail);
        User user = userService.findUserByEmail(currentPrincipalEmail);

        if (user == null) {
            logger.warn("Cannot retrieve INVENTORY products. Access token invalid for user with Email: {}", currentPrincipalEmail);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is invalid");
        }
        logger.info("Validated token for user: {} with Email: {}.", user, currentPrincipalEmail);


        logger.debug("Request to get business with ID: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (possibleBusiness == null) {
            logger.warn("Cannot retrieve INVENTORY products. Business ID: {} does not exist.", businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Business does not exist");
        }
        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusiness, businessId);


        if (!possibleBusiness.getAdministrators().contains(user) && user.getRole() != UserRoles.GLOBAL_APPLICATION_ADMIN && user.getRole() != UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN) {
            logger.warn("Cannot retrieve INVENTORY products. User: {} is not global admin or business admin: {}", user, possibleBusiness);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application or this business");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user, possibleBusiness);


        logger.debug("Trying to retrieve INVENTORY products for business: {}", possibleBusiness);
        List<Product> productList = inventoryService.getAllProductsByBusinessId(businessId);

        logger.info("INVENTORY Products retrieved: {} for business: {}", productList, possibleBusiness);
        return ResponseEntity.status(HttpStatus.OK).body(productList);

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
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
//            logger.error(errorMessage); it doesnt work I am not sure why
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }


}
