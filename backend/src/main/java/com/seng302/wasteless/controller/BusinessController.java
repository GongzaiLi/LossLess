package com.seng302.wasteless.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.dto.GetBusinessesDto;
import com.seng302.wasteless.dto.PutBusinessesMakeAdminDto;
import com.seng302.wasteless.dto.mapper.GetBusinessesDtoMapper;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.MainApplicationRunner;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.view.BusinessViews;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BusinessController {
    private static final Logger logger = LogManager.getLogger(MainApplicationRunner.class.getName());

    private final BusinessService businessService;
    private final UserService userService;
    private final AddressService addressService;
    private final ProductService productService;

    @Autowired
    public BusinessController(BusinessService businessService, AddressService addressService, UserService userService, ProductService productService) {
        this.addressService = addressService;
        this.businessService = businessService;
        this.userService = userService;
        this.productService = productService;

    }

    /**
     * Handle post request to /businesses endpoint for creating businesses
     * <p>
     * <p>
     * The @Valid annotation ensures the correct fields are present, 400 if not
     * The @JsonView prevents injection of readonly fields, fields ignored (null) if present
     *
     * @param business The business parsed from the request
     * @return 201 if created or 401 if unauthorised
     */
    @PostMapping("/businesses")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBusiness(@Valid @RequestBody @JsonView(BusinessViews.PostBusinessRequestView.class) Business business, HttpServletRequest request) {


            logger.info("Request to create new business {}", business);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentPrincipalEmail = authentication.getName();

            User user = userService.findUserByEmail(currentPrincipalEmail);

            logger.info("User trying to create business is: {}", user);

            if (user == null) {
                logger.info("Failed to create Business. Access token invalid for user: {}", user);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        "Access token is invalid");
            }
            logger.info("Access token valid for user: {}. Creating Business .... ", user);


            business.setPrimaryAdministrator(user);

            List<User> adminList = new ArrayList<>();
            adminList.add(user);
            business.setAdministrators(adminList);

            business.setCreated(LocalDate.now());

            //Save business
            Address address = business.getAddress();
            logger.info("Attempt to create Address Entity: {} entered by user: {}", address, user);
            addressService.createAddress(address);

            logger.info("Attempt to create Business Entity: {} by user: {}", business, user);
            business = businessService.createBusiness(business);

            logger.info("Successfully created Business Entity: {} requested by user: {}", business, user);

            logger.info("Trying to set user: {} as admin of business: {}", user, business);
            userService.addBusinessPrimarilyAdministered(user, business);

            logger.info("Trying to update user: {} with business: {}", user, business);
            userService.saveUserChanges(user);
            logger.info("Successfully saved business: {} created by user: {}", business);

            JSONObject responseBody = new JSONObject();
            responseBody.put("businessId", business.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);


    }


    /**
     * Get and return a business by its id
     *
     * @param businessId The id of the business to get
     * @return 200 and business if valid, 401 if unauthorised, 403 if forbidden, 406 if invalid id,
     */
    @GetMapping("/businesses/{id}")
    public ResponseEntity<Object> getBusiness(@PathVariable("id") Integer businessId, HttpServletRequest request) {

        logger.info("Request to get business with ID: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);
        if (possibleBusiness == null) {
            logger.warn("Business ID: {} does not exist.", businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        }
        logger.info("Successfully Retrieved Business: {} using ID: {}", possibleBusiness, businessId);




        logger.info("Request to get formatted business: {}", businessId);
        GetBusinessesDto getBusinessesDto = GetBusinessesDtoMapper.toGetBusinessesDto(possibleBusiness);

        logger.info("Successfully retrieved formatted business: {}", getBusinessesDto);
        return ResponseEntity.status(HttpStatus.OK).body(getBusinessesDto);
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

        logger.info("Request to Create product: {} for business ID: {}", possibleProduct, businessId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        logger.info("Validating user with Email: {}", currentPrincipalEmail);
        User user = userService.findUserByEmail(currentPrincipalEmail);

        if (user == null) {
            logger.warn("Access token invalid for user with Email: {}", currentPrincipalEmail);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is invalid");
        }
        logger.info("Validated toke for user: {} with Email: {}.", user, currentPrincipalEmail);


        logger.info("Request to get business with ID: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (possibleBusiness == null) {
            logger.warn("Business ID: {} does not exist.", businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Business does not exist");
        }
        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusiness, businessId);



        if (!possibleBusiness.getAdministrators().contains(user) && user.getRole() != UserRoles.GLOBAL_APPLICATION_ADMIN && user.getRole() != UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN) {
            logger.info("Cannot create product. User: {} is not global admin or business admin: {}", user, possibleBusiness);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application or this business");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user, possibleBusiness);
        logger.info("Trying to create product: {} for business: {}", possibleProduct, possibleBusiness);

        logger.info("Generating product ID");
        String productId = possibleProduct.createCode(businessId);

        if (productService.findProductById(productId) != null) {
            logger.info("Product ID not generated. ID already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product ID provided already exists.");
        }
        logger.info("Product ID: {} generated for product: {}", productId, possibleProduct);



        logger.info("Getting product creation date.");
        LocalDate dateCreated = LocalDate.now();

        logger.info("Setting product data.");
        possibleProduct.setId(productId);
        possibleProduct.setBusinessId(businessId);
        possibleProduct.setCreated(dateCreated);

        //Save product
        logger.info("Trying to create Product Entity for product: {}", possibleProduct);
        possibleProduct = productService.createProduct(possibleProduct);

        logger.info("Successfully created Product Entity: {}", possibleProduct);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * Handle get request to /businesses/{id}/products endpoint for retrieving all products in a business's catalogue
     *
     * @param businessId The id of the business to get
     * @return Http Status 200 and list of products if valid, 401 is unauthorised, 403 if forbidden, 406 if invalid id
     */
    @GetMapping("/businesses/{id}/products")
    public ResponseEntity<Object> getBusinessesProducts(@PathVariable("id") Integer businessId, HttpServletRequest request) {

        logger.info("Request to get business products");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        logger.info("Validating user with Email: {}", currentPrincipalEmail);
        User user = userService.findUserByEmail(currentPrincipalEmail);

        if (user == null) {
            logger.info("Access token invalid for user with Email: {}", currentPrincipalEmail);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is invalid");
        }
        logger.info("Validated toke for user: {} with Email: {}.", user, currentPrincipalEmail);


        logger.info("Request to get business with ID: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (possibleBusiness == null) {
            logger.warn("Business ID: {} does not exist.", businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Business does not exist");
        }
        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusiness, businessId);



        if (!possibleBusiness.getAdministrators().contains(user) && user.getRole() != UserRoles.GLOBAL_APPLICATION_ADMIN && user.getRole() != UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN) {
            logger.info("Cannot create product. User: {} is not global admin or business admin: {}", user, possibleBusiness);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application or this business");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user, possibleBusiness);



        logger.info("Trying to retrieve products for business: {}", possibleBusiness);
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
        logger.info("Request to update product with data: {} for business ID: {}", editedProduct, businessId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        logger.info("Validating user with Email: {}", currentPrincipalEmail);
        User user = userService.findUserByEmail(currentPrincipalEmail);
        if (user == null) {
            logger.info("Access token invalid for user with Email: {}", currentPrincipalEmail);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("Validated token for user: {} with Email: {}.", user, currentPrincipalEmail);


        if (user.getRole() != UserRoles.GLOBAL_APPLICATION_ADMIN && user.getRole() != UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN && !userService.checkUserAdminsBusiness(businessId, user.getId())) {
            logger.info("Cannot edit product. User: {} is not global admin or admin of business: {}", user, businessId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user, businessId);

        logger.info("Trying to find product with ID: {} in the catalogue", productId);
        Product oldProduct = productService.findProductById(productId);

        if (oldProduct == null) {
            logger.warn("Could not find product with ID: {}", productId );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product does not exist.");
        }
        logger.info("Found product: {} using ID: {}", oldProduct, productId );


        logger.info("Generating new product ID");
        String newProductId = editedProduct.createCode(businessId);
        if (!oldProduct.getId().equals(newProductId) && productService.findProductById(newProductId) != null) {
            logger.info("Product ID already exists. Not updating.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product ID provided already exists.");
        }
        logger.info("Product ID: {} generated for old product: {}", newProductId, productId);



        logger.info("Creating new Product Entity");
        Product newProduct = new Product();
        newProduct.setId(newProductId);
        newProduct.setName(editedProduct.getName());
        newProduct.setDescription(editedProduct.getDescription());
        newProduct.setRecommendedRetailPrice(editedProduct.getRecommendedRetailPrice());
        newProduct.setBusinessId(oldProduct.getBusinessId());
        newProduct.setCreated(oldProduct.getCreated());

        logger.info("Trying to update product: {} for business: {} with new data: {}", oldProduct, businessId, newProduct);
        productService.updateProduct(oldProduct, newProduct);

        logger.info("Successfully updated old product: {} with new product: {}", oldProduct, newProduct);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /**
     * Put request to make a user a administrator of a business
     *
     * Checks if the business exists, and user to make admin exists
     * Checks if the user making the request has permission to, either as the primary business admin
     * for that business, or as a global application admin
     *
     * Gets the user currently logged in from Authentication, this is the person acting
     *
     * If the above passes adds the user to the businesses list of administrators
     *
     * Returns 200 on success
     * Returns 400 if user does not exist, or if user is already admin
     * Returns 401 if unauthorised, handled by spring security
     * Returns 403 if forbidden request, i.e. the request is not allowed to make the request
     * Returns 406 if business does not exist
     *
     *
     * @param businessId    The id the business to add an administrator for
     * @param requestBody   The request body containing the userId of the user to make admin
     * @return  Response code with message, see above for codes
     */
    @PutMapping("/businesses/{id}/makeAdministrator")
    public ResponseEntity<Object> makeAdministrator(@PathVariable("id") Integer businessId, @RequestBody PutBusinessesMakeAdminDto requestBody) {

        logger.info("Request to make user: {} the admin of business: {}", requestBody.getUserId(), businessId);

        logger.info("Request to get business with ID: {}", businessId);
        Business possibleBusinessToAddAdminFor = businessService.findBusinessById(businessId);

        if (possibleBusinessToAddAdminFor == null) {
            logger.warn("Business ID: {} does not exist.", businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        }
        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusinessToAddAdminFor, businessId);


        logger.info("trying to find user with ID: {}", requestBody.getUserId());
        User possibleUserToMakeAdmin = userService.findUserById(requestBody.getUserId());

        if (possibleUserToMakeAdmin == null) {
            logger.warn("Could not find user with ID: {}", requestBody.getUserId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User does not exist");
        }
        logger.info("User: {} found using Id : {}", possibleUserToMakeAdmin, requestBody.getUserId());



        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        logger.info("Validating user with Email: {}", currentPrincipalEmail);
        User userMakingRequest = userService.findUserByEmail(currentPrincipalEmail);

        if (!userMakingRequest.getRole().equals(UserRoles.GLOBAL_APPLICATION_ADMIN)
            && !userMakingRequest.getRole().equals(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
            && !(possibleBusinessToAddAdminFor.getPrimaryAdministrator().getId().equals(userMakingRequest.getId()))) {
            logger.warn("Cannot process request. User: {} is not global admin or admin of business: {}", userMakingRequest, businessId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to make this request");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", userMakingRequest, businessId);


        if (userService.checkUserAdminsBusiness(possibleBusinessToAddAdminFor.getId(), possibleUserToMakeAdmin.getId())) {
            logger.info("Cannot process request. User: {} is already an admin of business: {}.", possibleUserToMakeAdmin, businessId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already admin of business");
        }


        //Set user to be admin of business
        logger.info("Making user: {} an admin of business: {}.", possibleUserToMakeAdmin, businessId);
        businessService.addAdministratorToBusiness(possibleBusinessToAddAdminFor, possibleUserToMakeAdmin);
        logger.info("Updating business: {} with new admin: {}", businessId, possibleUserToMakeAdmin);
        businessService.saveBusinessChanges(possibleBusinessToAddAdminFor);

        logger.info("Successfully made user: {} an admin of business: {}.", possibleUserToMakeAdmin, businessId);
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
        exception.getBindingResult().getAllErrors().forEach((error) -> {
//            logger.error(error);
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }


}
