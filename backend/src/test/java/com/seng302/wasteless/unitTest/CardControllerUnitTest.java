package com.seng302.wasteless.unitTest;

import com.seng302.wasteless.controller.CardController;
import com.seng302.wasteless.controller.ListingController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CardController.class)
@AutoConfigureMockMvc()
public class CardControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        User cardCreator = new User();
        cardCreator.setId(1);
        cardCreator.setEmail("james@gmail.com");
        cardCreator.setRole(UserRoles.USER);

        Card exchangeCard = new Card();
        exchangeCard.setId(1L);
        exchangeCard.setCreatorId(cardCreator.getId());
        exchangeCard.setSection(CardSections.EXCHANGE);
        exchangeCard.setTitle("My motivation");

        Mockito
                .when(cardService.findBySection(CardSections.EXCHANGE))
                .thenReturn(Collections.singletonList(exchangeCard));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToCards_andSectionIsExchange_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cards?section=Exchange")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToCards_andSectionIsInvalid_then400Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cards?section=Invalid")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
