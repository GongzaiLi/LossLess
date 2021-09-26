package com.seng302.wasteless.unitTest;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.NotificationService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @MockBean
    private NotificationService notificationService;

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

    @ParameterizedTest
    @ValueSource(strings = {"/users/1/makeAdmin", "/users/2/makeAdmin", "/users/2/revokeAdmin", "/users/1/revokeAdmin"})
     void whenTryMakeOrRevokeUserAdmin_andUserIsUserOrGAARole_andRequestFromDGAA_thenOk(String request) throws Exception {
        login(defaultAdmin);
        mockMvc.perform(MockMvcRequestBuilders.put(request)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void whenTryMakeUserAdmin_andUserIsDGAARole_andRequestFromDGAA_then406Response() throws Exception {
        login(defaultAdmin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/makeAdmin")
                .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }


    @Test
    void whenTryRevokeUserAdmin_andUserIsDGAARole_andRequestFromDGAA_then409Response_asAdminCannotRevokeOwnRights() throws Exception {
        login(defaultAdmin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/3/revokeAdmin")
                .with(csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    void whenTryMakeOrRevokeUserAdmin_andUserIsGAARole_andRequestFromGAA_then403Response() throws Exception {
        login(admin);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/makeAdmin")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }



    @ParameterizedTest
    @ValueSource(strings = {"/users/1/makeAdmin","/users/3/makeAdmin", "/users/2/revokeAdmin", "/users/3/revokeAdmin", "/users/1/revokeAdmin"})
    void whenTryMakeUserAdmin_andUserIsUserOrDGAARole_andRequestFromGAA_then403Response(String request) throws Exception {
        login(admin);
        mockMvc.perform(MockMvcRequestBuilders.put(request)
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/users/1/makeAdmin", "/users/4/makeAdmin", "/users/3/makeAdmin", "/users/2/makeAdmin",
            "/users/2/revokeAdmin", "/users/3/revokeAdmin", "/users/4/revokeAdmin", "/users/1/revokeAdmin"})
     void whenTryMakeOrRevokeUserAdmin_andUserIsAnyRole_andRequestFromUser_thenForbidden(String request) throws Exception {
        login(user);
        mockMvc.perform(MockMvcRequestBuilders.put(request)
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

}
