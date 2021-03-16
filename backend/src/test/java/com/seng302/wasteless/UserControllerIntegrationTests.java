package com.seng302.wasteless;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    public void whenSearchingForUsers_andOneMatchingUsers_thenCorrectResult() throws Exception {
        Cookie userCookie = createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", "14 Tyndale Pl", "Password123");

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=James")
                        .cookie(userCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertEquals("[{\"id\":1,\"firstName\":\"James\",\"lastName\":\"Harris\",\"middleName\":null,\"nickname\":null,\"bio\":null,\"homeAddress\":\"236a Blenheim Road\"}]", result);
    }

    @Test
    public void whenSearchingForUsers_andNoMatchingUsers_thenCorrectResult() throws Exception {
        Cookie userCookie = createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", "14 Tyndale Pl", "Password123");

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=Steve")
                        .cookie(userCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertEquals("[]", result);
    }



    @Test
    public void whenSearchingForUsers_andMultipleMatchingUsers_byFullMatch_andPartialMatch_thenCorrectOrder() throws Exception {
        Cookie userCookie = createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");
        createOneUser("Nothing", "James", "jeh@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");

        createOneUser("James123", "Harris", "jeh1281@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");
        createOneUser("Nothing", "James123", "jeh1@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");


        String user1 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1238\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\", \"middleName\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .cookie(userCookie)
                        .content(user1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user2 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"12347\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\", \"nickname\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .cookie(userCookie)
                        .content(user2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());


        String user3 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1236\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\", \"middleName\": \"James1234\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .cookie(userCookie)
                        .content(user3)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user4 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"12345\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\", \"nickname\": \"James1234\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .cookie(userCookie)
                        .content(user4)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=James")
                        .cookie(userCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertEquals("[{\"id\":1,\"firstName\":\"James\",\"lastName\":\"Harris\",\"middleName\":null,\"nickname\"" +
                ":null,\"bio\":null,\"homeAddress\":\"236a Blenheim Road\"},{\"id\":2,\"firstName\":\"Nothing\",\"lastName\"" +
                ":\"James\",\"middleName\":null,\"nickname\":null,\"bio\":null,\"homeAddress\":\"236a Blenheim Road\"}" +
                ",{\"id\":6,\"firstName\":\"Nothing\",\"lastName\":\"Nothing\",\"middleName\":null,\"nickname\":\"James\"," +
                "\"bio\":null,\"homeAddress\":\"236a Blenheim Road\"},{\"id\":5,\"firstName\":\"Nothing\",\"lastName\":\"Nothing\"," +
                "\"middleName\":\"James\",\"nickname\":null,\"bio\":null,\"homeAddress\":\"236a Blenheim Road\"},{\"id\":3," +
                "\"firstName\":\"James123\",\"lastName\":\"Harris\",\"middleName\":null,\"nickname\":null,\"bio\":null," +
                "\"homeAddress\":\"236a Blenheim Road\"},{\"id\":4,\"firstName\":\"Nothing\",\"lastName\":\"James123\"," +
                "\"middleName\":null,\"nickname\":null,\"bio\":null,\"homeAddress\":\"236a Blenheim Road\"},{\"id\":8," +
                "\"firstName\":\"Nothing\",\"lastName\":\"Nothing\",\"middleName\":null,\"nickname\":\"James1234\"," +
                "\"bio\":null,\"homeAddress\":\"236a Blenheim Road\"},{\"id\":7,\"firstName\":\"Nothing\",\"lastName\"" +
                ":\"Nothing\",\"middleName\":\"James1234\",\"nickname\":null,\"bio\":null,\"homeAddress\":" +
                "\"236a Blenheim Road\"}]", result);
    }


    @Test
    public void whenSearchingForUsers_andMultipleMatchingUsers_byPartial_thenCorrectOrder() throws Exception {
        Cookie userCookie = createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");
        createOneUser("Nothing", "James", "jeh@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");

        String user1 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"123\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\", \"middleName\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .cookie(userCookie)
                        .content(user1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user2 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1234\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\", \"nickname\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .cookie(userCookie)
                        .content(user2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=Jam")
                        .cookie(userCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertEquals("[{\"id\":1,\"firstName\":\"James\",\"lastName\":\"Harris\",\"middleName\":null," +
                "\"nickname\":null,\"bio\":null,\"homeAddress\":\"236a Blenheim Road\"},{\"id\":2,\"firstName\":" +
                "\"Nothing\",\"lastName\":\"James\",\"middleName\":null,\"nickname\":null,\"bio\":null,\"homeAddress\"" +
                ":\"236a Blenheim Road\"},{\"id\":4,\"firstName\":\"Nothing\",\"lastName\":\"Nothing\",\"middleName\"" +
                ":null,\"nickname\":\"James\",\"bio\":null,\"homeAddress\":\"236a Blenheim Road\"},{\"id\":3,\"firstName\"" +
                ":\"Nothing\",\"lastName\":\"Nothing\",\"middleName\":\"James\",\"nickname\":null,\"bio\":null,\"homeAddress\"" +
                ":\"236a Blenheim Road\"}]", result);
    }



    @Test
    public void whenGetRequestToUsersAndUserDoesntExists_thenCorrectResponse() throws Exception{
        Cookie userCookie = createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", "14 Tyndale Pl", "Password123");

        mockMvc.perform(get("/users/245")
                .cookie(userCookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    private Cookie createOneUser(String firstName, String lastName, String email, String dateOfBirth, String homeAddress, String password) {
        String user = String.format("{\"firstName\": \"%s\", \"lastName\" : \"%s\", \"email\": \"%s\", \"dateOfBirth\": \"%s\", \"homeAddress\": \"%s\", \"password\": \"%s\"}", firstName, lastName, email, dateOfBirth, homeAddress, password);

        try {
            MvcResult result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/users")
                            .content(user)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

            return result.getResponse().getCookie("JSESSIONID");

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }
}
