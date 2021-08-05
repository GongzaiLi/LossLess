package com.seng302.wasteless.unitTest.ServiceTests;


import com.seng302.wasteless.repository.BusinessRepository;
import com.seng302.wasteless.service.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.seng302.wasteless.model.*;

import java.util.Collections;


@RunWith(SpringRunner.class)
@WebMvcTest(BusinessService.class)
class BusinessServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private UserService userService;



    @Test
    void whenFindBusinessById_AndInvalidId_ThrowException() {
        Mockito.when(businessRepository.findFirstById(1)).thenReturn(null);
        boolean success = true;
        try {
            BusinessService businessService = new BusinessService(businessRepository);
            businessService.findBusinessById(1);
        } catch (ResponseStatusException e) {
            success = false;
            assertEquals(406, e.getRawStatusCode());
        }
        assert !success;


    }

    @Test
    void whenCheckUUserAdminOfBusinessOrGAA_AndNeither_ThrowException() {
        boolean success = true;
        try {
            BusinessService businessService = new BusinessService(businessRepository);
            Business business = new Business();
            User user = new User();
            user.setRole(UserRoles.USER);
            user.setId(1);
            User admin = new User();
            admin.setId(2);
            business.setPrimaryAdministrator(admin);
            business.setAdministrators(Collections.singletonList(admin));
            businessService.checkUserAdminOfBusinessOrGAA(business, user);
        } catch (ResponseStatusException e) {
            success = false;
            assertEquals(403, e.getRawStatusCode());
        }
        assert !success;


    }
    @Test
    void whenCheckUUserAdminOfBusinessOrGAA_AndUserAdmin_ThrowException() {
        boolean success = true;
        try {
            BusinessService businessService = new BusinessService(businessRepository);
            Business business = new Business();
            User user = new User();
            user.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);
            user.setId(1);
            User admin = new User();
            admin.setId(2);
            business.setPrimaryAdministrator(admin);
            businessService.checkUserAdminOfBusinessOrGAA(business, user);
        } catch (ResponseStatusException e) {
            success = false;
        }
        assert success;


    }
    @Test
    void whenCheckUUserAdminOfBusinessOrGAA_AndUserBusinessAdmin_ThrowException() {
        boolean success = true;
        try {
            BusinessService businessService = new BusinessService(businessRepository);
            Business business = new Business();
            User user = new User();
            user.setRole(UserRoles.USER);
            user.setId(1);
            business.setPrimaryAdministrator(user);
            businessService.checkUserAdminOfBusinessOrGAA(business, user);
        } catch (ResponseStatusException e) {
            success = false;
        }
        assert success;


    }
    @Test
    void whenCheckUUserAdminOfBusinessOrGAA_AndBoth_ThrowException() {
        boolean success = true;
        try {
            BusinessService businessService = new BusinessService(businessRepository);
            Business business = new Business();
            User user = new User();
            user.setRole(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN);
            user.setId(1);
            business.setPrimaryAdministrator(user);
            businessService.checkUserAdminOfBusinessOrGAA(business, user);
        } catch (ResponseStatusException e) {
            success = false;
        }
        assert success;


    }




}

