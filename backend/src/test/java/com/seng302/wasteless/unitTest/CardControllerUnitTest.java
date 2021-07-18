package com.seng302.wasteless.unitTest;

import com.seng302.wasteless.controller.CardController;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CardControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private BusinessService businessService;

    @MockBean
    private UserService userService;

    @MockBean
    private CardService cardService;

    @MockBean
    private Authentication authentication;

    private User user;

    private Card card;

    private User userForCard;

    private Card mockedCard;

    private List<String> keywords;

    @BeforeEach
    void setUp() {
        userForCard = new User();
        userForCard.setId(1);
        userForCard.setEmail("demo@gmail.com");
        userForCard.setRole(UserRoles.USER);

        keywords = new ArrayList<>();
        keywords.add("Vehicle");
        keywords.add("Car");

        card = new Card();
        card.setId(1);
        card.setCreator(userForCard);
        card.setSection(CardSections.FOR_SALE);
        card.setTitle("Sale");
        card.setKeywords(keywords);

        mockedCard = mock(Card.class);
        mockedCard.setId(1);
        mockedCard.setCreator(userForCard);
        mockedCard.setSection(CardSections.FOR_SALE);
        mockedCard.setTitle("Sale");
        mockedCard.setKeywords(keywords);

        user = mock(User.class);
        user.setId(1);
        user.setEmail("demo@gmail.com");
        user.setRole(UserRoles.USER);

        Mockito
                .when(cardService.createCard(any(Card.class)))
                .thenReturn(card.setId(2));

        Mockito
                .when(authentication.getName())
                .thenReturn("demo@gmail.com");

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(user);

        doReturn(userForCard).when(userService).findUserById(any(Integer.class));

    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenPostRequestToAddCard_andValidRequest_then201Response() throws Exception {
        String jsonInStringForRequest = "{\"section\": \"ForSale\", \"title\": \"1982 Lada Samara\", \"keywords\": [\"Vehicle\", \"Car\"]}";

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/cards")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("cardId", is(2)));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenPostRequestToAddCard_andKeywordsIsLessThan1_then400Response() throws Exception {
        String jsonInStringForRequest = "{\"section\": \"ForSale\", \"title\": \"1982 Lada Samara\", \"keywords\": []}";

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/cards")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenPostRequestToAddCard_andKeywordsIsMoreThan5_then400Response() throws Exception {
        String jsonInStringForRequest = "{\"section\": \"ForSale\", \"title\": \"1982 Lada Samara\", \"keywords\": [\"Vehicle\", \"Car\", \"Vehicle\", \"Car\", \"Vehicle\", \"Car\"]}";

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/cards")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
