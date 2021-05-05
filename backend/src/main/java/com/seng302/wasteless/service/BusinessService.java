package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business service applies business logic over the Business JPA repository.
 */
@Service
public class BusinessService {

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
     * @return          The found business, if any, or wise null
     */
    public Business findBusinessById(Integer id) {
        return businessRepository.findFirstById(id);
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
