package com.seng302.wasteless;

import com.seng302.wasteless.Security.CustomUserDetails;
import com.seng302.wasteless.User.User;
import com.seng302.wasteless.User.UserRoles;
import com.seng302.wasteless.User.UserService;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;

@TestConfiguration
public class MockUserDetailsService {

    public class MockCustomUserDetailsService implements UserDetailsService {
        User defaultAdmin;
        User user;

        public MockCustomUserDetailsService () {
            defaultAdmin = new User();
            defaultAdmin.setRole(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN);
            defaultAdmin.setEmail("defaultadmin@700");
            defaultAdmin.setPassword("password");

            user = new User();
            user.setRole(UserRoles.USER);
            user.setEmail("user@700");
            user.setPassword("password");
        }

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            if (email.equals("defaultadmin@700")) {
                return new CustomUserDetails(defaultAdmin);
            } else if (email.equals("user@700")) {
                return new CustomUserDetails(user);
            } else {
                throw new UsernameNotFoundException("Username not found");
            }
        }
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return new MockCustomUserDetailsService();
    }
}