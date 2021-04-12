package com.seng302.wasteless;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.testconfigs.MockUserServiceConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@Import(MockUserServiceConfig.class)
public class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    public void whenPostRequestToUsersAndValidUser_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void whenPostRequestToUsersAndUserInvalidDueToMissingFirstName_thenCorrectResponse() throws Exception {
        String user = "{\"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"27-10-2000\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void whenPostRequestToUsersAndUserInvalidDueToMissingLastName_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPostRequestToUsersAndUserInvalidDueToMissingEmail_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPostRequestToUsersAndUserInvalidDueToMissingDateOfBirth_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"homeAddress\": \"236a Blenheim Road\", \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPostRequestToUsersAndUserInvalidDueToMissingHomeAddress_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"2000-10-27\", \"password\": \"1337\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPostRequestToUsersAndUserInvalidDueToMissingPassword_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPostRequestToUsersAndUserInvalidDueToAlreadyUsedEmail_thenCorrectResponse() throws Exception {
        createOneUser("James", "Harris", "jeh128@uclive.ac.nz", "2000-10-27", "236a Blenheim Road", "1337");

        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"236a Blenheim Road\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPostRequestToUsersAndUserInvalidDueToDateOfBirthTooOld_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"1800-20-10\", \"homeAddress\": \"236a Blenheim Road\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPostRequestToUsersAndUserInvalidDueToDateOfBirthTooYoung_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"2200-10-27\", \"homeAddress\": \"236a Blenheim Road\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPostRequestToUsersAndUserInvalidDueToMalformedDateOfBirth_thenCorrectResponse() throws Exception {
        String user = "{\"firstName\": \"James\", \"lastName\" : \"Harris\", \"email\": \"jeh128@uclive.ac.nz\", \"dateOfBirth\": \"10/27/1000\", \"homeAddress\": \"236a Blenheim Road\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenNotLoggedInAndTryMakeAdmin_thenUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/10000/makeAdmin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenNotLoggedInAndTryRevokeAdmin_thenUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/10000/revokeAdmin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("user@700")
    public void whenUserTryMakeAdmin_thenForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/10000/makeAdmin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin@700")
    public void whenAdminTryDowngradeDefaultAdmin_thenForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/0/makeAdmin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin@700")
    public void whenAdminTryAddAdminToUser_thenOk() throws Exception {
        User user = new User();
        user.setEmail("blah");
        user.setRole(UserRoles.USER);
        user = userService.createUser(user);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user.getId().toString() + "/makeAdmin"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin@700")
    public void whenAdminTryRevokeDefaultAdmin_thenForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/0/revokeAdmin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("defaultadmin@700")
    public void whenDefaultAdminTryRevokeSelf_thenForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/0/revokeAdmin"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithUserDetails("user@700")
    public void whenUserTryRevokeAdmin_thenForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/10000/revokeAdmin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("defaultadmin@700")
    public void whenDefaultAdminTryRevokeAdmin_thenOk() throws Exception {
        User admin = new User();
        admin.setEmail("blah");
        admin.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);
        admin = userService.createUser(admin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + admin.getId().toString() + "/revokeAdmin"))
                .andExpect(status().isOk());
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

