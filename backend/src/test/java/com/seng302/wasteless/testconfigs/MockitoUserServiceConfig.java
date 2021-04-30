package com.seng302.wasteless.testconfigs;

import com.seng302.wasteless.service.UserService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * A very very basic config that allows a Mockito mocked UserService bean to be injected into the tests.
 * If you're using Mockito in the test then @Import this config, and NOT the MockUserServiceConfig class.
 */
@Configuration
public class MockitoUserServiceConfig {
    @Bean
    @Primary
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }
}
