package com.seng302.wasteless.unitTest;

import com.seng302.wasteless.controller.CardController;
import com.seng302.wasteless.dto.mapper.GetUserDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CardController.class)
@TestPropertySource(properties = {"max-display-period-seconds=10"})
class CardControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private UserService userService;

    @MockBean
    private BusinessService businessService;

    @MockBean
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        User userForCard = new User();
        userForCard.setId(1);
        userForCard.setEmail("demo@gmail.com");
        userForCard.setRole(UserRoles.USER);
        userForCard.setCreated(LocalDate.now());
        userForCard.setDateOfBirth(LocalDate.now());
        userForCard.setHomeAddress(Mockito.mock(Address.class));

        List<String> keywords = new ArrayList<>();
        keywords.add("Vehicle");
        keywords.add("Car");

        LocalDateTime expiry = LocalDateTime.of(2021, Month.JULY, 21, 0, 0, 0);

        Card card = new Card();
        card.setId(1);
        card.setCreator(userForCard);
        card.setSection(CardSections.FOR_SALE);
        card.setTitle("Sale");
        card.setKeywords(keywords);
        card.setDisplayPeriodEnd(expiry.plusYears(2));

        Card expiringCard1 = new Card();
        expiringCard1.setId(2);
        expiringCard1.setCreator(userForCard);
        expiringCard1.setSection(CardSections.FOR_SALE);
        expiringCard1.setTitle("I am expiring");
        expiringCard1.setKeywords(keywords);
        expiringCard1.setDisplayPeriodEnd(expiry.minusMonths(1));

        Card expiringCard2 = new Card();
        expiringCard2.setId(3);
        expiringCard2.setCreator(userForCard);
        expiringCard2.setSection(CardSections.FOR_SALE);
        expiringCard2.setTitle("I am expiring");
        expiringCard2.setKeywords(keywords);
        expiringCard2.setDisplayPeriodEnd(expiry.minusMonths(2));


        List<Card> userCards = new ArrayList<>();
        userCards.add(card);
        userCards.add(expiringCard1);
        userCards.add(expiringCard2);


        User userForCardTwo = new User();
        userForCardTwo.setId(2);
        userForCardTwo.setEmail("notDemo@gmail.com");
        userForCardTwo.setRole(UserRoles.USER);
        userForCardTwo.setCreated(LocalDate.now());
        userForCardTwo.setDateOfBirth(LocalDate.now());
        userForCardTwo.setHomeAddress(Mockito.mock(Address.class));

        Card cardTwo = new Card();
        cardTwo.setId(2);
        cardTwo.setCreator(userForCardTwo);
        cardTwo.setSection(CardSections.FOR_SALE);
        cardTwo.setTitle("Sale");
        cardTwo.setKeywords(keywords);

        Card card3DaysFromExpiry = new Card();
        card3DaysFromExpiry.setId(6);
        card3DaysFromExpiry.setCreator(userForCard);
        card3DaysFromExpiry.setSection(CardSections.WANTED);
        card3DaysFromExpiry.setTitle("WANTED");
        card3DaysFromExpiry.setKeywords(keywords);
        card3DaysFromExpiry.setDisplayPeriodEnd(LocalDateTime.now().minusDays(3));

        Card card1DaysFromExpiry = new Card();
        card1DaysFromExpiry.setId(7);
        card1DaysFromExpiry.setCreator(userForCard);
        card1DaysFromExpiry.setSection(CardSections.WANTED);
        card1DaysFromExpiry.setTitle("WANTED");
        card1DaysFromExpiry.setKeywords(keywords);
        card1DaysFromExpiry.setDisplayPeriodEnd(LocalDateTime.now().minusDays(1));

        Card card1DaysPastExpiry = new Card();
        card1DaysPastExpiry.setId(8);
        card1DaysPastExpiry.setCreator(userForCard);
        card1DaysPastExpiry.setSection(CardSections.WANTED);
        card1DaysPastExpiry.setTitle("WANTED");
        card1DaysPastExpiry.setKeywords(keywords);
        card1DaysPastExpiry.setDisplayPeriodEnd(LocalDateTime.now().plusDays(1));


        Mockito
                .when(cardService.findCardById(6))
                .thenReturn(card3DaysFromExpiry);

        Mockito
                .when(cardService.findCardById(7))
                .thenReturn(card1DaysFromExpiry);

        Mockito
                .when(cardService.findCardById(8))
                .thenReturn(card1DaysPastExpiry);

        Mockito
                .when(cardService.createCard(any(Card.class)))
                .thenReturn(card);

        Mockito
                .when(authentication.getName())
                .thenReturn("demo@gmail.com");

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(userForCard);

        Mockito
                .when(cardService.findBySection(eq(CardSections.FOR_SALE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(card)));

        Mockito
                .when(cardService.findCardById(1))
                .thenReturn(card);
        Mockito
                .when(cardService.findCardById(5))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Card with given ID does not exist"));

        Mockito
                .when(cardService.findById(2))
                .thenReturn(cardTwo);

        Mockito
                .when(cardService.getAllUserCards(1))
                .thenReturn(userCards);

        doReturn(true).when(cardService).checkUserHasPermissionForCard(any(Card.class), any(User.class));

        doReturn(userForCard).when(userService).findUserById(any(Integer.class));
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "The section specified is not one of 'ForSale', 'Wanted', or 'Exchange'")).when(cardService).checkValidSection("Invalid");

        doReturn(true).when(cardService).checkCardWithinExtendDateRange(any(Card.class));

        new GetUserDtoMapper(businessService, userService); // This initialises the DTO mapper with our mocked services. The constructor has to be manually called
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenPostRequestToAddCard_andValidRequest_then201Response() throws Exception {
        String jsonInStringForRequest = "{\"section\": \"ForSale\", \"title\": \"1982 Lada Samara\", \"keywords\": [\"Vehicle\", \"Car\"]}";

        mockMvc.perform(MockMvcRequestBuilders.post("/cards")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("cardId", is(1)));
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenPostRequestToAddCard_andKeywordsIsLessThan1_then201Response() throws Exception {
        String jsonInStringForRequest = "{\"section\": \"ForSale\", \"title\": \"1982 Lada Samara\", \"keywords\": []}";

        mockMvc.perform(MockMvcRequestBuilders.post("/cards")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenPostRequestToAddCard_andKeywordsIsMoreThan5_then400Response() throws Exception {
        String jsonInStringForRequest = "{\"section\": \"ForSale\", \"title\": \"1982 Lada Samara\", \"keywords\": [\"Vehicle\", \"Car\", \"Vehicle\", \"Car\", \"Vehicle\", \"Car\"]}";

        mockMvc.perform(MockMvcRequestBuilders.post("/cards")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenGetRequestToCards_andSectionIsForSale_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cards?section=ForSale")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id", is(1)))
                .andExpect(jsonPath("$.results[0].creator.id", is(1)))
                .andExpect(jsonPath("$.totalItems", is(1)))
                .andExpect(jsonPath("$.results[1]").doesNotExist());
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenGetRequestToCards_andSectionIsInvalid_then400Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cards?section=Invalid")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenGetRequestToCardsExpiry_andExpiringCardsExist_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cards/1/expiring")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id", is(2)))
                .andExpect(jsonPath("[0].title", is("I am expiring")))
                .andExpect(jsonPath("[0].creator.id", is(1)))
                .andExpect(jsonPath("[1].id", is(3)))
                .andExpect(jsonPath("[1].title", is("I am expiring")))
                .andExpect(jsonPath("[1].creator.id", is(1)));
    }
    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenPutRequestToCard_then200ResponseAndExpiryIsChanged() throws Exception {
        Card card = cardService.findCardById(7);
        LocalDateTime expiry = card.getDisplayPeriodEnd();
        mockMvc.perform(MockMvcRequestBuilders.put("/cards/7/extenddisplayperiod")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
        card = cardService.findCardById(7);
        Assertions.assertNotEquals(card.getDisplayPeriodEnd(),expiry);
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenPutRequestToCard_then200ResponseAndCreatedIsChanged() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/cards/7/extenddisplayperiod")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenPutRequestToCard_andMoreThan48HoursBeforeExpiry_then406Response() throws Exception {
        doReturn(false).when(cardService).checkCardWithinExtendDateRange(any(Card.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/cards/6/extenddisplayperiod")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenPutRequestToCard_andAfterExpiry_then200Response() throws Exception {
        Card card = cardService.findCardById(8);
        LocalDateTime expiry = card.getDisplayPeriodEnd();
        mockMvc.perform(MockMvcRequestBuilders.put("/cards/8/extenddisplayperiod")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
        card = cardService.findCardById(8);
        Assertions.assertNotEquals(card.getDisplayPeriodEnd(),expiry);
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenPutRequestToCard_andNotAuthorisedToExtendCard_then403Response() throws Exception {

        doReturn(false).when(cardService).checkUserHasPermissionForCard(any(Card.class), any(User.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/cards/7/extenddisplayperiod")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
