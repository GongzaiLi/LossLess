package com.seng302.wasteless;

import com.seng302.wasteless.service.DefaultAdminCreatorService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@PropertySource("classpath:global-admin.properties")
@AutoConfigureMockMvc
public class ScheduledTaskIntegrationTest {

    @Autowired
    DefaultAdminCreatorService creatorService;

    @Value("${check-default-admin-period-seconds}")
    private int wait_time;

    @Test
    public void checkIfScheduledMethodIsCalledWithCreatorService() throws InterruptedException{
        Thread.sleep(wait_time + 10000);
        assertEquals(creatorService.getInvocationCount() > 0, true);
    }
}
