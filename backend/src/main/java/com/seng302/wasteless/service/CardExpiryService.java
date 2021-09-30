package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.Notification;
import com.seng302.wasteless.model.NotificationType;
import com.seng302.wasteless.repository.CardRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    private final NotificationService notificationService;

    private final Logger logger = LoggerFactory.getLogger(CardExpiryService.class.getName());

    @Value("${notification-wait-period-seconds}")
    private Integer notificationWaitPeriodSeconds;

    @Autowired
    public CardExpiryService(CardService cardService, CardRepository cardRepository, NotificationService notificationService) {
        this.cardService = cardService;
        this.cardRepository = cardRepository;
        this.notificationService = notificationService;
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

        List<Card> expiringCards = cardRepository.findAllByDisplayPeriodEndLessThan(LocalDateTime.now());

        for (Card card : expiringCards) {
            if (card.getDisplayPeriodEnd().isBefore(LocalDateTime.now().minusSeconds(notificationWaitPeriodSeconds))) {
                // Delete cards whose display period ended 'notificationWaitPeriodSeconds' (24h) ago.
                logger.warn("Deleting expired card id={}, display end={}, title={}, creator={}", card.getId(), card.getDisplayPeriodEnd(), card.getTitle(), card.getCreator().getId());

                Notification notification = NotificationService.createNotification(card.getCreator().getId(),card.getId(), NotificationType.EXPIRED, String.format("Your card: %s has expired", card.getTitle()));
                notificationService.saveNotification(notification);

                cardService.deleteCard(card);
            } else {
                // Just give them a warning - they have 24h to delete/extend it
                logger.warn("Expiry warning for card id={}, display end={}, title={}, creator={}", card.getId(), card.getDisplayPeriodEnd(), card.getTitle(), card.getCreator().getId());

                Notification notification = NotificationService.createNotification(card.getCreator().getId(),card.getId(), NotificationType.EXPIRY_WARNING, String.format("Your card: %s will be deleted within 24 hours of its closing date. You can either extend its display period or delete it.", card.getTitle()));
                notificationService.saveNotification(notification);
            }
        }
    }
}
