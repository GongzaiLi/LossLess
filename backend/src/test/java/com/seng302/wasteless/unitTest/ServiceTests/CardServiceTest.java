package com.seng302.wasteless.unitTest.ServiceTests;


import com.seng302.wasteless.model.*;
import com.seng302.wasteless.repository.CardRepository;
import com.seng302.wasteless.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@WebMvcTest(CardService.class)
class CardServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private UserService userService;

    private User userForCards;

    private List<String> keywords;

    @BeforeEach
    void setUp() {
        userForCards = new User();
        userForCards.setId(1);
        userForCards.setEmail("demo@gmail.com");
        userForCards.setRole(UserRoles.USER);
        userForCards.setCreated(LocalDate.now());
        userForCards.setDateOfBirth(LocalDate.now());
        userForCards.setHomeAddress(Mockito.mock(Address.class));

        keywords = new ArrayList<>();
        keywords.add("Vehicle");
        keywords.add("Car");
    }

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

    @Test
    void when_checkCardWithinExtendDateRange_andCardMoreThan48HoursBeforeExpiry_thenFalseResponse() {
        Card card = new Card();
        card.setId(1);
        card.setCreator(userForCards);
        card.setSection(CardSections.FOR_SALE);
        card.setTitle("Sale");
        card.setKeywords(keywords);
        card.setDisplayPeriodEnd(LocalDateTime.now().plusHours(72));

        CardService cardService = new CardService(cardRepository);
        assertFalse(cardService.checkCardWithinExtendDateRange(card));
    }

    @Test
    void when_checkCardWithinExtendDateRange_andCardWithin48HoursBeforeExpiry_thenTrueResponse() {
        Card card = new Card();
        card.setId(1);
        card.setCreator(userForCards);
        card.setSection(CardSections.FOR_SALE);
        card.setTitle("Sale");
        card.setKeywords(keywords);
        card.setDisplayPeriodEnd(LocalDateTime.now().plusHours(24));

        CardService cardService = new CardService(cardRepository);
        assertTrue(cardService.checkCardWithinExtendDateRange(card));
    }

    @Test
    void when_checkCardWithinExtendDateRange_andCardWithin48HoursAfterExpiry_thenTrueResponse() {
        Card card = new Card();
        card.setId(1);
        card.setCreator(userForCards);
        card.setSection(CardSections.FOR_SALE);
        card.setTitle("Sale");
        card.setKeywords(keywords);
        card.setDisplayPeriodEnd(LocalDateTime.now().minusHours(24));

        CardService cardService = new CardService(cardRepository);
        assertTrue(cardService.checkCardWithinExtendDateRange(card));
    }

    @Test
    void when_checkCardWithinExtendDateRange_andCardAfter48HoursAfterExpiry_thenTrueResponse() {
        Card card = new Card();
        card.setId(1);
        card.setCreator(userForCards);
        card.setSection(CardSections.FOR_SALE);
        card.setTitle("Sale");
        card.setKeywords(keywords);
        card.setDisplayPeriodEnd(LocalDateTime.now().minusHours(72));

        CardService cardService = new CardService(cardRepository);
        assertTrue(cardService.checkCardWithinExtendDateRange(card));
    }

    @Test
    void when_checkUserHasPermissionForCard_andUserIsCreatorOfCard_thenTrueResponse() {
        Card card = new Card();
        card.setId(1);
        card.setCreator(userForCards);
        card.setSection(CardSections.FOR_SALE);
        card.setTitle("Sale");
        card.setKeywords(keywords);
        card.setDisplayPeriodEnd(LocalDateTime.now().minusHours(72));

        CardService cardService = new CardService(cardRepository);
        assertTrue(cardService.checkUserHasPermissionForCard(card, userForCards));
    }

    @Test
    void when_checkUserHasPermissionForCard_andUserNotCreatorOfCard_thenFalseResponse() {
        Card card = new Card();
        card.setId(1);
        card.setCreator(userForCards);
        card.setSection(CardSections.FOR_SALE);
        card.setTitle("Sale");
        card.setKeywords(keywords);
        card.setDisplayPeriodEnd(LocalDateTime.now().minusHours(72));

        User notCreator = new User();
        notCreator.setId(2);
        notCreator.setEmail("notCardCreator@gmail.com");
        notCreator.setRole(UserRoles.USER);
        notCreator.setCreated(LocalDate.now());
        notCreator.setDateOfBirth(LocalDate.now());
        notCreator.setHomeAddress(Mockito.mock(Address.class));

        CardService cardService = new CardService(cardRepository);
        assertFalse(cardService.checkUserHasPermissionForCard(card, notCreator));
    }

    @Test
    void when_checkUserHasPermissionForCard_andUserNotCreatorOfCard_butUserIsGlobalAdmin_thenTrueResponse() {
        Card card = new Card();
        card.setId(1);
        card.setCreator(userForCards);
        card.setSection(CardSections.FOR_SALE);
        card.setTitle("Sale");
        card.setKeywords(keywords);
        card.setDisplayPeriodEnd(LocalDateTime.now().minusHours(72));

        User notCreator = new User();
        notCreator.setId(2);
        notCreator.setEmail("notCardCreator@gmail.com");
        notCreator.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);
        notCreator.setCreated(LocalDate.now());
        notCreator.setDateOfBirth(LocalDate.now());
        notCreator.setHomeAddress(Mockito.mock(Address.class));

        CardService cardService = new CardService(cardRepository);
        assertTrue(cardService.checkUserHasPermissionForCard(card, notCreator));
    }

    @Test
    void when_checkUserHasPermissionForCard_andUserNotCreatorOfCard_butUserIsDefaultGlobalAdmin_thenTrueResponse() {
        Card card = new Card();
        card.setId(1);
        card.setCreator(userForCards);
        card.setSection(CardSections.FOR_SALE);
        card.setTitle("Sale");
        card.setKeywords(keywords);
        card.setDisplayPeriodEnd(LocalDateTime.now().minusHours(72));

        User notCreator = new User();
        notCreator.setId(2);
        notCreator.setEmail("notCardCreator@gmail.com");
        notCreator.setRole(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN);
        notCreator.setCreated(LocalDate.now());
        notCreator.setDateOfBirth(LocalDate.now());
        notCreator.setHomeAddress(Mockito.mock(Address.class));

        CardService cardService = new CardService(cardRepository);
        assertTrue(cardService.checkUserHasPermissionForCard(card, notCreator));
    }

}

