package com.seng302.wasteless.controller;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.*;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


/**
 * SalesReportController is used for mapping all Restful API requests relating to sales report data.
 */
@RestController
public class SalesReportController {
    private static final Logger logger = LogManager.getLogger(com.seng302.wasteless.controller.SalesReportController.class.getName());


    private final BusinessService businessService;
    private final UserService userService;
    private final InventoryService inventoryService;
    private final ListingsService listingsService;
    private final NotificationService notificationService;
    private final PurchasedListingService purchasedListingService;


    @Autowired
    public SalesReportController(BusinessService businessService,
                             UserService userService,
                             InventoryService inventoryService,
                             ListingsService listingsService,
                             PurchasedListingService purchasedListingService,
                             NotificationService notificationService) {
        this.businessService = businessService;
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.listingsService = listingsService;
        this.notificationService = notificationService;
        this.purchasedListingService = purchasedListingService;
    }

    /**
     * Gets the total number of purchases for a business
     *
     * @param businessId    The id of the business to get purchases for
     * @return              The total purchases for a business
     */
    @GetMapping("/businesses/{id}/salesReport/totalPurchases")
    public ResponseEntity<Object> getTotalPurchasesOfBusiness(@PathVariable("id") Integer businessId,
                                                              @RequestParam(value = "startDate", required = false) LocalDate startDate,
                                                              @RequestParam(value = "endDate", required = false) LocalDate endDate) {
        User user = userService.getCurrentlyLoggedInUser();
        Business possibleBusiness = businessService.findBusinessById(businessId);
        logger.info("Successfully retrieved business with ID: {}.", businessId);
        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness,user);

        Integer totalPurchases = 0;

        if (startDate == null && endDate == null) {
            totalPurchases = purchasedListingService.countPurchasedListingForBusiness(businessId);
        } else if (startDate == null | endDate == null) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must specify a start date and an end date, or neither.");
        } else {
            if (endDate.isBefore(startDate)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date.");
            }
            totalPurchases = purchasedListingService.countPurchasedListingForBusinessInDateRange(businessId, startDate, endDate);
        }


        JSONObject responseBody = new JSONObject();
        responseBody.put("totalPurchases", totalPurchases);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
