package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;



/**
 * Service for automatic default admin creation.
 */
@Component
@EnableScheduling
@PropertySource("classpath:global-admin.properties")
public class DefaultAdminCreatorService {
    private static final String CONFIG_FILE_PATH = "global-admin.properties";
    private static final Logger log = LoggerFactory.getLogger(DefaultAdminCreatorService.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private AtomicInteger count = new AtomicInteger(0);

    private String defaultEmail;
    private String defaultPassword;
    private Integer defaultTimeout;

    private AddressService addressService;
    private UserService userService;

    /**
     * Creates a new Creator service using parameters from a config file (resources/global-admin.properties).
     * This service periodically checks if a default admin exists and creates one if one does not.
     * @param userService The User Service autowired by Spring
     * @throws IOException If the config file "global-admin.properties" does not exist in the resources/ directory
     * @throws InvalidParameterException If the config file does not have all of the properties: 'default-admin-username', 'default-admin-password', and 'check-default-admin-period-ms'
     */
    @Autowired
    public DefaultAdminCreatorService(UserService userService, AddressService addressService) throws IOException, InvalidParameterException {


        InputStream configFileStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_PATH);
        if (configFileStream == null) {
            throw new IOException("Did not find global-admin.properties file in the resources directory");
        }
        this.readConfigFile(configFileStream);
        configFileStream.close();
        this.userService = userService;
        this.addressService = addressService;

        scheduleCheckDefaultAdmin();
    }

    /**
     * Takes an input stream (made from the config properties file) and reads from it the following config properties:
     * 'default-admin-username', 'default-admin-password', and 'check-default-admin-period-ms'
     * Stores these values to be used for the default admin creation/checking service.
     * DOES NOT CLOSE THE FILE STREAM AFTER READING. MAKE SURE YOU CLOSE IT AFTER CALLING THIS METHOD
     * @param configFileStream Input stream made from the config properties file
     * @throws InvalidParameterException If the config file does not have all of the properties: 'default-admin-username', 'default-admin-password', and 'check-default-admin-period-ms'
     * @throws IOException If reading the config file failed due to an external error, eg. the file was corrupted
     */
    private void readConfigFile(InputStream configFileStream) throws InvalidParameterException, IOException {
        Properties defaultProps = new Properties();
        defaultProps.load(configFileStream);

        defaultEmail = defaultProps.getProperty("default-admin-username");
        defaultPassword = defaultProps.getProperty("default-admin-password");
        String defaultTimeoutString = defaultProps.getProperty("check-default-admin-period-ms");

        if (defaultPassword == null || defaultEmail == null || defaultTimeoutString == null) {
            throw new InvalidParameterException("Missing config fields. Must have 'default-admin-username', 'default-admin-password', and 'check-default-admin-period-ms'");
        }
        try {
            defaultTimeout = Integer.parseInt(defaultTimeoutString);
        } catch(NumberFormatException nfe) {
            throw new InvalidParameterException("'check-default-admin-period-ms' field of config file does not have an integer value.");
        }

        if (defaultTimeout <= 0) {
            throw new InvalidParameterException("'check-default-admin-period-ms' must be greater than zero.");
        }
    }

    /**
     * Creates a new default admin, using the credentials given in the config file.
     */
    private void createDefaultAdmin() {
        User defaultAdmin = new User();
        defaultAdmin.setEmail(defaultEmail);
        defaultAdmin.setPassword(new BCryptPasswordEncoder().encode(defaultPassword));

        Address address = new Address();
        address.setCountry("UK");
        address.setCity("London");
        address.setStreetNumber("10");
        address.setStreetName("Downing St");
        address.setPostcode("SW1A2AA");

        addressService.createAddress(address);

        defaultAdmin.setHomeAddress(address);
        defaultAdmin.setDateOfBirth(LocalDate.EPOCH);
        defaultAdmin.setFirstName("Default");
        defaultAdmin.setLastName("Admin");
        defaultAdmin.setRole(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN);
        defaultAdmin.setCreated(LocalDate.now());

        userService.createUser(defaultAdmin);
    }

    /**
     * This is a scheduled task that starts 10 seconds after start-up. It periodically checks
     * whether an DGAA exists inside the database, if it doesn't it logs that a DGAA does not exist
     * and creates one using DefaultAdminCreatorService. It periodically runs depending on what is
     * set in the global-admin.properties. Currently set to 5 seconds
     */
    @Scheduled(fixedDelayString = "${check-default-admin-period-ms}")
    public void scheduleCheckDefaultAdmin() {
        log.info("[SERVER] DGAA Check: {}", dateFormat.format(new Date()));
        this.count.incrementAndGet();
        if (!userService.checkRoleAlreadyExists(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)) {
            log.info("[SERVER] DGAA (404), creating DGAA...");
            createDefaultAdmin();
        }
    }

    /**
     * Used for testing schedule
     * @return getInvocationCount a Atomic Integer that increments
     */
    public int getInvocationCount() {
        return this.count.get();
    }
}
