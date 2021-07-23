package com.seng302.wasteless.unitTest.ServiceTests;


import com.seng302.wasteless.repository.CardRepository;
import com.seng302.wasteless.service.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(SpringRunner.class)
@WebMvcTest(CardService.class)
class CardServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private UserService userService;

    @Test
    void whenFindInventoryById_AndInvalidId_ThrowException() {
        Mockito.when(cardRepository.findFirstById(1)).thenReturn(null);
        boolean success = true;
        try {
            CardService cardService = new CardService(cardRepository);
            cardService.findCardById(1);
        } catch (ResponseStatusException e) {
            success = false;
            assertEquals(406, e.getRawStatusCode());
        }
        assert !success;


    }




}

