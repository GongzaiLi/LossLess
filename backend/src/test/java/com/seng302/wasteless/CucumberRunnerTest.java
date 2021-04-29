package com.seng302.wasteless;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty"}, // How to format test report, "pretty" is good for human eyes
        glue = {"com.seng302.wasteless.steps"}, // Where to look for your tests' steps
        features = {"src/test/resources/features/"}, // Where to look for your features
        publish = true
)
public class CucumberRunnerTest { } // Classname ends with "Test" so it will be picked up by JUnit and hence by 'gradle test'


