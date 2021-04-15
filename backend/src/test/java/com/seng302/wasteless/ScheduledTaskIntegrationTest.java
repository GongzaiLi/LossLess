package com.seng302.wasteless;

import com.seng302.wasteless.service.DefaultAdminCreatorService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ScheduledTaskIntegrationTest {

    @Autowired
    DefaultAdminCreatorService creatorService;

    @Test
    public void checkIfScheduledMethodIsCalledWithCreatorService() throws InterruptedException{
        Thread.sleep(15000);
        assertEquals(creatorService.getInvocationCount() > 0, true);
    }
}
