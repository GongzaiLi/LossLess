package com.seng302.wasteless.integrationTest;

import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.testconfigs.WithMockCustomUser;
import org.junit.jupiter.api.Assertions;
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

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties"
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Reset JPA between test
public class AdminEndpointsIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @Test
    @WithMockCustomUser(email = "test@700", role = UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
    public void whenTryMakeUserAdmin_andUserIsUserRole_andRequestFromDGAA_thenOk() throws Exception {
        User user = new User();

        user.setRole(UserRoles.USER);
        user.setEmail("Test@Gmail");
        user.setPassword("password");
        user.setDateOfBirth(LocalDate.now());
        user.setBio("Bio1");
        user.setFirstName("FirstName1");
        user.setLastName("LastName1");

        Address address = new Address();
        address.setCountry("NZ");
        address.setCity("Christchurch");
        address.setStreetNumber("1");
        address.setStreetName("Ilam Rd");
        address.setPostcode("8041");

        addressService.createAddress(address);

        user.setHomeAddress(address);

        userService.createUser(user);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/makeAdmin"))
                .andExpect(status().isOk());

        User resultUser = userService.findUserByEmail("Test@Gmail");

        Assertions.assertEquals(UserRoles.GLOBAL_APPLICATION_ADMIN, resultUser.getRole());
    }

    @Test
    @WithMockCustomUser(email = "test@700", role = UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
    public void whenTryMakeUserAdmin_andUserIsUserGlobalApplicationAdmin_andRequestFromDGAA_thenOk() throws Exception {
        User user = new User();

        user.setRole(UserRoles.USER);
        user.setEmail("Test@Gmail");
        user.setPassword("password");
        user.setDateOfBirth(LocalDate.now());
        user.setBio("Bio1");
        user.setFirstName("FirstName1");
        user.setLastName("LastName1");

        Address address = new Address();
        address.setCountry("NZ");
        address.setCity("Christchurch");
        address.setStreetNumber("1");
        address.setStreetName("Ilam Rd");
        address.setPostcode("8041");

        addressService.createAddress(address);

        user.setHomeAddress(address);

        userService.createUser(user);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/makeAdmin"))
                .andExpect(status().isOk());

        User resultUser = userService.findUserByEmail("Test@Gmail");

        Assertions.assertEquals(UserRoles.GLOBAL_APPLICATION_ADMIN, resultUser.getRole());
    }

    @Test
    @WithMockCustomUser(email = "test@700", role = UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
    public void whenUserToBeMadeAdminNotFound_thenNotAcceptable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/10000/makeAdmin"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockCustomUser(email = "defaultAdmin@700", role = UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
    public void whenUserToBeMadeAdminNotFound_andDGAA_thenNotAcceptable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/10000/makeAdmin"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockCustomUser(email = "admin@700", role = UserRoles.GLOBAL_APPLICATION_ADMIN)
    public void whenUserToBeMadeAdminNotFound_andGAA_thenNotAcceptable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/10000/makeAdmin"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockCustomUser(email = "jeh128@uclive.ac.nz", role = UserRoles.GLOBAL_APPLICATION_ADMIN)
    public void whenUserToMakeAdminFound_andGAA_butRequestToChangeDGAA_thenForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/makeAdmin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(email = "jeh128@uclive.ac.nz", role = UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
    public void whenUserToMakeAdminFound_andDGAA_butRequestToChangeDGAA_thenForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/makeAdmin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(email = "admin@700", role = UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
    public void whenTryRevokeUserExists_andUserIsDGAARole_andRequestFromDGAA_thenForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/revokeAdmin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(email = "admin@700", role = UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
    public void whenTryRevokeUserExists_andUserIsDGAARole_andRequestFromGAA_thenForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/revokeAdmin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(email = "admin@700", role = UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
    public void whenTryRevokeUserExists_andUserIsUserRole_andRequestFromDGAA_thenOk() throws Exception {
        User user = new User();

        user.setRole(UserRoles.USER);
        user.setEmail("Test@Gmail");
        user.setPassword("password");
        user.setDateOfBirth(LocalDate.now());
        user.setBio("Bio1");
        user.setFirstName("FirstName1");
        user.setLastName("LastName1");

        Address address = new Address();
        address.setCountry("NZ");
        address.setCity("Christchurch");
        address.setStreetNumber("1");
        address.setStreetName("Ilam Rd");
        address.setPostcode("8041");

        addressService.createAddress(address);

        user.setHomeAddress(address);

        userService.createUser(user);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/revokeAdmin"))
                .andExpect(status().isOk());

        User resultUser = userService.findUserByEmail("Test@Gmail");

        Assertions.assertEquals(UserRoles.USER, resultUser.getRole());
    }

    @Test
    @WithMockCustomUser(email = "admin@700", role = UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
    public void whenTryRevokeUserExists_andUserIsGAARole_andRequestFromDGAA_thenOk() throws Exception {
        User user = new User();

        user.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);
        user.setEmail("Test@Gmail");
        user.setPassword("password");
        user.setDateOfBirth(LocalDate.now());
        user.setBio("Bio1");
        user.setFirstName("FirstName1");
        user.setLastName("LastName1");

        Address address = new Address();
        address.setCountry("NZ");
        address.setCity("Christchurch");
        address.setStreetNumber("1");
        address.setStreetName("Ilam Rd");
        address.setPostcode("8041");

        addressService.createAddress(address);

        user.setHomeAddress(address);

        userService.createUser(user);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/revokeAdmin"))
                .andExpect(status().isOk());

        User resultUser = userService.findUserByEmail("Test@Gmail");

        Assertions.assertEquals(UserRoles.USER, resultUser.getRole());
    }





}
