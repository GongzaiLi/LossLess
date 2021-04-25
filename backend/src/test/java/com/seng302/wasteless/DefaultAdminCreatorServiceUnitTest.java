package com.seng302.wasteless;


import com.seng302.wasteless.service.DefaultAdminCreatorService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class DefaultAdminCreatorServiceUnitTest {
    DefaultAdminCreatorService creatorService;

    @Mock
    private UserService userService;

    @BeforeEach
    public void init() throws IOException {
        creatorService = new DefaultAdminCreatorService(userService);
    }

    @Test
    public void correctFieldsWhenReadConfigFile() {
        String config = "check-default-admin-period-seconds=60\n" +
                "default-admin-username=admin@sengmail.com\n" +
                "default-admin-password=supersecurepassword";

        ReflectionTestUtils.invokeMethod(creatorService, "readConfigFile", new ByteArrayInputStream(config.getBytes()));

        Assertions.assertEquals(ReflectionTestUtils.getField(creatorService, "defaultEmail"), "admin@sengmail.com");
        Assertions.assertEquals(ReflectionTestUtils.getField(creatorService, "defaultPassword"), "supersecurepassword");
        Assertions.assertEquals(ReflectionTestUtils.getField(creatorService, "defaultTimeout"), 60);
    }

    @Test
    public void throwsInvalidParameterExceptionWhenPeriodNotANumber() {
        String config = "check-default-admin-period-seconds=asdf\n" +
                "default-admin-username=admin@sengmail.com\n" +
                "default-admin-password=supersecurepassword";

        assertThrows(InvalidParameterException.class, () -> ReflectionTestUtils.invokeMethod(creatorService, "readConfigFile", new ByteArrayInputStream(config.getBytes())));
    }

    @Test
    public void throwsInvalidParameterExceptionWhenPeriodIsZero() {
        String config = "check-default-admin-period-seconds=0\n" +
                "default-admin-username=admin@sengmail.com\n" +
                "default-admin-password=supersecurepassword";

        assertThrows(InvalidParameterException.class, () -> ReflectionTestUtils.invokeMethod(creatorService, "readConfigFile", new ByteArrayInputStream(config.getBytes())));
    }

    @Test
    public void throwsInvalidParameterExceptionWhenPeriodIsNegative() {
        String config = "check-default-admin-period-seconds=-5\n" +
                "default-admin-username=admin@sengmail.com\n" +
                "default-admin-password=supersecurepassword";

        assertThrows(InvalidParameterException.class, () -> ReflectionTestUtils.invokeMethod(creatorService, "readConfigFile", new ByteArrayInputStream(config.getBytes())));
    }

    @Test
    public void throwsInvalidParameterExceptionWhenConfigEmpty() {
        String config = "";

        assertThrows(InvalidParameterException.class, () -> ReflectionTestUtils.invokeMethod(creatorService, "readConfigFile", new ByteArrayInputStream(config.getBytes())));
    }
}
