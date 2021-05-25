package com.seng302.wasteless.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.dto.GetBusinessesDto;
import com.seng302.wasteless.dto.PutBusinessesAdminDto;
import com.seng302.wasteless.dto.mapper.GetBusinessesDtoMapper;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.view.BusinessViews;
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

/**
 * BusinessController is used for mapping all Restful API requests related directly to businesses
 */
@RestController
public class BusinessController {
    private static final Logger logger = LogManager.getLogger(BusinessController.class.getName());

    private final BusinessService businessService;
    private final UserService userService;
    private final AddressService addressService;

    @Autowired
    public BusinessController(BusinessService businessService, AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.businessService = businessService;
        this.userService = userService;

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

        logger.debug("Request to create new business {}", business);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        User user = userService.findUserByEmail(currentPrincipalEmail);

        logger.info("User trying to create business is: {}", user);

        if (user == null) {
            logger.warn("Failed to create Business. Access token invalid for user: {}", user);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is invalid");
        }
        logger.info("Access token valid for user: {}. Creating Business .... ", user);

        logger.debug("Adding business data");
        business.setPrimaryAdministrator(user);

        if (!user.checkIsOverSixteen()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Must be 16 to create a business");
        }

        business.setPrimaryAdministrator(user);

        List<User> adminList = new ArrayList<>();
        adminList.add(user);
        business.setAdministrators(adminList);

        logger.debug("Adding business date");
        business.setCreated(LocalDate.now());

        //Save business
        Address address = business.getAddress();
        logger.debug("Attempt to create Address Entity: {} entered by user: {}", address, user);
        addressService.createAddress(address);

        logger.debug("Attempt to create Business Entity: {} by user: {}", business, user);
        business = businessService.createBusiness(business);

        logger.info("Successfully created Business Entity: {} requested by user: {}", business, user);

        logger.debug("Trying to set user: {} as admin of business: {}", user, business);
        userService.addBusinessPrimarilyAdministered(user, business);

        logger.debug("Trying to update user: {} with business: {}", user, business);
        userService.saveUserChanges(user);
        logger.info("Successfully saved business: {} created by user: {}", business, user);

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


        logger.debug("Request to get business with ID: {}", businessId);

        Business possibleBusiness = businessService.findBusinessById(businessId);
        if (possibleBusiness == null) {
            logger.warn("Business ID: {} does not exist.", businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        }
        logger.info("Successfully Retrieved Business: {} using ID: {}", possibleBusiness, businessId);


        logger.debug("Request to get formatted business: {}", businessId);
        GetBusinessesDto getBusinessesDto = GetBusinessesDtoMapper.toGetBusinessesDto(possibleBusiness);

        logger.info("Successfully retrieved formatted business: {}", getBusinessesDto);
        return ResponseEntity.status(HttpStatus.OK).body(getBusinessesDto);


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
    public ResponseEntity<Object> makeAdministrator(@PathVariable("id") Integer businessId, @RequestBody PutBusinessesAdminDto requestBody) {

        logger.debug("Request to make user: {} the admin of business: {}", requestBody.getUserId(), businessId);

        logger.debug("Request to get business with ID: {}", businessId);
        Business possibleBusinessToAddAdminFor = businessService.findBusinessById(businessId);

        if (possibleBusinessToAddAdminFor == null) {
            logger.warn("Business ID: {} does not exist.", businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        }
        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusinessToAddAdminFor, businessId);


        logger.debug("Trying to find user with ID: {}", requestBody.getUserId());
        User possibleUserToMakeAdmin = userService.findUserById(requestBody.getUserId());

        if (possibleUserToMakeAdmin == null) {
            logger.warn("User with ID: {} does not exist", requestBody.getUserId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User does not exist");
        }
        logger.info("User: {} found using Id : {}", possibleUserToMakeAdmin, requestBody.getUserId());


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        logger.debug("Validating user with Email: {}", currentPrincipalEmail);
        User userMakingRequest = userService.findUserByEmail(currentPrincipalEmail);

        if (!userMakingRequest.checkUserGlobalAdmin()
                && !(possibleBusinessToAddAdminFor.checkUserIsPrimaryAdministrator(userMakingRequest))) {
            logger.warn("Cannot edit product. User: {} is not global admin or admin of business: {}", userMakingRequest, businessId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to make this request");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", userMakingRequest, businessId);


        if (userService.checkUserAdminsBusiness(possibleBusinessToAddAdminFor.getId(), possibleUserToMakeAdmin.getId())) {
            logger.warn("Cannot process request. User: {} is already an admin of business: {}.", requestBody.getUserId(), businessId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already admin of business");
        }


        //Set user to be admin of business
        logger.debug("Making user: {} an admin of business: {}.", possibleUserToMakeAdmin, businessId);
        businessService.addAdministratorToBusiness(possibleBusinessToAddAdminFor, possibleUserToMakeAdmin);
        logger.debug("Updating business: {} with new admin: {}", businessId, possibleUserToMakeAdmin);
        businessService.saveBusinessChanges(possibleBusinessToAddAdminFor);

        logger.info("Successfully made user: {} an admin of business: {}.", requestBody.getUserId(), businessId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /**
     * Handles Put request for /businesses/{id}/makeAdministrator endpoint for
     * removing an admin from a business.
     *
     * @param businessId The Id of the business
     * @param requestBody DTO that contains userId
     * @return (400) User with userId does not exist or Admin to remove is Primary, (406) business does not exist,
     *         (403) User is not admin, (200) if put request runs with out errors
     */
    @PutMapping("/businesses/{id}/removeAdministrator")
    public ResponseEntity<Object> revokeAdmin(@PathVariable("id") Integer businessId, @RequestBody PutBusinessesAdminDto requestBody) {

        Business possibleBusiness = businessService.findBusinessById(businessId);
        User possibleUser = userService.findUserById(requestBody.getUserId());
        logger.info("possible Business{}", possibleBusiness);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        User loggedInUser = userService.findUserByEmail(currentPrincipalEmail);

        if (possibleBusiness == null) {
            logger.warn("Business does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        }
        if (possibleUser == null) {
            logger.warn("User does not exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with ID does not exist");
        }
        if (possibleBusiness.getPrimaryAdministrator().equals(possibleUser)) {
            logger.warn("User is primary admin");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is primary admin");
        }
        if (!(possibleBusiness.checkUserIsPrimaryAdministrator(loggedInUser)) && !loggedInUser.checkUserGlobalAdmin()) {
            logger.warn("You are not a primary business admin");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to make this request");
        }
        if (!userService.checkUserAdminsBusiness(possibleBusiness.getId(), possibleUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not admin of business");
        }

        businessService.removeAdministratorFromBusiness(possibleBusiness, possibleUser);
        businessService.saveBusinessChanges(possibleBusiness);

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
