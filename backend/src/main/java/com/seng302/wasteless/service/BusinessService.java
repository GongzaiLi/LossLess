package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessService {

    private BusinessRepository businessRepository;

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

}
