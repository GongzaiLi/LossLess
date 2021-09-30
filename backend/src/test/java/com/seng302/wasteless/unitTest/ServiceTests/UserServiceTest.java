package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.dto.PutUserDto;
import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.Image;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.repository.UserRepository;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.ImageService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@WebMvcTest(UserService.class)
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ImageService imageService;

    @MockBean
    private AddressService addressService;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Test
    void whenFindUserById_AndInvalidId_ThrowException() {
        Mockito.when(userRepository.findFirstById(1)).thenReturn(null);
        boolean success = true;
        try {
            UserService userService = new UserService(userRepository, imageService, addressService, passwordEncoder);
            userService.findUserById(1);
        } catch (ResponseStatusException e) {
            success = false;
            assertEquals(406, e.getRawStatusCode());
        }
        assert !success;
    }

    @Test
    void whenFindUserById_AndValidId_UserIsReturned() {
        User userToFind = Mockito.mock(User.class);
        Mockito.when(userToFind.getId()).thenReturn(1);
        Mockito.when(userRepository.findFirstById(1)).thenReturn(userToFind);

        UserService userService = new UserService(userRepository, imageService, addressService, passwordEncoder);
        Assertions.assertEquals(1, userService.findUserById(1).getId());
    }

    @Test
    void whenDeleteUserImage_ImageIsDeleted() {
        User user = new User();
        user.setProfileImage(new Image());

        UserService userService = new UserService(userRepository, imageService, addressService, passwordEncoder);
        userService.deleteUserImage(user);

        Assertions.assertNull(user.getProfileImage());
    }


    @Test
    void whenCheckUserEmailWithoutCOM_AndValidEmail_ReturnTrue() {
        String userEmail = "a@a";
        Assertions.assertTrue(userService.checkEmailValid(userEmail));
    }

    @Test
    void whenCheckUserEmailIncludeCOM_AndValidEmail_ReturnTrue() {
        String userEmail = "a@a.com";
        Assertions.assertTrue(userService.checkEmailValid(userEmail));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a.com", "a", "", "#@#"})
    void whenCheckUserEmailWithoutAT_AndInValidEmail_ReturnFalse(String userEmail) {
        Assertions.assertFalse(userService.checkEmailValid(userEmail));
    }


    @Test
    void whenUserRegister_andEmailHasAlreadyUser_ReturnFalse() {
        String userEmail = "a@a.com";
        User user = new User();
        user.setEmail(userEmail);
        Mockito.when(userRepository.findFirstByEmail(userEmail)).thenReturn(user);
        Assertions.assertTrue(userService.checkEmailAlreadyUsed(userEmail));
    }

    @Test
    void whenUserRegister_andEmailHasNotAlreadyUser_ReturnFalse() {

        User user = new User();
        user.setEmail("a@a");
        Mockito.when(userRepository.findFirstByEmail(user.getEmail())).thenReturn(null);
        Assertions.assertFalse(userService.checkEmailAlreadyUsed("aaaas@bbbbs.com"));
    }


    @Test
    void whenUser2UpdateUserEmail_AndValidEmail_AndEmailAlreadyUsed_Return409() {
        String newEmail = "a@a";
        User user1 = new User();
        user1.setEmail("a@a");
        User user2 = new User();
        user2.setEmail("b@b");
        Mockito.when(userRepository.findFirstByEmail(newEmail)).thenReturn(user1);

        try {
            userService.updateUserEmail(user2, newEmail);
        } catch (ResponseStatusException e) {
            Assertions.assertEquals("409 CONFLICT \"Attempted to update user with already used email\"", e.getMessage());
        }
        Assertions.assertNotEquals(user2.getEmail(), newEmail);
    }

    @Test
    void whenUser1UpdateUserEmail_AndInValidEmail_Return400() {
        String newEmail = "a@";
        User user1 = new User();
        user1.setEmail("a@a");
        User user2 = new User();
        user2.setEmail("b@b");
        Mockito.when(userRepository.findFirstByEmail(newEmail)).thenReturn(null);

        try {
            userService.updateUserEmail(user1, newEmail);
        } catch (ResponseStatusException e) {
            Assertions.assertEquals("400 BAD_REQUEST \"Email address is invalid\"", e.getMessage());
        }
        Assertions.assertNotEquals(user1.getEmail(), newEmail);
    }

    @Test
    void whenUser1UpdateUserEmail_AndValidEmail_ThenTheUserEmailUpdated() {
        String newEmail = "a@b";
        User user1 = new User();
        user1.setEmail("a@a");
        User user2 = new User();
        user2.setEmail("b@b");
        Mockito.when(userRepository.findFirstByEmail(newEmail)).thenReturn(null);

        userService.updateUserEmail(user1, newEmail);
        Assertions.assertEquals(user1.getEmail(), newEmail);
    }


    @Test
    void whenUserUpdateUserDetails_ThenUpdatedUser() {
        User user = new User();
        user.setFirstName("a");
        user.setLastName("a");
        user.setMiddleName("a");
        user.setNickname("a");
        user.setBio("a");
        user.setPhoneNumber("1");

        PutUserDto newUserDetail = new PutUserDto();
        newUserDetail.setFirstName("b");
        newUserDetail.setLastName("b");
        newUserDetail.setMiddleName("b");
        newUserDetail.setNickname("b");
        newUserDetail.setBio("b");
        newUserDetail.setPhoneNumber("2");

        Assertions.assertNotEquals(user.getFirstName(), newUserDetail.getFirstName());
        Assertions.assertNotEquals(user.getLastName(), newUserDetail.getLastName());
        Assertions.assertNotEquals(user.getMiddleName(), newUserDetail.getMiddleName());
        Assertions.assertNotEquals(user.getNickname(), newUserDetail.getNickname());
        Assertions.assertNotEquals(user.getBio(), newUserDetail.getBio());
        Assertions.assertNotEquals(user.getPhoneNumber(), newUserDetail.getPhoneNumber());

        userService.updateUserDetails(user, newUserDetail);

        Assertions.assertEquals(user.getFirstName(), newUserDetail.getFirstName());
        Assertions.assertEquals(user.getLastName(), newUserDetail.getLastName());
        Assertions.assertEquals(user.getMiddleName(), newUserDetail.getMiddleName());
        Assertions.assertEquals(user.getNickname(), newUserDetail.getNickname());
        Assertions.assertEquals(user.getBio(), newUserDetail.getBio());
        Assertions.assertEquals(user.getPhoneNumber(), newUserDetail.getPhoneNumber());
    }


    @Test
    void whenUserUpdateUserDateOfBirth_TheDateOfBirthUnder13_Return400() {
        LocalDate birth = LocalDate.parse("1998-05-09");
        LocalDate newBirth = LocalDate.parse("2020-07-07");
        User user = new User();
        user.setEmail("a@a");
        user.setDateOfBirth(birth);

        try {
            userService.modifyUserDateOfBirth(user, newBirth);
        } catch (ResponseStatusException e) {
            user.setDateOfBirth(birth);
            Assertions.assertEquals("400 BAD_REQUEST \"Date out of expected range\"", e.getMessage());
        }
        Assertions.assertNotEquals(user.getDateOfBirth(), newBirth);
    }

    @Test
    void whenUserUpdateUserDateOfBirth_TheDateOfBirthIsOver120_Return400() {
        LocalDate birth = LocalDate.parse("1998-05-09");
        LocalDate newBirth = LocalDate.parse("1770-07-07");
        User user = new User();
        user.setEmail("a@a");
        user.setDateOfBirth(birth);

        try {
            userService.modifyUserDateOfBirth(user, newBirth);
        } catch (ResponseStatusException e) {
            user.setDateOfBirth(birth);
            Assertions.assertEquals("400 BAD_REQUEST \"Date out of expected range\"", e.getMessage());
        }
        Assertions.assertNotEquals(user.getDateOfBirth(), newBirth);
    }

    @Test
    void whenUserUpdateUserDateOfBirth_TheDateOfBirthIsValid() {
        LocalDate birth = LocalDate.parse("1998-05-09");
        LocalDate newBirth = LocalDate.parse("2000-07-07");
        User user = new User();
        user.setEmail("a@a");
        user.setDateOfBirth(birth);


        userService.modifyUserDateOfBirth(user, newBirth);
        Assertions.assertEquals(user.getDateOfBirth(), newBirth);
    }


    @Test
    void whenUserModifyUserHomeAddress_ThenUpdatedUserAddress() {
        Address address = new Address();
        address.setCountry("NZ");

        Address newAddress = new Address();
        address.setCountry("CN");

        User user = new User();
        user.setHomeAddress(address);

        userService.modifyUserHomeAddress(user, newAddress);
        Assertions.assertEquals(user.getHomeAddress(), newAddress);

    }

    @Test
    void whenUserModifyUserPassword_AndNewPasswordIsNull_ThenUserPasswordDoesNotChange() {
        String password = "A";
        String newPassword = null;

        User user = new User();
        user.setPassword(password);

        userService.modifyUserPassword(user, newPassword);
        Assertions.assertEquals(user.getPassword(), password);

    }

    @Test
    void whenUserModifyUserPassword_AndNewPasswordIsEmpty_ThenUserPasswordDoesNotChange() {
        String password = "A";
        String newPassword = "";

        User user = new User();
        user.setPassword(password);

        userService.modifyUserPassword(user, newPassword);
        Assertions.assertEquals(user.getPassword(), password);
    }


    @Test
    void whenUserModifyUserPassword_AndNewPasswordIsB_ThenUserPasswordUpdate() {
        String password = "A";
        String newPassword = "B";

        User user = new User();
        user.setPassword(password);

        Mockito.when(passwordEncoder.encode(any())).thenReturn(newPassword);

        userService.modifyUserPassword(user, newPassword);
        System.out.println(user.getPassword());
        Assertions.assertEquals(user.getPassword(), newPassword);
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenCheckCurrentlyLoggedInUser_andUserIsNull_ThenReturn401() {
        Mockito.when(userService.findUserByEmail(any())).thenReturn(null);
        boolean success = true;
        try {
            userService.getCurrentlyLoggedInUser();
        } catch (ResponseStatusException e) {
            success = false;
            Assertions.assertEquals("401 UNAUTHORIZED \"Session token is invalid\"", e.getMessage());
        }
        Assertions.assertFalse(success);
    }
}

