package com.seng302.wasteless.unitTest;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
 class GlobalAdminEndpointUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AddressService addressService;

    @MockBean
    private Authentication authentication;


    @BeforeEach
    void setUp() {

        User user = mock(User.class);
        user.setId(1);
        user.setEmail("user@gmail.com");
        user.setRole(UserRoles.USER);

        User admin = mock(User.class);
        admin.setId(2);
        admin.setEmail("admin@gmail.com");
        admin.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);

        User admin2 = mock(User.class);
        admin2.setId(5);
        admin2.setEmail("admin2@gmail.com");
        admin2.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);

        User defaultAdmin = mock(User.class);
        defaultAdmin.setId(3);
        defaultAdmin.setEmail("default@gmail.com");
        defaultAdmin.setRole(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN);

        doReturn(false).when(user).checkUserGlobalAdmin();
        doReturn(true).when(admin).checkUserGlobalAdmin();
        doReturn(true).when(admin2).checkUserGlobalAdmin();
        doReturn(true).when(defaultAdmin).checkUserGlobalAdmin();

        doReturn(false).when(user).checkUserDefaultAdmin();
        doReturn(false).when(admin).checkUserDefaultAdmin();
        doReturn(false).when(admin2).checkUserDefaultAdmin();
        doReturn(true).when(defaultAdmin).checkUserDefaultAdmin();

        doReturn(1).when(user).getId();
        doReturn(2).when(admin).getId();
        doReturn(3).when(defaultAdmin).getId();
        doReturn(5).when(admin2).getId();


        Mockito
                .when(userService.findUserByEmail("user@gmail.com"))
                .thenReturn(user);

        Mockito
                .when(userService.findUserByEmail("admin@gmail.com"))
                .thenReturn(admin);

        Mockito
                .when(userService.findUserByEmail("admin2@gmail.com"))
                .thenReturn(admin2);

        Mockito
                .when(userService.findUserByEmail("default@gmail.com"))
                .thenReturn(defaultAdmin);

        Mockito
                .when(userService.findUserById(1))
                .thenReturn(user);

        Mockito
                .when(userService.findUserById(2))
                .thenReturn(admin);

        Mockito
                .when(userService.findUserById(3))
                .thenReturn(defaultAdmin);
    }

    @Test
    @WithMockUser(username = "default@gmail.com", password = "pwd")
     void whenTryMakeUserAdmin_andUserIsUserRole_andRequestFromDGAA_thenOk() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/makeAdmin")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "pwd")
     void whenTryMakeUserAdmin_andUserIsUserRole_andRequestFromGAA_then403Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/makeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "pwd")
     void whenTryMakeUserAdmin_andUserIsUserRole_andRequestFromUser_thenForbidden() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/makeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "default@gmail.com", password = "pwd")
     void whenTryMakeUserAdmin_andUserDoesNotExist_andRequestFromDGAA_then406Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/users/4/makeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "pwd")
     void whenTryMakeUserAdmin_andUserDoesNotExist_andRequestFromGAA_then406Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/users/4/makeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "pwd")
     void whenTryMakeUserAdmin_andUserDoesNotExist_andRequestFromUser_then406Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/users/4/makeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "default@gmail.com", password = "pwd")
     void whenTryMakeUserAdmin_andUserIsDGAARole_andRequestFromDGAA_then406Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/makeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "pwd")
     void whenTryMakeUserAdmin_andUserIsDGAARole_andRequestFromGAA_then403Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/makeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "pwd")
     void whenTryMakeUserAdmin_andUserIsDGAARole_andRequestFromUser_then403Response_insteadOf400_becauseForbiddenTakesPrecedence() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/makeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "default@gmail.com", password = "pwd")
     void whenTryMakeUserAdmin_andUserIsGAARole_andRequestFromDGAA_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/makeAdmin")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "pwd")
     void whenTryMakeUserAdmin_andUserIsGAARole_andRequestFromGAA_then403Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/makeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "pwd")
     void whenTryMakeUserAdmin_andUserIsGAARole_andRequestFromUser_then403Response_insteadOf400_becauseForbiddenTakesPrecedence() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/makeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }





    @Test
    @WithMockUser(username = "default@gmail.com", password = "pwd")
     void whenTryRevokeUserAdmin_andUserIsDGAARole_andRequestFromDGAA_then409Response_asAdminCannotRevokeOwnRights() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isConflict());
    }


    @Test
    @WithMockUser(username = "default@gmail.com", password = "pwd")
     void whenTryRevokeUserAdmin_andUserIsGAARole_andRequestFromDGAA_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "default@gmail.com", password = "pwd")
     void whenTryRevokeUserAdmin_andUserIsUserRole_andRequestFromDGAA_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "default@gmail.com", password = "pwd")
     void whenTryRevokeUserAdmin_andUserDoesNotExist_andRequestFromDGAA_then406Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/4/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "pwd")
     void whenTryRevokeUserAdmin_andUserIsGAARole_andRequestFromGAA_then403Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "pwd")
     void whenTryRevokeUserAdmin_andUserDGAARole_andRequestFromGAA_then403Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "pwd")
     void whenTryRevokeUserAdmin_andUserIsUserRole_andRequestFromGAA_then403Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "pwd")
     void whenTryRevokeUserAdmin_andUserDoesNotExist_andRequestFromGAA_then406Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/4/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "pwd")
     void whenTryRevokeUserAdmin_andUserGAARole_andRequestFromUser_then403Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "pwd")
     void whenTryRevokeUserAdmin_andUserDGAARole_andRequestFromUser_then403Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = "user@gmail.com", password = "pwd")
     void whenTryRevokeUserAdmin_andDoesNotExist_andRequestFromUser_then406Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/4/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }


    @Test
    @WithMockUser(username = "user@gmail.com", password = "pwd")
     void whenTryRevokeUserAdmin_andUserIsUserRole_andRequestFromUser_then403Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
