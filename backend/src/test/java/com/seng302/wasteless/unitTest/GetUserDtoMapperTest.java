package com.seng302.wasteless.unitTest;

import com.seng302.wasteless.dto.GetUserBusinessAdministeredDto;
import com.seng302.wasteless.dto.mapper.GetUserDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class GetUserDtoMapperTest {

    @MockBean
    private BusinessService businessService;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    User user = new User();
    User signedInUser = new User();

    @BeforeEach
    public void setup() {
        user.setId(1);
        user.setEmail("james@gmail.com");
        user.setFirstName("James");
        user.setLastName("Harris");
        user.setRole(UserRoles.USER);
        user.setCreated(LocalDate.now());
        user.setDateOfBirth(LocalDate.now());
        user.setHomeAddress(new Address());

        Business business = new Business();
        business.setPrimaryAdministrator(user);
        business.setCreated(LocalDate.now());
        business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setId(1);
        business.setAdministrators(Collections.singletonList(user));
        business.setName("Jimmy's clown store");

        Mockito
                .when(businessService.findBusinessesByUserId(anyInt()))
                .thenReturn(Collections.singletonList(business));

        signedInUser.setId(1);
        signedInUser.setEmail("eric@gmail.com");
        signedInUser.setRole(UserRoles.USER);

        Mockito
                .when(authentication.getName())
                .thenReturn("eric@gmail.com");

        Mockito
                .when(userService.findUserByEmail(anyString()))
                .thenReturn(signedInUser);
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenGetUserDtoAndSignedInAsRegularUser_thenDtoContainsBusinessesAdministered() {
        List<GetUserBusinessAdministeredDto> businessesAdministered = GetUserDtoMapper.toGetUserDto(user).getBusinessesAdministered();
        Assertions.assertNotNull(businessesAdministered);
        Assertions.assertEquals(businessesAdministered.size(), 1);
        Assertions.assertEquals(businessesAdministered.get(0).getName(), "Jimmy's clown store");
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenGetUserDtoAndSignedInAsAdmin_thenDtoContainsBusinessesAdministered() {
        signedInUser.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);

        List<GetUserBusinessAdministeredDto> businessesAdministered = GetUserDtoMapper.toGetUserDto(user).getBusinessesAdministered();
        Assertions.assertNotNull(businessesAdministered);
        Assertions.assertEquals(businessesAdministered.size(), 1);
        Assertions.assertEquals(businessesAdministered.get(0).getName(), "Jimmy's clown store");
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenGetUserDtoAndSignedInAsDGAA_thenDtoContainsBusinessesAdministered() {
        signedInUser.setRole(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN);

        List<GetUserBusinessAdministeredDto> businessesAdministered = GetUserDtoMapper.toGetUserDto(user).getBusinessesAdministered();
        Assertions.assertNotNull(businessesAdministered);
        Assertions.assertEquals(businessesAdministered.size(), 1);
        Assertions.assertEquals(businessesAdministered.get(0).getName(), "Jimmy's clown store");
    }
}
