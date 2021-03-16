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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
public class BusinessController {
    private static final Logger logger = LogManager.getLogger(MainApplicationRunner.class.getName());

    private BusinessService businessService;

    @Autowired
    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
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
    public ResponseEntity<Object> createBusiness(@Valid @RequestBody @JsonView(BusinessViews.PostBusinessRequestView.class) Business business) {

        logger.info("business was {}", business);

        //Validate business type
        if(! business.checkValidBusinessType()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid business type");
        }

        business.setCreated(LocalDate.now());

        //Todo add association to the user who created this business

        //Save business
        business = businessService.createBusiness(business);

        logger.info("saved new business {}", business);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



}
