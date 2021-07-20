package com.seng302.wasteless.unitTest;

import com.seng302.wasteless.controller.CardController;
import com.seng302.wasteless.dto.mapper.GetUserDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.repository.CardRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CardController.class)
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

        Card card = new Card();
        card.setId(1);
        card.setCreator(userForCard);
        card.setSection(CardSections.FOR_SALE);
        card.setTitle("Sale");
        card.setKeywords(keywords);

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
                .when(cardService.findBySection(CardSections.FOR_SALE))
                .thenReturn(Collections.singletonList(card));

        Mockito
                .when(cardService.findCardById(1))
                .thenReturn(card);
        Mockito
                .when(cardService.findCardById(5))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Card with given ID does not exist"));

        Mockito
                .when(cardService.findById(2))
                .thenReturn(cardTwo);

        doReturn(userForCard).when(userService).findUserById(any(Integer.class));
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "The section specified is not one of 'ForSale', 'Wanted', or 'Exchange'")).when(cardService).checkValidSection("Invalid");

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
    void whenPostRequestToAddCard_andKeywordsIsLessThan1_then400Response() throws Exception {
        String jsonInStringForRequest = "{\"section\": \"ForSale\", \"title\": \"1982 Lada Samara\", \"keywords\": []}";

        mockMvc.perform(MockMvcRequestBuilders.post("/cards")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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
                .andExpect(jsonPath("[0].id", is(1)))
                .andExpect(jsonPath("[0].title", is("Sale")))
                .andExpect(jsonPath("[0].creator.id", is(1)))
                .andExpect(jsonPath("[1]").doesNotExist());
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
    void whenGetCardByIdRequestToCards_andValidId_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cards/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("title", is("Sale")))
                .andExpect(jsonPath("creator.id", is(1)));
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenGetCardByIdRequestToCards_andIdIsInvalid_then406Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cards/5")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenPostCard_andDescriptionIsMoreThan250_then400Response() throws Exception {
        String description = "a".repeat(1000);
        String jsonInStringForRequest = "{\"section\": \"ForSale\", \"title\": \"1982 Lada Samara\", \"keywords\": [\"Vehicle\"], \"description\" : \"" + description + "\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/cards")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenPostCard_andTitleIsMoreThan50_then400Response() throws Exception {
        String title = "a".repeat(100);
        String jsonInStringForRequest = String.format("{\"section\": \"ForSale\", \"title\": \"{}\", \"keywords\": [\"Vehicle\"],", title);

        mockMvc.perform(MockMvcRequestBuilders.post("/cards")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenPostCard_andSectionIsInvalid_then400Response() throws Exception {
        String jsonInStringForRequest = "{\"section\": \"Invalid\", \"title\": \"1982 Lada Samara\", \"keywords\": [\"Vehicle\", \"Car\"]}";

        mockMvc.perform(MockMvcRequestBuilders.post("/cards")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenPostCard_andKeywordElementIsMoreThan10_then400Response() throws Exception {
        String jsonInStringForRequest = "{\"section\": \"ForSale\", \"title\": \"1982 Lada Samara\", \"keywords\": [\"NiceKnowingThatTheKeyWordCantBeLong\"]}";

        mockMvc.perform(MockMvcRequestBuilders.post("/cards")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenDeleteRequestToCards_andCardExistsAndIsNotOwnedByRequester_then403Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/cards/2")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "demo@gmail.com", password = "pwd", roles = "USER")
    void whenDeleteRequestToCards_andCardExistsAndIsOwnedByRequester_then406Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/cards/3")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

}
