package com.seng302.wasteless;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //Reset JPA stuff between test
public class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenLoggingIntoAccountWithIncorrectRequestBody() {
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", "14 Tyndale Pl", "Password123");
        String login = "{\"username\": \"wrongemail@uclive.ac.nz\", \"pass\" : \"Password123\"}";

        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/login")
                            .content(login)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }

    @Test
    public void whenLoggingIntoAccountThatDoesNotExist() {
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", "14 Tyndale Pl", "Password123");
        String login = "{\"email\": \"wrongemail@uclive.ac.nz\", \"password\" : \"Password123\"}";

        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/login")
                            .content(login)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }

    @Test
    public void whenLoggingIntoAccountWithIncorrectPassword() {
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", "14 Tyndale Pl", "Password123");
        String login = "{\"email\": \"ojc31@uclive.ac.nz\", \"password\" : \"wrongPassword\"}";

        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/login")
                            .content(login)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }

    @Test
    public void whenLoggingIntoAccountWithCorrectPassword() {
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", "14 Tyndale Pl", "Password123");
        String login = "{\"email\": \"ojc31@uclive.ac.nz\", \"password\" : \"Password123\"}";

        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/login")
                            .content(login)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }

    private void createOneUser(String firstName, String lastName, String email, String dateOfBirth, String homeAddress, String password) {
        String user = String.format("{\"firstName\": \"%s\", \"lastName\" : \"%s\", \"email\": \"%s\", \"dateOfBirth\": \"%s\", \"homeAddress\": \"%s\", \"password\": \"%s\"}", firstName, lastName, email, dateOfBirth, homeAddress, password);

        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/users")
                            .content(user)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated());

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }
}
