package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.view.MessageViews;
import com.seng302.wasteless.view.PurchasedListingView;
import com.seng302.wasteless.view.UserViews;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * An implementation of User model.
 * This class creates a User JPA entity that is mapped to an SQL table.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class User {

    private static final Logger logger = LogManager.getLogger(User.class.getName());

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @JsonView({PurchasedListingView.GetPurchasedListingView.class, MessageViews.GetMessageView.class})
    private Integer id;

    @Column(name = "first_name") // map camelcase name (java) to snake case (SQL)
    @NotBlank(message = "firstName is mandatory")
    @JsonView({UserViews.PostUserRequestView.class, PurchasedListingView.GetPurchasedListingView.class, MessageViews.GetMessageView.class})
    @Size(min = 0, max = 50)
    private String firstName;

    @NotBlank(message = "lastName is mandatory")
    @Column(name = "last_name") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class, PurchasedListingView.GetPurchasedListingView.class, MessageViews.GetMessageView.class})
    @Size(min = 0, max = 50)
    private String lastName;

    @Column(name = "middle_name") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class, PurchasedListingView.GetPurchasedListingView.class, MessageViews.GetMessageView.class})
    @Size(min = 0, max = 50)
    private String middleName;

    @Column(name = "nick_name") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class, PurchasedListingView.GetPurchasedListingView.class})
    @Size(min = 0, max = 50)
    private String nickname;

    @Column(name = "bio") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class, PurchasedListingView.GetPurchasedListingView.class})
    @Size(min = 0, max = 250)
    private String bio;

    @Email
    @NotBlank(message = "email is mandatory")
    @Column(name = "email") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class})
    @Size(min = 0, max = 50)
    private String email;

    @Past
    @NotNull(message = "dateOfBirth is mandatory")
    @Column(name = "date_of_birth") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class})
    //Date format validated by spring. See spring.mvc.format.date=yyyy-MM-dd in application.properties
    private LocalDate dateOfBirth;

    @Column(name = "phone_number") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class})
    @Size(min = 0, max = 50)
    private String phoneNumber;

    @JsonView({UserViews.PostUserRequestView.class})
    @NotNull(message = "homeAddress is mandatory")
    @OneToOne
    @JoinColumn(name = "home_address") // map camelcase name (java) to snake case (SQL)
    private Address homeAddress;

    @NotBlank(message = "password is mandatory")
    @JsonView({UserViews.PostUserRequestView.class})
    @Column(name = "password") // map camelcase name (java) to snake case (SQL)
    @Size(min = 8, max = 100)
    private String password;

    @Column(name = "businesses_primarily_administered")
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Business> businessesPrimarilyAdministered;

    @Column(name = "created") // map camelcase name (java) to snake case (SQL)
    private LocalDate created;

    @Column(name = "role")
    private UserRoles role;

    @JoinColumn(name = "listing_liked")
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Listing> listingsLiked;

    @JoinColumn(name = "profile_image")
    @OneToOne
    @JsonView({MessageViews.GetMessageView.class})
    private Image profileImage;


    /**
     * Check this objects date is within the expected maximum and minimum date ranges
     */
    public boolean checkDateOfBirthValid() {

        LocalDate today = LocalDate.now();

        LocalDate minimumDOB = today.minusYears(13).plusDays(1);
        LocalDate maximumDOB = today.minusYears(120);

        return (this.dateOfBirth.isBefore(minimumDOB) && this.dateOfBirth.isAfter(maximumDOB));
    }
    /**
     * Check this objects date is within the expected maximum and minimum date ranges
     */
    public boolean checkIsOverSixteen() {

        LocalDate today = LocalDate.now();

        LocalDate minimumDOB = today.minusYears(16).plusDays(1);
        LocalDate maximumDOB = today.minusYears(120);

        return (this.dateOfBirth.isBefore(minimumDOB) && this.dateOfBirth.isAfter(maximumDOB));
    }
    /**
     * Add a business to the list of businessesPrimarilyAdministered.
     *
     * Never call this directly, only call it from user service.
     *
     * @param business  The business to add to the list of businesses primarily administered
     */
    public void addPrimaryBusiness(Business business) {
        this.businessesPrimarilyAdministered.add(business);
    }

    /**
     * Check if a user is a global admin or default global admin
     * @return true if global admin, false otherwise
     */
    public boolean checkUserGlobalAdmin() {
        return this.role == UserRoles.GLOBAL_APPLICATION_ADMIN ||
                this.role == UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN;
    }

    /**
     * Check if user is default admin
     *
     * @return true if default admin
     */
    public boolean checkUserDefaultAdmin() {
        return this.role == UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN;
    }


    /**
     * Toggles the like on a listing,
     * if the user liked listing doesnt contain the new listing, the listing is added
     * if the user is currently 'liking' it then the like is removed by removing the listing from the set
     * the total likes on the listing is then increased or decreased depending ion if the like is added or removed
     * @param listing listing to have current user's like's toggled
     * @return true if like added, false if like removed
     */
    public boolean toggleListingLike(Listing listing) {
        Boolean likeStatus;
        if(listingsLiked.contains(listing)) {
            this.unLikeListing(listing);
            likeStatus = Boolean.FALSE;
            logger.info("Listing: {} unliked by user: {}", listing.getId(), this.id);
        } else {
            this.addLikedListing(listing);
            likeStatus = Boolean.TRUE;
            logger.info("Listing: {} liked by user: {}", listing.getId(), this.id);
        }
        return likeStatus;
    }

    /**
     * Checks if lisitng given is currently liked by the user
     * @param listing listing to check if currently liked by user
     * @return true if user has listing liked, false if listing not liked
     */
    public boolean checkUserLikesListing(Listing listing) {
        return this.listingsLiked.stream().anyMatch(candidate -> candidate.getId().equals(listing.getId()));
    }

    /**
     * Adds a like to the listing
     *
     * @param listing The listing that the like is added to
     */
    private void addLikedListing(Listing listing) { this.listingsLiked.add(listing); }

    /**
     * Removes a like to the listing
     *
     * @param listing The listing that the like is removed from
     */
    private void unLikeListing(Listing listing) {
        this.listingsLiked.remove(listing);
    }
}

