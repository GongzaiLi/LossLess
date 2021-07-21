package com.seng302.wasteless.service;

import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * This service handles automatically notifying and deleting expiring cards.
 */
@Component
@EnableScheduling
@PropertySource("classpath:card-expiry.properties")
public class CardExpiryService {
    CardService cardService;
    UserService userService;

    public CardExpiryService(UserService userService, CardService cardService) {
        this.userService = userService;
        this.cardService = cardService;
    }

    /**
     * This scheduled task handles automatically notifying and deleting expiring cards.
     * The period is determined by the check-card-expiry-period-ms property in the
     * resources/card-expiry.properties file
     */
    @Scheduled(fixedDelayString = "${check-card-expiry-period-ms}")
    public void scheduleCheckCardExpiry() {
    }
}
