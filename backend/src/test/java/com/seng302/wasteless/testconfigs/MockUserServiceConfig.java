package com.seng302.wasteless.testconfigs;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.service.UserService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * This is a configuration file for a mocked UserService bean.
 * It contains the class definition for MockUserService, a mock UserService
 * (see doc for MockUserService for more details).
 *
 * To use this configuration in unit tests, put @Import(MockUserServiceConfig.class)
 * above the test class. This will allow the test to use the mocked UserService bean.
 */

@TestConfiguration
public class MockUserServiceConfig {


    /**
     * Mock UserService class for testing purposes.
     * The mock UserService is implemented as a simple Array of users,
     * with three existing accounts. The emails are:
     * - defaultadmin@700
     * - admin@700
     * - user@700
     * The emails have the respective roles (default global app admin, global app admin, user).
     * Additionally, those users have the ids 0, 1, 2 respectively. New users will have ids starting from 3.
     */
    public static class MockUserService extends UserService {
        ArrayList<User> users = new ArrayList<>();


        public MockUserService () {
            super(null, null, null, null);
            User defaultAdmin = new User();
            defaultAdmin.setRole(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN);
            defaultAdmin.setEmail("defaultadmin@700");
            defaultAdmin.setPassword("password");
            defaultAdmin.setDateOfBirth(LocalDate.now().minusYears(17));

            User user = new User();
            user.setRole(UserRoles.USER);
            user.setEmail("user@700");
            user.setPassword("password");
            user.setDateOfBirth(LocalDate.now().minusYears(17));

            User admin = new User();
            admin.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);
            admin.setEmail("admin@700");
            admin.setPassword("password");
            admin.setDateOfBirth(LocalDate.now().minusYears(17));

            User user2 = new User();
            user2.setRole(UserRoles.USER);
            user2.setEmail("user2@700");
            user2.setPassword("password");
            user2.setDateOfBirth(LocalDate.now().minusYears(17));

            User user3 = new User();
            user3.setRole(UserRoles.USER);
            user3.setEmail("user3@700");
            user3.setPassword("password");
            user3.setDateOfBirth(LocalDate.now().minusYears(14));

            createUser(defaultAdmin);
            createUser(admin);
            createUser(user);
            createUser(user2);
            createUser(user3);
        }

        @Override
        public boolean checkEmailAlreadyUsed(String email) {
            return email.equals("defaultadmin@700");
        }

        @Override
        public User findUserByEmail(String email) {
            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    return user;
                }
            }
            return null;
        }

        @Override
        public User findUserById(Integer id) {
            if (id < users.size()) {
                return users.get(id);
            } else {
                return null;
            }

        }

        @Override
        public User createUser(User user) {
            Integer id = users.size();
            users.add(user);
            user.setId(id);
            return user;
        }

        @Override
        public void addBusinessPrimarilyAdministered(User user, Business business) {
        }

        @Override
        public void updateUser(User user) {
            users.set(user.getId(), user);
        }

        @Override
        public void saveUserChanges(User user) {
            users.set(user.getId(), user);
        }

        @Override
        public boolean checkUserAdminsBusiness(Integer businessId, Integer userId) {
            return userId.equals(1);
        }
    }

    @Bean
    public UserService userService() {
        return new MockUserService();
    }

}
