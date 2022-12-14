package com.seng302.wasteless.unitTest.ControllerTests;

import com.seng302.wasteless.controller.MessageController;
import com.seng302.wasteless.dto.GetMessageDto;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.MessageService;
import com.seng302.wasteless.service.NotificationService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false) //Disable spring security for the unit tests
class MessageControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private CardService cardService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private NotificationService notificationService;

    private User user;


    private User userForCard;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        user.setId(1);
        user.setEmail("james@gmail.com");
        user.setRole(UserRoles.USER);


        userForCard = new User();
        userForCard.setId(2);
        userForCard.setEmail("demo@gmail.com");
        userForCard.setRole(UserRoles.USER);
        userForCard.setCreated(LocalDate.now());
        userForCard.setDateOfBirth(LocalDate.now());
        userForCard.setHomeAddress(Mockito.mock(Address.class));

        List<String> keywords = new ArrayList<>();
        keywords.add("Vehicle");
        keywords.add("Car");

        Message message = new Message();
        message.setMessageText("Hello");
        message.setReceiverId(1);
        message.setSenderId(2);
        message.setTimestamp(LocalDateTime.now());

        Card card = new Card();
        card.setId(1);
        card.setCreator(userForCard);
        card.setSection(CardSections.FOR_SALE);
        card.setTitle("Sale");
        card.setKeywords(keywords);
        card.setDisplayPeriodEnd(LocalDateTime.now().plusYears(2));

        Mockito
                .when(authentication.getName())
                .thenReturn("james@gmail.com");

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(user);

        Mockito
                .when(cardService.findCardById(1))
                .thenReturn(card);

        Mockito
                .when(cardService.findCardById(2))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Card with given ID does not exist"));

        Mockito
                .when(userService.findUserById(1))
                .thenReturn(user);

        Mockito
                .when(userService.findUserById(2))
                .thenReturn(userForCard);

        Mockito
                .when(userService.findUserById(3))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "User does not exist"));

        Mockito
                .when(messageService.checkOneUserOwnsCard(anyInt(), anyInt(), any(Card.class)))
                .thenReturn(true);

        Mockito
                .when(messageService.createMessage(any(Message.class)))
                .thenReturn(message);



        List<Message> messages = new ArrayList<>();
        messages.add(message);
        messages.add(message);
        messages.add(message);

        User user1 = new User();
        user1.setId(1);
        User user3 = new User();
        user3.setId(3);

        GetMessageDto messageDto1 = new GetMessageDto(1, user1, userForCard, messages);
        GetMessageDto messageDto2 = new GetMessageDto(1, user3, userForCard, messages);

        List<GetMessageDto> messageDtos = new ArrayList<>();
        messageDtos.add(messageDto1);
        messageDtos.add(messageDto2);

        Mockito
                .when(messageService.findAllMessagesForUserOnCardTheyDontOwn(any(User.class), any(Card.class)))
                .thenReturn(messageDto1);

        Mockito
                .when(messageService.findAllMessagesForUserOnCardTheyDoOwn(anyInt(), any(Card.class)))
                .thenReturn(messageDtos);
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToCreateMessage_andAllFieldsValid_andSenderIsNotCardOwner_then201Response() throws Exception {
        String jsonInStringForRequest = "{\"receiverId\": 2, \"messageText\": \"Hello\", \"cardId\": 1}";

        mockMvc.perform(MockMvcRequestBuilders.post("/messages")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToCreateMessage_andAllFieldsValid_andSenderIsCardOwner_then201Response() throws Exception {
        String jsonInStringForRequest = "{\"receiverId\": 1, \"messageText\": \"Hello\", \"cardId\": 1}";

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(userForCard);

        mockMvc.perform(MockMvcRequestBuilders.post("/messages")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToCreateMessage_andSenderIsReceiver_then400Response() throws Exception {
        String jsonInStringForRequest = "{\"receiverId\": 2, \"messageText\": \"Hello\", \"cardId\": 1}";

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(userForCard);

        mockMvc.perform(MockMvcRequestBuilders.post("/messages")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToCreateMessage__andReceiverDoesNotExist_then400Response() throws Exception {


        String jsonInStringForRequest = "{\"receiverId\": 3, \"messageText\": \"Hello\", \"cardId\": 1}";

        mockMvc.perform(MockMvcRequestBuilders.post("/messages")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToCreateMessage__andCardDoesNotExist_then400Response() throws Exception {
        String jsonInStringForRequest = "{\"receiverId\": 2, \"messageText\": \"Hello\", \"cardId\": 2}";

        mockMvc.perform(MockMvcRequestBuilders.post("/messages")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToCreateMessage_andNeitherSenderNorReceiverOwnsCard_then400Response() throws Exception {

        Mockito
                .when(messageService.checkOneUserOwnsCard(anyInt(), anyInt(), any(Card.class)))
                .thenReturn(false);

        String jsonInStringForRequest = "{\"receiverId\": 2, \"messageText\": \"Hello\", \"cardId\": 1}";

        mockMvc.perform(MockMvcRequestBuilders.post("/messages")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void whenGetRequestToMessagesEndpoint_andUserIsNotCardOwner_thenUserGetMessagesForCorrectCard() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("cardId", is(1)));
    }



    @Test
    void whenGetRequestToMessagesEndpoint_andCardDoesntExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/messages/2")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void whenGetRequestToMessagesEndpoint_andUserDoesNotExist() throws Exception {
        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session token is invalid"));
        mockMvc.perform(MockMvcRequestBuilders.get("/messages/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenGetRequestToMessagesEndpoint_andUserIsNotCardOwner_thenUserGetMessagesBetweenThemAndCardOwnerForTheGivenCard() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("cardId", is(1)))
                .andExpect(jsonPath("messages[0].messageText", is("Hello")))
                .andExpect(jsonPath("messages[1].messageText", is("Hello")))
                .andExpect(jsonPath("messages[2].messageText", is("Hello")));
    }

    @Test
    void whenGetRequestToMessagesEndpoint_andUserIsCardOwner_thenUserGetMessagesBetweenThemAndAllUsersWhoHaveMessaged_forTheGivenCard() throws Exception {

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(userForCard);

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].cardOwner.id", is(2)))
                .andExpect(jsonPath("[0].otherUser.id", is(1)))
                .andExpect(jsonPath("[1].cardOwner.id", is(2)))
                .andExpect(jsonPath("[1].otherUser.id", is(3)));
    }

}
