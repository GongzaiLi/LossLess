package com.seng302.wasteless.unitTest.ServiceTests;


import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.DefaultAdminCreatorService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
 class DefaultAdminCreatorServiceUnitTest {
    DefaultAdminCreatorService creatorService;

    @Mock
    private UserService userService;

    @Mock
    private AddressService addressService;

    @BeforeEach
     void init() throws IOException {
        creatorService = new DefaultAdminCreatorService(userService, addressService);
    }

    @Test
     void correctFieldsWhenReadConfigFile() {
        String config = "check-default-admin-period-ms=60\n" +
                "default-admin-username=admin@sengmail.com\n" +
                "default-admin-password=supersecurepassword";

        ReflectionTestUtils.invokeMethod(creatorService, "readConfigFile", new ByteArrayInputStream(config.getBytes()));

        Assertions.assertEquals("admin@sengmail.com", ReflectionTestUtils.getField(creatorService, "defaultEmail"));
        Assertions.assertEquals("supersecurepassword", ReflectionTestUtils.getField(creatorService, "defaultPassword"));
        Assertions.assertEquals(60, ReflectionTestUtils.getField(creatorService, "defaultTimeout"));
    }

   /**
    * Test 1: PeriodNotANumber
    * Test 2: PeriodIsZero
    * Test 3: PeriodIsNegative
    */
    @ParameterizedTest
    @ValueSource(strings = {
            "check-default-admin-period-ms=asdf\ndefault-admin-username=admin@sengmail.com\ndefault-admin-password=supersecurepassword",
            "check-default-admin-period-ms=0\ndefault-admin-username=admin@sengmail.com\ndefault-admin-password=supersecurepassword",
            "check-default-admin-period-ms=-5\ndefault-admin-username=admin@sengmail.com\ndefault-admin-password=supersecurepassword"})
     void throwsInvalidParameterExceptionWhenPeriodInvalid(String config) {
        ByteArrayInputStream bytes = new ByteArrayInputStream(config.getBytes());
        assertThrows(InvalidParameterException.class, () -> ReflectionTestUtils.invokeMethod(creatorService, "readConfigFile", bytes));
    }


    @Test
     void throwsInvalidParameterExceptionWhenConfigEmpty() {
        String config = "";
        ByteArrayInputStream bytes = new ByteArrayInputStream(config.getBytes());
        assertThrows(InvalidParameterException.class, () -> ReflectionTestUtils.invokeMethod(creatorService, "readConfigFile", bytes));
    }
}
