package com.seng302.wasteless.testconfigs;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.model.BusinessTypes;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import org.springframework.boot.test.context.TestConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a configuration file for a mocked BusinessService bean.
 * It contains the class definition for MockBusinessService, a mock BusinessService
 * (see doc for MockBusinessService for more details).
 *
 * To use this configuration in unit tests, put @Import(MockBusinessServiceConfig.class)
 * above the test class. This will allow the test to use the mocked BusinessService bean.
 */

@TestConfiguration
public class MockBusinessServiceConfig {

    public static class MockBusinessService extends BusinessService {
        ArrayList<Business> businesses = new ArrayList<>();

        public MockBusinessService () {
            super(null);

            User businessUser = new User();
            businessUser.setRole(UserRoles.USER);
            businessUser.setEmail("businessUser@800");
            businessUser.setPassword("password");

            Business business = new Business();
            business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
            business.setPrimaryAdministrator(businessUser);
            business.setAddress("27 Mako Road");
            business.setDescription("The best tacos in town");
            business.setName("Mako's Tacos");

            List<User> admins = new ArrayList<>();
            admins.add(businessUser);
            business.setAdministrators(admins);

            List<Business> businessesAdministered = new ArrayList<>();
            businessesAdministered.add(business);

            businessUser.setBusinessesPrimarilyAdministered(businessesAdministered);
        }

        @Override
        public Business createBusiness(Business business) {
            Integer id = businesses.size();
            businesses.add(business);
            business.setId(id);
            return business;
        }

        @Override
        public Business findBusinessById(Integer id) {
            return businesses.get(id);
        }

    }

}
