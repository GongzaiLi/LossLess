package com.seng302.wasteless.unitTest.ModelTests;

import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;


@RunWith(SpringRunner.class)
@WebMvcTest(UserService.class)
class UserTest {

    private User user;
    private Listing listing;

    @BeforeEach
    void setup() {
        LocalDate expiry = LocalDate.of(2022, Month.JULY, 14);
        user = new User();
        user.setListingsLiked(new HashSet<Listing>());
        listing = new Listing();
        listing.setId(1)
                .setCreated(expiry.minusMonths(3))
                .setQuantity(3)
                .setPrice(17.99)
                .setMoreInfo("Seller may be willing to consider near offers")
                .setCloses(LocalDate.of(2021, Month.DECEMBER, 1));

    }

    @Test
    void whenToggleListingLike_AndListingNotLiked_ReturnTrueLikeListingAndIncreaseTotalLikes() {
        Assertions.assertTrue(user.toggleListingLike(listing));
        Assertions.assertSame(listing, user.getListingsLiked().iterator().next());
        Assertions.assertEquals(1, listing.getUsersLiked());

    }

    @Test
    void whenToggleListingLike_AndListingLiked_ReturnFalseUnlikeListingAndDecreaseTotalLikes() {
        Set<Listing> listings = new HashSet<>();
        listing.setUsersLiked(1);
        listings.add(listing);
        user.setListingsLiked(listings);
        Assertions.assertFalse(user.toggleListingLike(listing));
        Assertions.assertEquals(0, user.getListingsLiked().size());
        Assertions.assertEquals(0, listing.getUsersLiked());

    }

}
