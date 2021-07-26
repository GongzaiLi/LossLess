package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.repository.CardRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;

/**
 * This service handles automatically notifying and deleting expiring cards.
 */
@Component
@EnableScheduling
@PropertySource("classpath:card-expiry.properties")
public class CardExpiryService {
    private final CardService cardService;
    private final CardRepository cardRepository;
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(CardExpiryService.class.getName());

    @Value("${notification-wait-period-seconds}")
    private Integer notificationWaitPeriodSeconds;

    @Autowired
    public CardExpiryService(CardService cardService, CardRepository cardRepository, UserService userService) {
        this.cardService = cardService;
        this.cardRepository = cardRepository;
        this.userService = userService;
    }

    /**
     * This scheduled task handles automatically notifying and deleting expiring cards.
     * The period is determined by the check-card-expiry-period-ms property in the
     * resources/card-expiry.properties file.
     * All cards for which the notification wait period (24H) has passed after their display
     * period ended will be deleted.
     */
    @Scheduled(fixedDelayString = "${check-card-expiry-period-ms}")
    public void scheduleCheckCardExpiry() {
        logger.info("[SERVER] Card Expiry Check");

        // Delete cards whose display period ended 'notificationWaitPeriodSeconds' (24h) ago.
        LocalDateTime expiryDisplayPeriodEnd = LocalDateTime.now().minusSeconds(notificationWaitPeriodSeconds);
        List<Card> expiredCards = cardRepository.findAllByDisplayPeriodEndLessThan(expiryDisplayPeriodEnd);

        for (Card card : expiredCards) {
            logger.warn("Deleting expired card id={}, created={}, title={}, creator={}", card.getId(), card.getCreated(), card.getTitle(), card.getCreator());

            User cardCreator = card.getCreator();
            cardCreator.setHasCardsDeleted(true);
            userService.saveUserChanges(cardCreator);

            cardService.deleteCard(card);
        }
    }
}