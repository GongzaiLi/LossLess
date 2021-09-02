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
     * Takes a date range in form of startDate and endDate. Either both or neither must be supplied
     *
     * @param businessId    The id of the business to get purchases for
     * @param startDate     The start date for the date range. Format yyyy-MM-dd
     * @param endDate       The end date for the date range. Format yyyy-MM-dd
     * @return              The total purchases for a business
     */
    @GetMapping("/businesses/{id}/salesReport/totalPurchases")
    public ResponseEntity<Object> getTotalPurchasesOfBusiness(@PathVariable("id") Integer businessId,
                                                              @RequestParam(value = "startDate", required = false) LocalDate startDate,
                                                              @RequestParam(value = "endDate", required = false) LocalDate endDate,
                                                              @RequestParam(value = "period") String period) {
        User user = userService.getCurrentlyLoggedInUser();
        Business possibleBusiness = businessService.findBusinessById(businessId);
        logger.info("Successfully retrieved business with ID: {}.", businessId);
        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness,user);
//todo AC4: I can select the granularity of the report.
// By default, I will just see the total number and total value of all purchases made during the period,
// together with the details of the period, and any other relevant detail (e.g. the business name)..
        if (startDate == null && endDate == null) {
            startDate = possibleBusiness.getCreated();
            endDate = LocalDate.now();
        }

        Integer dayPeriod = 1;
        LocalDate periodStart = startDate;
        LocalDate periodEnd = endDate;
        if (period.equals("day")) {
            dayPeriod = 1;
        }
        else if(period.equals("week")){
            dayPeriod = 7;
            while(periodStart.getDayOfWeek()!=START_OF_WEEK){
                periodStart = periodStart.minusDays(1);
                logger.info("Minus.{}",START_OF_WEEK);
            }
            while(periodEnd.getDayOfWeek()!=START_OF_WEEK.minus(1)){
                periodEnd = periodEnd.plusDays(1);
                logger.info("Plus.{}",START_OF_WEEK.minus(1));
            }
        }
        else if(period.equals("month")){
            periodStart = startDate.withDayOfMonth(1);
            periodEnd = endDate.withDayOfMonth(endDate.lengthOfMonth());
        }

        List<SalesReportDto> responseBody = new ArrayList<>();


        if (startDate == null | endDate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must specify a start date and an end date, or neither.");
        } else {
            if (endDate.isBefore(startDate)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date.");
            }
            LocalDate searchStart;
            LocalDate searchEnd;
            for (LocalDate date = periodStart; date.isBefore(periodEnd.plusDays(1)); date = date.plusDays(dayPeriod)) {
                logger.info("Successfully retrieved date: {}.", date);
                searchStart = date;
                searchEnd = searchStart.plusDays(dayPeriod - 1);
                logger.info("Before {}.", searchStart.isBefore(startDate));
                logger.info("After: {}.", searchEnd.isAfter(endDate));
                if (searchStart.isBefore(startDate)){ searchStart = startDate;}
                if (searchEnd.isAfter(endDate)){ searchEnd = endDate;}
                Integer totalPurchases = purchasedListingService.countPurchasedListingForBusinessInDateRange(businessId, searchStart, searchEnd);
                Double totalValue = purchasedListingService.totalPurchasedListingValueForBusinessInDateRange(businessId, searchStart, searchEnd);

                if (totalValue == null) {
                    totalValue = 0.0;
                }

                SalesReportDto reportDto = new SalesReportDto(searchStart,searchEnd, totalPurchases, totalValue);
                responseBody.add(reportDto);
            }


        }

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
