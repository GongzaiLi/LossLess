package gradle.cucumber;

import com.seng302.wasteless.BusinessControllerIntegrationTest;
import com.seng302.wasteless.Main;
import com.seng302.wasteless.MainTests;
import com.seng302.wasteless.controller.BusinessController;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes= BusinessControllerIntegrationTest.class)
public class MainCucumberRunner {
    @Before
    public void dummy() {}
}
