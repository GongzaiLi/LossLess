package com.seng302.wasteless.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.dto.GetBusinessesDto;
import com.seng302.wasteless.dto.mapper.GetBusinessesDtoMapper;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.MainApplicationRunner;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.view.BusinessViews;
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

    @Autowired
    public BusinessController(BusinessService businessService, AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.businessService = businessService;
        this.userService = userService;
    }

    /**
     * Handle post request to /businesses endpoint for creating businesses
     *
     *
     * The @Valid annotation ensures the correct fields are present, 400 if not
     * The @JsonView prevents injection of readonly fields, fields ignored (null) if present
     *
     * @param business  The business parsed from the request
     * @return          201 if created or 401 if unauthorised
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

        logger.info("saved new business {}", business);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * Get and return a business by its id
     *
     * @param businessId        The id of the business to get
     * @return                  406 if invalid id, 401 is unauthorised, 200 and business if valid
     */
    @GetMapping("/businesses/{id}")
    public ResponseEntity<Object> getBusiness(@PathVariable("id") Integer businessId, HttpServletRequest request) {

        Business possibleBusiness = businessService.findBusinessById(businessId);
        logger.info("possible Business{}", possibleBusiness);
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
