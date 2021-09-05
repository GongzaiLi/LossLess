package com.seng302.wasteless.controller;

import com.seng302.wasteless.dto.SalesReportDto;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;


/**
 * SalesReportController is used for mapping all Restful API requests relating to sales report data.
 */
@RestController
public class SalesReportController {
    private static final Logger logger = LogManager.getLogger(com.seng302.wasteless.controller.SalesReportController.class.getName());
    private static final DayOfWeek START_OF_WEEK = DayOfWeek.MONDAY;

    private final BusinessService businessService;
    private final UserService userService;
    private final PurchasedListingService purchasedListingService;


    @Autowired
    public SalesReportController(BusinessService businessService,
                             UserService userService,
                             PurchasedListingService purchasedListingService) {
        this.businessService = businessService;
        this.userService = userService;
        this.purchasedListingService = purchasedListingService;
    }

    /**
     * Gets the total number of purchases for a business
     *
     * Takes a date range in form of startDate and endDate. Either both or neither must be supplied
     *
     * @param businessId    The id of the business to get purchases for
     * @param startDate     The start date for the date range. Format yyyy-MM-dd
     * @param endDate       The end date for the date range. Format yyyy-MM-dd
     * @param period        The selected period for the data.
     * @return              The total purchases for a business
     */
    @GetMapping("/businesses/{id}/salesReport/totalPurchases")
    public ResponseEntity<Object> getPurchaseDataOfBusiness(@PathVariable("id") Integer businessId,
                                                              @RequestParam(value = "startDate", required = false) LocalDate startDate,
                                                              @RequestParam(value = "endDate", required = false) LocalDate endDate,
                                                              @RequestParam(value = "period", required = false) String period) {
        User user = userService.getCurrentlyLoggedInUser();
        Business possibleBusiness = businessService.findBusinessById(businessId);
        logger.info("Successfully retrieved business with ID: {}.", businessId);
        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness,user);

        if (startDate == null && endDate == null) {
            startDate = possibleBusiness.getCreated();
            endDate = LocalDate.now();
        } else if (startDate == null || endDate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must specify a start date and an end date, or neither.");
        } else if (endDate.isBefore(startDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date.");
        }

        Period periodOfData;
        LocalDate firstPeriodStart = startDate;
        LocalDate lastPeriodEnd = endDate;

        if (period == null) {

            List<SalesReportDto> responseBody = purchasedListingService.getSalesReportDataNoPeriod(businessId, startDate, endDate);
            logger.info(responseBody);

            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }

        switch (period) {
            case "day":
                periodOfData = Period.ofDays(1);
                break;
            case "week":
                periodOfData = Period.ofDays(7);
                while (firstPeriodStart.getDayOfWeek() != START_OF_WEEK) {
                    firstPeriodStart = firstPeriodStart.minusDays(1);
                }
                while (lastPeriodEnd.getDayOfWeek() != START_OF_WEEK.minus(1)) {
                    lastPeriodEnd = lastPeriodEnd.plusDays(1);
                }
                break;
            case "month":
                periodOfData = Period.ofMonths(1);
                firstPeriodStart = startDate.withDayOfMonth(1);
                lastPeriodEnd = endDate.withDayOfMonth(endDate.lengthOfMonth());
                break;
            case "year":
                periodOfData = Period.ofYears(1);
                firstPeriodStart = startDate.withDayOfYear(1);
                lastPeriodEnd = endDate.withDayOfYear(endDate.lengthOfYear());
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have not specified a correct period.");
        }

        List<SalesReportDto> responseBody = purchasedListingService.getSalesReportDataWithPeriod(businessId, startDate,
                endDate, firstPeriodStart, lastPeriodEnd, periodOfData);
        logger.info(responseBody);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
