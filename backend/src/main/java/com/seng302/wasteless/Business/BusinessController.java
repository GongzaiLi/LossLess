package com.seng302.wasteless.Business;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.MainApplicationRunner;
import com.seng302.wasteless.User.User;
import com.seng302.wasteless.User.UserService;
import com.seng302.wasteless.User.UserViews;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
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

    private BusinessService businessService;
    private UserService userService;

    @Autowired
    public BusinessController(BusinessService businessService, UserService userService) {
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

        Cookie type = WebUtils.getCookie(request, "JSESSIONID");

        if (type == null || !type.getValue().contains("USER")) {
            logger.warn("Access token is missing or invalid.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is missing or invalid");
        }

        String cookieValue = type.getValue();
        String userId = cookieValue.substring(0, cookieValue.length() - 5);

        User user = userService.findUserById(Integer.parseInt(userId));

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is invalid");
        }

        business.setPrimaryAdministrator(user);

        List<User> adminList = new ArrayList<>();
        adminList.add(user);
        business.setAdministrators(adminList);
        user.addPrimaryBusiness(business);

        logger.info("business was {}", business);

//        //Validate business type
//        if(! business.checkValidBusinessType()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid business type");
//        }

        business.setCreated(LocalDate.now());

        //Todo add association to the user who created this business

        //Save business
        business = businessService.createBusiness(business);

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
    @JsonView({BusinessViews.GetBusinessView.class})
    public ResponseEntity<Object> getBusiness(@PathVariable("id") Integer businessId, HttpServletRequest request) {

        Cookie type = WebUtils.getCookie(request, "JSESSIONID");

        if (type == null || !type.getValue().contains("USER")) {
            logger.warn("Access token is missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is missing or invalid");
        }

        Business possibleBusiness = businessService.findBusinessById(businessId);
        logger.info("possible Business{}", possibleBusiness);
        if (possibleBusiness == null) {
            logger.warn("ID does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        }

        logger.info("Business: {} retrieved successfully", possibleBusiness);
        return ResponseEntity.status(HttpStatus.OK).body(possibleBusiness);
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
