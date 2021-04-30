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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is invalid");
        }

        business.setPrimaryAdministrator(user);

        List<User> adminList = new ArrayList<>();
        adminList.add(user);
        business.setAdministrators(adminList);

        business.setCreated(LocalDate.now());

        //Save business
        addressService.createAddress(business.getAddress());
        business = businessService.createBusiness(business);

        userService.addBusinessPrimarilyAdministered(user, business);

        userService.saveUserChanges(user);

        logger.info("saved new business {}", business);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * Get and return a business by its id
     *
     * @param businessId The id of the business to get
     * @return 200 and business if valid, 401 if unauthorised, 403 if forbidden, 406 if invalid id,
     */
    @GetMapping("/businesses/{id}")
    public ResponseEntity<Object> getBusiness(@PathVariable("id") Integer businessId, HttpServletRequest request) {

        Business possibleBusiness = businessService.findBusinessById(businessId);
        logger.info("possible Business {}", possibleBusiness);
        if (possibleBusiness == null) {
            logger.warn("ID does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        }

        logger.info("Business: {} retrieved successfully", possibleBusiness);

        GetBusinessesDto getBusinessesDto = GetBusinessesDtoMapper.toGetBusinessesDto(possibleBusiness);

        logger.info(getBusinessesDto);

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        User user = userService.findUserByEmail(currentPrincipalEmail);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is invalid");
        }

        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (possibleBusiness == null) {
            logger.warn("ID does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Business does not exist");
        }


        if (!possibleBusiness.getAdministrators().contains(user) && user.getRole() != UserRoles.GLOBAL_APPLICATION_ADMIN && user.getRole() != UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application or this business");
        }

        String productId = possibleProduct.createCode(businessId);

        if (productService.findProductById(productId) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The name of the product you have entered is too similar " +
                    "to one that is already in your catalogue.");
        }
        LocalDate dateCreated = LocalDate.now();

        possibleProduct.setId(productId);
        possibleProduct.setBusinessId(businessId);
        possibleProduct.setCreated(dateCreated);

        //Save product
        possibleProduct = productService.createProduct(possibleProduct);

        logger.info("saved new product {}", possibleProduct);

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        User user = userService.findUserByEmail(currentPrincipalEmail);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is invalid");
        }

        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (possibleBusiness == null) {
            logger.warn("ID does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Business does not exist");
        }


        if (!possibleBusiness.getAdministrators().contains(user) && user.getRole() != UserRoles.GLOBAL_APPLICATION_ADMIN && user.getRole() != UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application or this business");
        }

        List<Product> productList = productService.getAllProductsByBusinessId(businessId);


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
        logger.info("Request to edit product with {}", editedProduct);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        User user = userService.findUserByEmail(currentPrincipalEmail);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (user.getRole() != UserRoles.GLOBAL_APPLICATION_ADMIN && user.getRole() != UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN && !userService.checkUserAdminsBusiness(businessId, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Product oldProduct = productService.findProductById(productId);
        if (oldProduct == null) {
            logger.warn("Product does not exist.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product does not exist");
        }

        String newProductId = editedProduct.createCode(businessId);
        if (!oldProduct.getId().equals(newProductId) && productService.findProductById(newProductId) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The name of the product you have entered is too similar " +
                    "to one that is already in your catalogue.");
        }

        Product newProduct = new Product();
        newProduct.setId(newProductId);
        newProduct.setName(editedProduct.getName());
        newProduct.setDescription(editedProduct.getDescription());
        newProduct.setRecommendedRetailPrice(editedProduct.getRecommendedRetailPrice());
        newProduct.setBusinessId(oldProduct.getBusinessId());
        newProduct.setCreated(oldProduct.getCreated());

        productService.updateProduct(oldProduct, newProduct);
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

        Business possibleBusinessToAddAdminFor = businessService.findBusinessById(businessId);
        logger.info("possible Business {}", possibleBusinessToAddAdminFor);

        if (possibleBusinessToAddAdminFor == null) {
            logger.warn("ID does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        }

        User possibleUserToMakeAdmin = userService.findUserById(requestBody.getUserId());
        logger.info("possible User {}", possibleUserToMakeAdmin);

        if (possibleUserToMakeAdmin == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User does not exist");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        User userMakingRequest = userService.findUserByEmail(currentPrincipalEmail);

        if (!userMakingRequest.getRole().equals(UserRoles.GLOBAL_APPLICATION_ADMIN)
            && !userMakingRequest.getRole().equals(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
            && !(possibleBusinessToAddAdminFor.getPrimaryAdministrator().getId().equals(userMakingRequest.getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to make this request");
        }

        if (userService.checkUserAdminsBusiness(possibleBusinessToAddAdminFor.getId(), possibleUserToMakeAdmin.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already admin of business");
        }

        //Set user to be admin of business
        businessService.addAdministratorToBusiness(possibleBusinessToAddAdminFor, possibleUserToMakeAdmin);
        businessService.saveBusinessChanges(possibleBusinessToAddAdminFor);

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
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }


}
