package com.seng302.wasteless;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties"
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Reset JPA between test
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

    @Test
    public void whenSearchingForUsers_andOneMatchingUsers_thenCorrectResult() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", "14 Tyndale Pl", "Password123");

       mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=James")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id", is(2)))
                .andExpect(jsonPath("[0].firstName", is("James")))
                .andExpect(jsonPath("[0].homeAddress", is("236a Blenheim Road")));
    }

    @Test
    public void whenSearchingForUsers_andNoMatchingUsers_thenCorrectResult() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", "14 Tyndale Pl", "Password123");

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=Steve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertEquals("[]", result);
    }



    @Test
    public void whenSearchingForUsers_andMultipleMatchingUsers_byFullMatch_andPartialMatch_thenCorrectOrder() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");
        createOneUser("Nothing", "James", "jeh@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");

        createOneUser("James123", "Harris", "jeh1281@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");
        createOneUser("Nothing", "James123", "jeh1@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");


        String user1 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1238\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\", \"middleName\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user2 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"12347\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\", \"nickname\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());


        String user3 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1236\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\", \"middleName\": \"James1234\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user3)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user4 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"12345\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\", \"nickname\": \"James1234\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user4)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=James")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id", is(2)))
                .andExpect(jsonPath("[1].id", is(3)))
                .andExpect(jsonPath("[2].id", is(7)))
                .andExpect(jsonPath("[3].id", is(6)))
                .andExpect(jsonPath("[4].id", is(4)))
                .andExpect(jsonPath("[5].id", is(5)))
                .andExpect(jsonPath("[6].id", is(9)))
                .andExpect(jsonPath("[7].id", is(8)));

    }

    @Test
    public void whenGetUserWithIdTwo_andOnlyOneCreatedUser_BesidedDefault_ThenGetCorrectUser() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(2)))
                .andExpect(jsonPath("firstName", is("James")))
                .andExpect(jsonPath("lastName", is("Harris")))
                .andExpect(jsonPath("role", is("user")));
        }

    @Test
    public void whenSearchingForUsers_andMultipleMatchingUsers_byPartial_thenCorrectOrder() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");
        createOneUser("Nothing", "James", "jeh@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");

        String user1 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"123\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\", \"middleName\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user2 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1234\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\", \"nickname\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=Jam")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id", is(2)))
                .andExpect(jsonPath("[1].id", is(3)))
                .andExpect(jsonPath("[2].id", is(5)))
                .andExpect(jsonPath("[3].id", is(4)));


    }

    @Test
    public void whenGetRequestToUsersAndUserDoesntExists_thenCorrectResponse() throws Exception{
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", "14 Tyndale Pl", "Password123");

        mockMvc.perform(get("/users/245")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }


    private void createOneUser(String firstName, String lastName, String email, String dateOfBirth, String homeAddress, String password) {
        String user = String.format("{\"firstName\": \"%s\", \"lastName\" : \"%s\", \"email\": \"%s\", \"dateOfBirth\": \"%s\", \"homeAddress\": \"%s\", \"password\": \"%s\"}", firstName, lastName, email, dateOfBirth, homeAddress, password);

        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/users")
                            .content(user)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }
}