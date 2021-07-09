package com.seng302.wasteless.service;

import com.seng302.wasteless.controller.InventoryController;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.repository.BusinessRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Business service applies business logic over the Business JPA repository.
 */
@Service
public class BusinessService {
    private static final Logger logger = LogManager.getLogger(BusinessService.class.getName());

    private final BusinessRepository businessRepository;

    @Autowired
    public BusinessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    /**
     * Create a new business in Database
     *
     * @param business      The business object to create
     * @return              The created business
     */
    public Business createBusiness(Business business) {
        return businessRepository.save(business);
    }


    /**
     * Find business by id
     *
     * @param id        The id of the business to find
     * @return          The found business, if any, or throw ResponseStatusException
     */
    public Business findBusinessById(Integer id) {
        Business possibleBusiness = businessRepository.findFirstById(id);
        if (possibleBusiness == null) {
            logger.warn("Business with id: {} does not exist.", id);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Business with given ID does not exist");
        }
        return possibleBusiness;
    }


    /**
     * Find all businesses administered by user id
     *
     * @param id        The id of the user administrators
     * @return          All business ids administered by user id
     */
    public List<Business> findBusinessesByUserId(Integer id) {return businessRepository.findBySpecificAdminId(id); }

    /**
     * Add administrator to a business
     *
     * Calling the method in this way allows for mocking during automated testing
     *
     */
    public void addAdministratorToBusiness(Business business, User user) {
        business.addAdministrator(user);
    }

    /**
     * Remove administrator from a business
     * Calling the method in this way allows for mocking during automated testing
     *
     */
    public void removeAdministratorFromBusiness(Business business, User user) {
        business.removeAdministrator(user);
    }


    /**
     * Save changes to business
     *
     * @param business Save changes to a business
     */
    public void saveBusinessChanges(Business business) {
        businessRepository.save(business);
    }
}
