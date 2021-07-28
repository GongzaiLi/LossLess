package com.seng302.wasteless.integrationTest;

import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.testconfigs.WithMockCustomUser;
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
import static org.hamcrest.Matchers.hasSize;
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
 class UserControllerIntegrationTests {

    String homeAddress = "{\n" +
            "    \"streetNumber\": \"3/24\",\n" +
            "    \"streetName\": \"Ilam Road\",\n" +
            "    \"suburb\": \"Riccarton\",\n" +
            "    \"city\": \"Christchurch\",\n" +
            "    \"region\": \"Canterbury\",\n" +
            "    \"country\": \"New Zealand\",\n" +
            "    \"postcode\": \"90210\"\n" +
            "  }";


    @Autowired
    private MockMvc mockMvc;

    @Test
     void whenLoggingIntoAccountWithIncorrectRequestBody() {
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", homeAddress, "Password123");
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
     void whenLoggingIntoAccountThatDoesNotExist() {
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", homeAddress, "Password123");
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
     void whenLoggingIntoAccountWithIncorrectPassword() {
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", homeAddress, "Password123");
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
     void whenLoggingIntoAccountWithCorrectPassword() {
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", homeAddress, "Password123");
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
     void whenSearchingForUsers_andOneMatchingUsers_thenCorrectResult() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", homeAddress, "Password123");

       mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=James&offset=0&count=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("results.[0].id", is(2)))
                .andExpect(jsonPath("results.[0].firstName", is("James")))
                .andExpect(jsonPath("results.[0].email", is("jeh128@uclive.ac.nz")));
    }

    @Test
     void whenSearchingForUsers_andNoMatchingUsers_thenCorrectResult() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", homeAddress, "Password123");

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=Steve&offset=0&count=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertEquals("{\"results\":[],\"totalItems\":0}", result);
    }



    @Test
     void whenSearchingForUsers_andMultipleMatchingUsers_byFullMatch_andPartialMatch_thenCorrectOrder() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");
        createOneUser("Nothing", "James", "jeh@uclive.ac.nz", "2000-10-27", homeAddress, "1337");

        createOneUser("James123", "Harris", "jeh1281@uclive.ac.nz", "2000-10-27", homeAddress, "1337");
        createOneUser("Nothing", "James123", "jeh1@uclive.ac.nz", "2000-10-27", homeAddress, "1337");


        String user1 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1238@FSF\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"middleName\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user2 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1234@FSF7\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"nickname\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());


        String user3 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1236@FSF\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"middleName\": \"James1234\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user3)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user4 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"12345@efsf\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"nickname\": \"James1234\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user4)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=James&offset=0&count=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("results.[0].id", is(2)))
                .andExpect(jsonPath("results.[1].id", is(3)))
                .andExpect(jsonPath("results.[2].id", is(4)))
                .andExpect(jsonPath("results.[3].id", is(5)))
                .andExpect(jsonPath("results.[4].id", is(6)))
                .andExpect(jsonPath("results.[5].id", is(7)))
                .andExpect(jsonPath("results.[6].id", is(8)))
                .andExpect(jsonPath("results.[7].id", is(9)));

    }

    @Test
     void whenGetUserWithIdTwo_andOnlyOneCreatedUser_BesidedDefault_ThenGetCorrectUser() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");

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
    void whenGetUserWithIdThree_andTwoCreatedUser_andUserHimSelfLoggedIn_BesidedDefault_ThenGetCorrectUserRoleAndAddress() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", homeAddress, "Password123");

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(3)))
                .andExpect(jsonPath("firstName", is("Oliver")))
                .andExpect(jsonPath("lastName", is("Cranshaw")))
                .andExpect(jsonPath("role", is("user")))
                .andExpect(jsonPath("homeAddress.streetNumber", is("3/24")))
                .andExpect(jsonPath("homeAddress.country", is("New Zealand")));
        }

    @Test
    void whenGetUserWithIdTwo_andTwoCreatedUser_andUserHimselfNotLoggedIn_BesidedDefault_ThenGetNoUserRoleAndAddress() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");
        createOneUser("Oliver", "Cranshaw", "ojc31@uclive.ac.nz", "2000-11-11", homeAddress, "Password123");

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(2)))
                .andExpect(jsonPath("role").doesNotExist())
                .andExpect(jsonPath("homeAddress.streetNumber").doesNotExist())
                .andExpect(jsonPath("homeAddress.city", is("Christchurch")))
                .andExpect(jsonPath("homeAddress.country", is("New Zealand")));
    }

    @Test
    @WithMockCustomUser(email = "test@700", role = UserRoles.GLOBAL_APPLICATION_ADMIN)
    void whenGetUserWithIdOne_andAdminLoggedIn_andAdminLoggedIn_BesidedDefault_ThenGetCorrectUserRole() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())                    //check number of attributes
                .andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("role", is("defaultGlobalApplicationAdmin")))
                .andExpect(jsonPath("homeAddress").exists())
                .andExpect(jsonPath("city").doesNotExist());
    }


    @Test
    @WithMockCustomUser(email = "test@700", role = UserRoles.GLOBAL_APPLICATION_ADMIN) //fails without this?
     void whenGetRequestToUsersAndUserExists_thenCorrectResponse() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
     void whenSearchingForUsers_andMultipleMatchingUsers_byPartial_thenCorrectOrder() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");
        createOneUser("Nothing", "James", "jeh@uclive.ac.nz", "2000-10-27", homeAddress, "1337");

        String user1 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"123@123\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"middleName\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user2 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1234@FSF\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"nickname\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=Jam&offset=0&count=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("results.[0].id", is(2)))
                .andExpect(jsonPath("results.[1].id", is(3)))
                .andExpect(jsonPath("results.[2].id", is(4)))
                .andExpect(jsonPath("results.[3].id", is(5)));


    }

    @Test
    void whenSearchingForUsers_WithOneCountAndZeroOffset_ReturnsTotalResults() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");
        createOneUser("Nothing", "James", "jeh@uclive.ac.nz", "2000-10-27", homeAddress, "1337");

        String user1 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"123@123\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"middleName\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user2 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1234@FSF\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"nickname\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=Jam&offset=0&count=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("results", hasSize(1)))
                .andExpect(jsonPath("totalItems", is(4)));


    }

    @Test
    void whenSearchingForUsers_WithCountLessThanTotal_OnlyReturnsCount() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");
        createOneUser("Nothing", "James", "jeh@uclive.ac.nz", "2000-10-27", homeAddress, "1337");

        String user1 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"123@123\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"middleName\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user2 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1234@FSF\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"nickname\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=Jam&offset=0&count=3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("results", hasSize(3)));
    }
    @Test
    void whenSearchingForUsers_Withoffset_ReturnsWithOffsetApplies() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");
        createOneUser("Nothing", "James", "jeh@uclive.ac.nz", "2000-10-27", homeAddress, "1337");

        String user1 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"123@123\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"middleName\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user2 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1234@FSF\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"nickname\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=Jam&offset=0&count=4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("results.[0].id", is(2)));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=Jam&offset=0&count=4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("results.[0].id", is(2)));
    }


    @Test
    void whenSearchingForUsers_WithNegativeOffset_returns400BadRequest() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");
        createOneUser("Nothing", "James", "jeh@uclive.ac.nz", "2000-10-27", homeAddress, "1337");

        String user1 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"123@123\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"middleName\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user2 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1234@FSF\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"nickname\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=Jam&offset=-1&count=4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenSearchingForUsers_WithNegativeCount_returns400BadRequest() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", homeAddress, "1337");
        createOneUser("Nothing", "James", "jeh@uclive.ac.nz", "2000-10-27", homeAddress, "1337");

        String user1 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"123@123\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"middleName\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        String user2 = "{\"firstName\": \"Nothing\", \"lastName\" : \"Nothing\", \"email\": \"1234@FSF\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"password\": \"1337\", \"nickname\": \"James\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(user2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/search?searchQuery=Jam&offset=2&count=-4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    private void createOneUser(String firstName, String lastName, String email, String dateOfBirth, String homeAddress, String password) {
        String user = String.format("{\"firstName\": \"%s\", \"lastName\" : \"%s\", \"email\": \"%s\", \"dateOfBirth\": \"%s\", \"homeAddress\": %s, \"password\": \"%s\"}", firstName, lastName, email, dateOfBirth, homeAddress, password);

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
