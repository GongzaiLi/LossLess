package com.seng302.wasteless.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.view.ProductViews;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import javax.validation.ConstraintViolationException;

/**
 * ProductController is used for mapping all Restful API requests starting with the address
 * "/businesses/{id}/products" and /businesses/{businessId}/products/{productId}.
 */
@RestController
public class CatalogueController {

    private static final Logger logger = LogManager.getLogger(CatalogueController.class.getName());

    private final BusinessService businessService;
    private final UserService userService;
    private final ProductService productService;

    @Autowired
    public CatalogueController(BusinessService businessService, UserService userService, ProductService productService) {
        this.businessService = businessService;
        this.userService = userService;
        this.productService = productService;

    }
    /**
     * Handle post request to /businesses/{id}/products endpoint for creating products
     *
     * @param businessId      the id of the business that is creating the product
     * @param possibleProduct the product that is trying to be added to the catalogue
     * @return Http Response:  200 if created, 400 for a bad request, 401 if unauthorised or 403 if forbidden
     */
    @PostMapping("/businesses/{id}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBusinessProduct(@PathVariable("id") Integer businessId, @Valid @RequestBody @JsonView(ProductViews.PostProductRequestView.class) Product possibleProduct, HttpServletRequest request) {

        logger.debug("Request to Create product: {} for business ID: {}", possibleProduct, businessId);


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String currentPrincipalEmail = authentication.getName();

        logger.debug("Validating user with Email: {}", currentPrincipalEmail);
        User user = userService.findUserByEmail(currentPrincipalEmail);

        if (user == null) {
            logger.warn("Access token invalid for user with Email: {}", currentPrincipalEmail);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is invalid");
        }
        logger.info("Validated token for user: {} with Email: {}.", user, currentPrincipalEmail);

        if (!possibleProduct.getId().matches("^[a-zA-Z0-9-_]*$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your product ID must be alphanumeric with dashes or underscores allowed.");
        }

        logger.debug("Request to get business with ID: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (possibleBusiness == null) {
            logger.warn("Business ID: {} does not exist.", businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Business does not exist");
        }
        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusiness, businessId);


        if (!possibleBusiness.getAdministrators().contains(user) && user.getRole() != UserRoles.GLOBAL_APPLICATION_ADMIN && user.getRole() != UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN) {
            logger.warn("Cannot create product. User: {} is not global admin or business admin: {}", user, possibleBusiness);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application or this business");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user, possibleBusiness);
        logger.debug("Trying to create product: {} for business: {}", possibleProduct, possibleBusiness);

        logger.debug("Generating product ID");
        String productId = possibleProduct.createCode(businessId);

        if (productService.findProductById(productId) != null) {
            logger.warn("Product ID not generated. ID already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product ID provided already exists.");
        }
        logger.info("Product ID: {} generated for product: {}", productId, possibleProduct);


        if (productService.findProductById(productId) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product ID provided already exists.");
        }

        if (possibleProduct.getRecommendedRetailPrice() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product RRP can't be negative.");
        }
        LocalDate dateCreated = LocalDate.now();

        logger.debug("Setting product data.");
        possibleProduct.setId(productId);
        possibleProduct.setBusinessId(businessId);
        possibleProduct.setCreated(dateCreated);

        //Save product
        logger.debug("Trying to create Product Entity for product: {}", possibleProduct);
        possibleProduct = productService.createProduct(possibleProduct);

        JSONObject responseBody = new JSONObject();
        responseBody.put("productId", possibleProduct.getId());

        logger.info("Successfully created Product Entity: {}", possibleProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);



    }


    /**
     * Handle get request to /businesses/{id}/products endpoint for retrieving all products in a business's catalogue
     *
     * @param businessId The id of the business to get
     * @return Http Status 200 and list of products if valid, 401 is unauthorised, 403 if forbidden, 406 if invalid id
     */
    @GetMapping("/businesses/{id}/products")
    public ResponseEntity<Object> getBusinessesProducts(@PathVariable("id") Integer businessId, HttpServletRequest request) {

        logger.debug("Request to get business products");


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        logger.debug("Validating user with Email: {}", currentPrincipalEmail);
        User user = userService.findUserByEmail(currentPrincipalEmail);

        if (user == null) {
            logger.warn("Access token invalid for user with Email: {}", currentPrincipalEmail);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is invalid");
        }
        logger.info("Validated toke for user: {} with Email: {}.", user, currentPrincipalEmail);


        logger.debug("Request to get business with ID: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (possibleBusiness == null) {
            logger.warn("Business ID: {} does not exist.", businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Business does not exist");
        }
        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusiness, businessId);


        if (!possibleBusiness.getAdministrators().contains(user) && user.getRole() != UserRoles.GLOBAL_APPLICATION_ADMIN && user.getRole() != UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN) {
            logger.warn("Cannot create product. User: {} is not global admin or business admin: {}", user, possibleBusiness);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application or this business");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user, possibleBusiness);


        logger.debug("Trying to retrieve products for business: {}", possibleBusiness);
        List<Product> productList = productService.getAllProductsByBusinessId(businessId);

        logger.info("Products retrieved: {} for business: {}", productList, possibleBusiness);
        return ResponseEntity.status(HttpStatus.OK).body(productList);

    }

    /**
     * Handle put request to /businesses/{businessId}/products/{productId} endpoint for updating a product
     * in the product catalogue
     *
     * @param businessId    The business containing the product
     * @param productId     The product that needs to be updated
     * @param editedProduct A product object that holds the edited values
     * @return  Http Status 200 and 403 if user not admin, 400 if product doesn't exist and if new product ID already exists.
     */
    @PutMapping("/businesses/{businessId}/products/{productId}")
    public ResponseEntity<Object> editBusinessProduct(@PathVariable("businessId") Integer businessId, @PathVariable("productId") String productId, @Valid @RequestBody Product editedProduct) {

        logger.debug("Request to update product with data: {} for business ID: {}", editedProduct, businessId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        logger.debug("Validating user with Email: {}", currentPrincipalEmail);
        User user = userService.findUserByEmail(currentPrincipalEmail);
        if (user == null) {
            logger.warn("Access token invalid for user with Email: {}", currentPrincipalEmail);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("Validated token for user: {} with Email: {}.", user, currentPrincipalEmail);


        if (user.getRole() != UserRoles.GLOBAL_APPLICATION_ADMIN && user.getRole() != UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN && !userService.checkUserAdminsBusiness(businessId, user.getId())) {
            logger.warn("Cannot edit product. User: {} is not global admin or admin of business: {}", user, businessId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user, businessId);

        logger.debug("Trying to find product with ID: {} in the catalogue", productId);
        Product oldProduct = productService.findProductById(productId);

        if (oldProduct == null) {
            logger.warn("Could not find product with ID: {}", productId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product does not exist.");
        }
        logger.info("Found product: {} using ID: {}", oldProduct, productId);

        if (!oldProduct.getId().matches("^[a-zA-Z0-9-_]*$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your product ID must be alphanumeric with dashes or underscores allowed.");
        }
        logger.debug("Generating new product ID");
        String newProductId = editedProduct.createCode(businessId);
        if (!oldProduct.getId().equals(newProductId) && productService.findProductById(newProductId) != null) {
            logger.warn("Product ID already exists. Not updating.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product ID provided already exists.");
        }
        logger.info("Product ID: {} generated for old product: {}", newProductId, productId);

        if (editedProduct.getRecommendedRetailPrice() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product RRP can't be negative.");
        }

        logger.debug("Trying to update product: {} for business: {} with new data: {}", oldProduct, businessId, editedProduct);

        oldProduct.setId(newProductId);
        oldProduct.setName(editedProduct.getName());
        oldProduct.setDescription(editedProduct.getDescription());
        oldProduct.setManufacturer(editedProduct.getManufacturer());
        oldProduct.setRecommendedRetailPrice(editedProduct.getRecommendedRetailPrice());

        productService.updateProduct(oldProduct);

        logger.info("Successfully updated old product with data: {}", oldProduct);
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
    /**
     * Returns a json object of bad field found in the request
     *
     * @param exception The exception thrown by Spring when it detects invalid data
     * @return Map of field name that had the error and a message describing the error.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleValidationExceptions(
            ConstraintViolationException exception) {

        Map<String, String> errors = new HashMap<>();

        String constraintName = exception.getConstraintViolations().toString();
        String errorMsg = exception.getMessage();

        errors.put(constraintName, errorMsg);
        return errors;
    }
}
