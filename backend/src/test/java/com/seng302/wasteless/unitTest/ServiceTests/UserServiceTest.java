package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.repository.UserRepository;
import com.seng302.wasteless.service.UserService;
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


    @Test
    void whenFindUserById_AndInvalidId_ThrowException() {
        Mockito.when(userRepository.findFirstById(1)).thenReturn(null);
        boolean success = true;
        try {
            UserService userService = new UserService(userRepository);
            userService.findUserById(1);
        } catch (ResponseStatusException e) {
            success = false;
            assertEquals(406, e.getRawStatusCode());
        }
        assert !success;


    }

}

