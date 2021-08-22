package com.seng302.wasteless.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.dto.GetBusinessesDto;
import com.seng302.wasteless.dto.GetSearchBusinessDto;
import com.seng302.wasteless.dto.PutBusinessesAdminDto;
import com.seng302.wasteless.dto.mapper.GetBusinessesDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.view.BusinessViews;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<Object> createBusiness(@Valid @RequestBody @JsonView(BusinessViews.PostBusinessRequestView.class) Business business) {

        logger.debug("Request to create new business {}", business);

        User user = userService.getCurrentlyLoggedInUser();

        logger.debug("Adding business data");
        business.setPrimaryAdministrator(user);

        if (!user.checkIsOverSixteen()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must be 16 to create a business");
        }

        business.setPrimaryAdministrator(user);

        List<User> adminList = new ArrayList<>();
        adminList.add(user);
        business.setAdministrators(adminList);

        business.setCreated(LocalDate.now());

        //Save business
        Address address = business.getAddress();
        addressService.createAddress(address);

        business = businessService.createBusiness(business);

        logger.debug("Trying to set user: {} as admin of business: {}", user.getId(), business.getId());
        userService.addBusinessPrimarilyAdministered(user, business);
        userService.saveUserChanges(user);

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
    public ResponseEntity<Object> getBusiness(@PathVariable("id") Integer businessId) {
        logger.debug("Request to get business with ID: {}", businessId);

        Business possibleBusiness = businessService.findBusinessById(businessId);

        logger.debug("Request to get formatted business: {}", businessId);
        GetBusinessesDto getBusinessesDto = GetBusinessesDtoMapper.toGetBusinessesDto(possibleBusiness);

        logger.info("Successfully retrieved formatted business");
        return ResponseEntity.status(HttpStatus.OK).body(getBusinessesDto);
    }


    /**
     * Handle request to /businesses/search for searching by name for all businesses. Takes a pageable to
     * perform pagination and sorting.
     *
     * @param searchQuery           The search query to search the businesses by name
     * @param type                  The business type to search by, can be blank. Must be same as enum type i.e "CHARITABLE_ORGANISATION"
     * @param pageable              The pageable that consists of page index, size (number of pages) and sort order.
     * @return                      Http Status 200 and list of businesses and a total items value if valid,
     *                              401 is unauthorised,
     *                              400 bad request if invalid business type
     */
    @JsonView({BusinessViews.SearchBusinessesView.class})
    @GetMapping("/businesses/search")
    public ResponseEntity<Object> searchBusinesses(String searchQuery, String type, Pageable pageable) {
        if (searchQuery == null) searchQuery = "";
        logger.debug("Request to search businesses with query: {} and business type {}", searchQuery, type);

        List<Business> businessList;
        Integer totalItems;

        if (type != null && !type.equals("")) {
            BusinessTypes businessType;
            try {
                businessType = BusinessTypes.valueOf(type);
            } catch (IllegalArgumentException e) {
                logger.info("Invalid value for businessType. Value was {}", type);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid value for businessType.");
            }

            logger.debug("Retrieving businesses with search query {} and business type {}", searchQuery, businessType);
            businessList = businessService.searchBusinessesWithBusinessType(searchQuery, businessType, pageable);
            totalItems = businessService.getTotalBusinessesCountWithQueryAndType(searchQuery, businessType);
        } else {
            logger.debug("Retrieving businesses with search query {}", searchQuery);
            businessList = businessService.searchBusinesses(searchQuery, pageable);
            totalItems = businessService.getTotalBusinessesCount(searchQuery);
        }




        GetSearchBusinessDto getSearchBusinessDto = new GetSearchBusinessDto()
                .setBusinesses(businessList)
                .setTotalItems(totalItems);

        return ResponseEntity.status(HttpStatus.OK).body(getSearchBusinessDto);


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

        Business possibleBusinessToAddAdminFor = businessService.findBusinessById(businessId);

        User possibleUserToMakeAdmin = userService.findUserById(requestBody.getUserId());
        User userMakingRequest = userService.getCurrentlyLoggedInUser();

        businessService.checkUserAdminOfBusinessOrGAA(possibleBusinessToAddAdminFor, userMakingRequest);


        if (userService.checkUserAdminsBusiness(possibleBusinessToAddAdminFor.getId(), possibleUserToMakeAdmin.getId())) {
            logger.warn("Cannot process request. User: {} is already an admin of business: {}.", requestBody.getUserId(), businessId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already admin of business");
        }

        //Set user to be admin of business
        businessService.addAdministratorToBusiness(possibleBusinessToAddAdminFor, possibleUserToMakeAdmin);
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
        User userToRevoke = userService.findUserById(requestBody.getUserId());
        logger.info("possible Business{}", possibleBusiness);

        User loggedInUser = userService.getCurrentlyLoggedInUser();

        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness, loggedInUser);

        if (possibleBusiness.getPrimaryAdministrator().equals(userToRevoke)) {
            logger.warn("User is primary admin");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is primary admin");
        }

        if (!userService.checkUserAdminsBusiness(possibleBusiness.getId(), userToRevoke.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not admin of business");
        }

        businessService.removeAdministratorFromBusiness(possibleBusiness, userToRevoke);
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
