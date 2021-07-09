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

    private User user;
    private User admin;
    private User defaultAdmin;

    @BeforeEach
    void setUp() {

        user = mock(User.class);
        user.setId(1);
        user.setEmail("user@gmail.com");
        user.setRole(UserRoles.USER);

        admin = mock(User.class);
        admin.setId(2);
        admin.setEmail("admin@gmail.com");
        admin.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);


        defaultAdmin = mock(User.class);
        defaultAdmin.setId(3);
        defaultAdmin.setEmail("default@gmail.com");
        defaultAdmin.setRole(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN);

        doReturn(false).when(user).checkUserGlobalAdmin();
        doReturn(true).when(admin).checkUserGlobalAdmin();
        doReturn(true).when(defaultAdmin).checkUserGlobalAdmin();

        doReturn(false).when(user).checkUserDefaultAdmin();
        doReturn(false).when(admin).checkUserDefaultAdmin();
        doReturn(true).when(defaultAdmin).checkUserDefaultAdmin();

        doReturn(1).when(user).getId();
        doReturn(2).when(admin).getId();
        doReturn(3).when(defaultAdmin).getId();


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

    private void login(User currentUser) {
        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(currentUser);
    }

    @Test
     void whenTryMakeUserAdmin_andUserIsUserRole_andRequestFromDGAA_thenOk() throws Exception {
        login(defaultAdmin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/makeAdmin")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
     void whenTryMakeUserAdmin_andUserIsUserRole_andRequestFromGAA_then403Response() throws Exception {
        login(admin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/makeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
     void whenTryMakeUserAdmin_andUserIsUserRole_andRequestFromUser_thenForbidden() throws Exception {
        login(user);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/makeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
     void whenTryMakeUserAdmin_andUserDoesNotExist_andRequestFromDGAA_then406Response() throws Exception {
        login(defaultAdmin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/4/makeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
     void whenTryMakeUserAdmin_andUserDoesNotExist_andRequestFromGAA_then406Response() throws Exception {
        login(admin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/4/makeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
     void whenTryMakeUserAdmin_andUserDoesNotExist_andRequestFromUser_then406Response() throws Exception {
        login(user);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/4/makeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
     void whenTryMakeUserAdmin_andUserIsDGAARole_andRequestFromDGAA_then406Response() throws Exception {
        login(defaultAdmin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/makeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
     void whenTryMakeUserAdmin_andUserIsDGAARole_andRequestFromGAA_then403Response() throws Exception {
        login(admin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/makeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
     void whenTryMakeUserAdmin_andUserIsDGAARole_andRequestFromUser_then403Response_insteadOf400_becauseForbiddenTakesPrecedence() throws Exception {
        login(user);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/makeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
     void whenTryMakeUserAdmin_andUserIsGAARole_andRequestFromDGAA_then200Response() throws Exception {
        login(defaultAdmin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/makeAdmin")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
     void whenTryMakeUserAdmin_andUserIsGAARole_andRequestFromGAA_then403Response() throws Exception {
        login(admin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/makeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
     void whenTryMakeUserAdmin_andUserIsGAARole_andRequestFromUser_then403Response_insteadOf400_becauseForbiddenTakesPrecedence() throws Exception {
        login(user);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/makeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
     void whenTryRevokeUserAdmin_andUserIsDGAARole_andRequestFromDGAA_then409Response_asAdminCannotRevokeOwnRights() throws Exception {
        login(defaultAdmin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isConflict());
    }


    @Test
     void whenTryRevokeUserAdmin_andUserIsGAARole_andRequestFromDGAA_then200Response() throws Exception {
        login(defaultAdmin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
     void whenTryRevokeUserAdmin_andUserIsUserRole_andRequestFromDGAA_then200Response() throws Exception {
        login(defaultAdmin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
     void whenTryRevokeUserAdmin_andUserDoesNotExist_andRequestFromDGAA_then406Response() throws Exception {
        login(defaultAdmin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/4/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
     void whenTryRevokeUserAdmin_andUserIsGAARole_andRequestFromGAA_then403Response() throws Exception {
        login(admin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
     void whenTryRevokeUserAdmin_andUserDGAARole_andRequestFromGAA_then403Response() throws Exception {
        login(admin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
     void whenTryRevokeUserAdmin_andUserIsUserRole_andRequestFromGAA_then403Response() throws Exception {
        login(admin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
     void whenTryRevokeUserAdmin_andUserDoesNotExist_andRequestFromGAA_then406Response() throws Exception {
        login(admin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/4/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
     void whenTryRevokeUserAdmin_andUserGAARole_andRequestFromUser_then403Response() throws Exception {
        login(user);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
     void whenTryRevokeUserAdmin_andUserDGAARole_andRequestFromUser_then403Response() throws Exception {
        login(user);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }


    @Test
     void whenTryRevokeUserAdmin_andDoesNotExist_andRequestFromUser_then406Response() throws Exception {
        login(user);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/4/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }


    @Test
     void whenTryRevokeUserAdmin_andUserIsUserRole_andRequestFromUser_then403Response() throws Exception {
        login(user);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
