package com.seng302.wasteless.service;

import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.Properties;

/**
 * Service for automatic default admin creation.
 */
@Component
public class DefaultAdminCreatorService {
    private static final String CONFIG_FILE_PATH = "global-admin.properties";

    private String defaultEmail;
    private String defaultPassword;
    private Integer defaultTimeout;

    private UserService userService;

    /**
     * Creates a new Creator service using parameters from a config file (resources/global-admin.properties).
     * This service periodically checks if a default admin exists and creates one if one does not.
     * @param userService The User Service autowired by Spring
     * @throws IOException If the config file "global-admin.properties" does not exist in the resources/ directory
     * @throws InvalidParameterException If the config file does not have all of the properties: 'default-admin-username', 'default-admin-password', and 'check-default-admin-period-seconds'
     */
    @Autowired
    public DefaultAdminCreatorService(UserService userService) throws IOException, InvalidParameterException {
        InputStream configFileStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_PATH);
        if (configFileStream == null) {
            throw new IOException("Did not find global-admin.properties file in the resources directory");
        }
        this.readConfigFile(configFileStream);
        configFileStream.close();
        this.userService = userService;

        createDefaultAdmin();
    }

    /**
     * Takes an input stream (made from the config properties file) and reads from it the following config properties:
     * 'default-admin-username', 'default-admin-password', and 'check-default-admin-period-seconds'
     * Stores these values to be used for the default admin creation/checking service.
     * DOES NOT CLOSE THE FILE STREAM AFTER READING. MAKE SURE YOU CLOSE IT AFTER CALLING THIS METHOD
     * @param configFileStream Input stream made from the config properties file
     * @throws InvalidParameterException If the config file does not have all of the properties: 'default-admin-username', 'default-admin-password', and 'check-default-admin-period-seconds'
     * @throws IOException If reading the config file failed due to an external error, eg. the file was corrupted
     */
    private void readConfigFile(InputStream configFileStream) throws InvalidParameterException, IOException {
        Properties defaultProps = new Properties();
        defaultProps.load(configFileStream);

        defaultEmail = defaultProps.getProperty("default-admin-username");
        defaultPassword = defaultProps.getProperty("default-admin-password");
        String defaultTimeoutString = defaultProps.getProperty("check-default-admin-period-seconds");

        if (defaultPassword == null || defaultEmail == null || defaultTimeoutString == null) {
            throw new InvalidParameterException("Missing config fields. Must have 'default-admin-username', 'default-admin-password', and 'check-default-admin-period-seconds'");
        }
        try {
            defaultTimeout = Integer.parseInt(defaultTimeoutString);
        } catch(NumberFormatException nfe) {
            throw new InvalidParameterException("'check-default-admin-period-seconds' field of config file does not have an integer value.");
        }

        if (defaultTimeout <= 0) {
            throw new InvalidParameterException("'check-default-admin-period-seconds' must be greater than zero.");
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
