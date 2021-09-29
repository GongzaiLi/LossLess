package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.dto.GetMessageDto;
import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.Message;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.repository.MessageRepository;
import com.seng302.wasteless.repository.UserRepository;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.MessageService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressService addressService;


    @BeforeAll
    void setup() {

        Message message1 = new Message(1, 1, 2, "Hello 1 1 2", LocalDateTime.now());
        messageService.createMessage(message1);
        Message message2 = new Message(1, 1, 2, "Hello 1 1 2 Again", LocalDateTime.now());
        messageService.createMessage(message2);
        Message message3 = new Message(1, 2, 1, "Hello 1 2 1", LocalDateTime.now());
        messageService.createMessage(message3);
        Message message4 = new Message(1, 2, 1, "Hello 1 2 1 Again", LocalDateTime.now());
        messageService.createMessage(message4);
        Message message5 = new Message(1, 3, 1, "Hello 1 3 1", LocalDateTime.now());
        messageService.createMessage(message5);
        Message message6 = new Message(1, 1, 3, "Hello 1 1 3", LocalDateTime.now());
        messageService.createMessage(message6);
        Message message7 = new Message(1, 4, 1, "Hello 1 4 1", LocalDateTime.now());
        messageService.createMessage(message7);
        Message message8 = new Message(1, 1, 4, "Hello 1 1 4", LocalDateTime.now());
        messageService.createMessage(message8);
        Message message9 = new Message(4, 5, 6, "Hello 4 9 8", LocalDateTime.now());
        messageService.createMessage(message9);
        Message message10 = new Message(4, 6, 5, "Hello 4 8 9", LocalDateTime.now());
        messageService.createMessage(message10);


        createUser(1);
        createUser(2);
        createUser(3);
        createUser(4);
        createUser(5);
        createUser(6);

    }

    void createUser(Integer userId) {
        User user = new User();
        user.setId(userId);
        Address address = new Address();
        address.setCountry("NZ");
        address.setSuburb("Riccarton");
        address.setCity("Christchurch");
        address.setStreetNumber("1");
        address.setStreetName("Ilam Rd");
        address.setPostcode("8041");
        addressService.createAddress(address);
        user.setHomeAddress(address);
        user.setEmail(String.format("scottLi%d@a.com", userId));
        user.setFirstName("a");
        user.setLastName("b");
        user.setDateOfBirth(LocalDate.parse("1998-05-09"));
        user.setPassword("a");

        if (userRepository.findFirstById(userId) == null) {
            userService.createUser(user);
        }

    }

    @Test
    void whenCheckOneUserOwnsCard_andReceiverOwnsCard_thenTrueResponse() {

        User cardCreator = new User();
        cardCreator.setId(1);

        Card card = new Card();
        card.setId(1);
        card.setCreator(cardCreator);

        assertTrue(messageService.checkOneUserOwnsCard(2, 1, card));
    }

    @Test
    void whenCheckOneUserOwnsCard_andSenderOwnsCard_thenTrueResponse() {
        User cardCreator = new User();
        cardCreator.setId(1);

        Card card = new Card();
        card.setId(1);
        card.setCreator(cardCreator);

        assertTrue(messageService.checkOneUserOwnsCard(1, 2, card));
    }

    @Test
    void whenCheckOneUserOwnsCard_andNeitherOwnsCard_thenFalseResponse() {
        User cardCreator = new User();
        cardCreator.setId(3);

        Card card = new Card();
        card.setId(1);
        card.setCreator(cardCreator);

        assertFalse(messageService.checkOneUserOwnsCard(2, 1, card));
    }

    @Test
    void whenFindAllMessagesForUserOnCardTheyDontOwn_thenGetCorrectMessages() {
        User cardCreator = new User();
        cardCreator.setId(2);
        User user = new User();
        user.setId(1);
        GetMessageDto messageDto = messageService.findAllMessagesForUserOnCardTheyDontOwn(user, new Card().setId(1).setCreator(cardCreator));
        assertEquals(1, messageDto.getCardId());
        assertEquals(2, messageDto.getCardOwner().getId());
        assertEquals(1, messageDto.getOtherUser().getId());
        assertEquals(8, messageDto.getMessages().size());
        assertEquals("Hello 1 1 2 Again", messageDto.getMessages().get(1).getMessageText());
    }

    @Test
    void whenFindAllMessagesForUserOnCardTheyDoOwn_andOnlyOneConversation_thenGetCorrectMessages() {
        User cardCreator = new User();
        cardCreator.setId(6);

        List<GetMessageDto> messageDto = messageService.findAllMessagesForUserOnCardTheyDoOwn(6, new Card().setId(4).setCreator(cardCreator));
        assertEquals(1, messageDto.size());
    }

    @Test
    void whenFindAllMessagesForUserOnCardTheyDoOwn_andMultipleConversation_thenGetCorrectMessages() {
        User cardCreator = new User();
        cardCreator.setId(2);

        List<GetMessageDto> messageDto = messageService.findAllMessagesForUserOnCardTheyDoOwn(1, new Card().setId(1).setCreator(cardCreator));
        assertEquals(3, messageDto.size());
    }

}
