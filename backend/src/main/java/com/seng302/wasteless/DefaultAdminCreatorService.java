package com.seng302.wasteless;

import com.seng302.wasteless.Security.WebSecurityConfig;
import com.seng302.wasteless.User.User;
import com.seng302.wasteless.User.UserRoles;
import com.seng302.wasteless.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Properties;

/**
 * Service for automatic default admin creation.
 */
@Component
public class DefaultAdminCreatorService {
    private String defaultEmail;
    private String defaultPassword;
    private String defaultTimeout;

    private UserService userService;

    /**
     * Creates a new Creator service using parameters from a config file (resources/global-admin.properties).
     * This service periodically checks if a default admin exists and creates one if one does not.
     * @param userService The User Service autowired by Spring
     * @throws IOException If the config file "global-admin.properties" does not exist in te resources/ directory
     * @throws InvalidParameterException If the config file does not have all of the properties: 'default-admin-username', 'default-admin-password', and 'check-default-admin-period-seconds'
     */
    @Autowired
    public DefaultAdminCreatorService(UserService userService) throws IOException, InvalidParameterException {
        this();
        this.userService = userService;

        createDefaultAdmin();
    }

    /**
     * Creates a new Creator service using parameters from a config file (resources/global-admin.properties).
     * DO NOT CALL THIS METHOD!! This is only used by the public DefaultAdminCreatorService constructor.
     * @throws IOException If the config file "global-admin.properties" does not exist in te resources/ directory
     * @throws InvalidParameterException If the config file does not have all of the properties: 'default-admin-username', 'default-admin-password', and 'check-default-admin-period-seconds'
     */
    private DefaultAdminCreatorService() throws IOException, InvalidParameterException {
        Properties defaultProps = new Properties();
        InputStream configFile = getClass().getClassLoader().getResourceAsStream("global-admin.properties");
        defaultProps.load(configFile);
        configFile.close();

        defaultEmail = defaultProps.getProperty("default-admin-username");
        defaultPassword = defaultProps.getProperty("default-admin-password");
        defaultTimeout = defaultProps.getProperty("check-default-admin-period-seconds");

        if (defaultPassword == null || defaultEmail == null || defaultTimeout == null) {
            throw new InvalidParameterException("Missing config fields. Must have 'default-admin-username', 'default-admin-password', and 'check-default-admin-period-seconds'");
        }
    }

    /**
     * Creates a new default admin, using the credentials given in the config file.
     */
    private void createDefaultAdmin() {
        User defaultAdmin = new User();
        defaultAdmin.setEmail(defaultEmail);
        defaultAdmin.setPassword(new BCryptPasswordEncoder().encode(defaultPassword));
        defaultAdmin.setHomeAddress("10 Downing Street");
        defaultAdmin.setDateOfBirth(LocalDate.EPOCH);
        defaultAdmin.setFirstName("Default");
        defaultAdmin.setLastName("Admin");
        defaultAdmin.setRole(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN);

        userService.createUser(defaultAdmin);
    }
}
