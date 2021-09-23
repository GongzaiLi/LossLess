package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.repository.MessageRepository;
import com.seng302.wasteless.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    void whenCheckOneUserOwnsCard_andReceiverOwnsCard_thenTrueResponse() {
        MessageService messageService = new MessageService(messageRepository);

        User cardCreator = new User();
        cardCreator.setId(1);

        Card card = new Card();
        card.setId(1);
        card.setCreator(cardCreator);

        messageService.checkOneUserOwnsCard(2, 1, card);
    }

    @Test
    void whenCheckOneUserOwnsCard_andSenderOwnsCard_thenTrueResponse() {
        MessageService messageService = new MessageService(messageRepository);

        User cardCreator = new User();
        cardCreator.setId(1);

        Card card = new Card();
        card.setId(1);
        card.setCreator(cardCreator);

        messageService.checkOneUserOwnsCard(1, 2, card);
    }

    @Test
    void whenCheckOneUserOwnsCard_andNeitherOwnsCard_thenFalseResponse() {
        MessageService messageService = new MessageService(messageRepository);

        User cardCreator = new User();
        cardCreator.setId(3);

        Card card = new Card();
        card.setId(1);
        card.setCreator(cardCreator);

        messageService.checkOneUserOwnsCard(2, 1, card);
    }

}
