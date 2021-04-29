package com.seng302.wasteless;

import com.seng302.wasteless.controller.BusinessController;
import com.seng302.wasteless.interceptors.ActAsBusinessInterceptor;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.testconfigs.MockBusinessServiceConfig;
import com.seng302.wasteless.testconfigs.MockUserServiceConfig;
import com.seng302.wasteless.testconfigs.MocksApplication;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.AssertTrue;
import java.io.IOException;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes=MocksApplication.class)
@Import({MockBusinessServiceConfig.class, MockUserServiceConfig.class})
public class ActAsBusinessInterceptorUnitTest {
    @Autowired
    private BusinessService businessService;

    @Autowired
    private UserService userService;

    @Before
    public void setUp() {
        System.out.println("ADSADSADSADSAD");
    }

    @Test
    @WithUserDetails("user@700")
    public void worksForValidUserAndBusiness() throws IOException {/*
        System.out.println(userService.findUserByEmail("user@700"));
        System.out.println(userService);
        ActAsBusinessInterceptor blah = new ActAsBusinessInterceptor();
        MockHttpServletRequest request = new MockHttpServletRequest("GET","/urlpath/soemname");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Assertions.assertTrue(blah.preHandle(request, response, request));*/
    }


}
