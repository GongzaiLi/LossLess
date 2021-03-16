package com.seng302.wasteless.Business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
