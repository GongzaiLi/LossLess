package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.model.Image;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.repository.UserRepository;
import com.seng302.wasteless.service.ImageService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@WebMvcTest(UserService.class)
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ImageService imageService;

    @Test
    void whenFindUserById_AndInvalidId_ThrowException() {
        Mockito.when(userRepository.findFirstById(1)).thenReturn(null);
        boolean success = true;
        try {
            UserService userService = new UserService(userRepository, imageService);
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

        UserService userService = new UserService(userRepository, imageService);
        Assertions.assertEquals(1, userService.findUserById(1).getId());
    }

    @Test
    void whenDeleteUserImage_ImageIsDeleted() {
        User user = new User();
        user.setProfileImage(new Image());

        UserService userService = new UserService(userRepository, imageService);
        userService.deleteUserImage(user);

        Assertions.assertNull(user.getProfileImage());
    }

}

