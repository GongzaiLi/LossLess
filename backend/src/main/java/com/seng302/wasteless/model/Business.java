package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.view.BusinessViews;
import com.seng302.wasteless.view.ListingViews;
import com.seng302.wasteless.view.PurchasedListingView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * An implementation of Business model.
 * This class creates a Business JPA entity that is mapped to an SQL table.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString(exclude = {"primaryAdministrator", "administrators"}) // generate a toString method, excluded to prevent recursive problems
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Business {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @JsonView({BusinessViews.SearchBusinessesView.class, PurchasedListingView.GetPurchasedListingView.class})
    private Integer id;

    @JoinColumn(name = "primary_administrator")
    @OneToOne
    private User primaryAdministrator;

    @Column(name = "administrators")
    @ManyToMany(fetch = FetchType.EAGER) //Eager so it is actually retrieved for testing
    private List<User> administrators;

    @JsonView({BusinessViews.PostBusinessRequestView.class, BusinessViews.SearchBusinessesView.class, ListingViews.GetListingView.class, PurchasedListingView.GetPurchasedListingView.class})
    @NotBlank(message = "name is mandatory")
    @Column(name = "name")
    @Size(min = 0, max = 50)
    private String name;

    @JsonView({BusinessViews.PostBusinessRequestView.class, BusinessViews.SearchBusinessesView.class, ListingViews.GetListingView.class, PurchasedListingView.GetPurchasedListingView.class})
    @Column(name = "description")
    @Size(min = 0, max = 250)
    private String description;

    @JsonView({BusinessViews.PostBusinessRequestView.class, BusinessViews.SearchBusinessesView.class, ListingViews.GetListingView.class, PurchasedListingView.GetPurchasedListingView.class})
    @NotNull
    @OneToOne
    @JoinColumn(name = "address") // map camelcase name (java) to snake case (SQL)
    private Address address;

    @JsonView({BusinessViews.PostBusinessRequestView.class, BusinessViews.SearchBusinessesView.class, ListingViews.GetListingView.class, PurchasedListingView.GetPurchasedListingView.class})
    @NotNull(message = "businessType is mandatory")
    @Column(name = "business_type")
    private BusinessTypes businessType;

    @Column(name = "created")
    private LocalDate created;

    @JoinColumn(name = "profile_image")
    @OneToOne
    private Image profileImage;

    /**
     * Add an administrator to a business
     * Never call this directly, only call it from business service.
     *
     * @param user The user to add to the list of administrators
     */
    public void addAdministrator(User user) {
        this.administrators.add(user);
    }

    public void removeAdministrator(User user) {this.administrators.remove(user);}

    /**
     * Check if the given user is an administrator of this business
     * @param user The user to check administrative status of
     * @return true if user admin, false otherwise
     */
    public boolean checkUserIsAdministrator(User user) {
        for (User administrator: administrators) {
            if (user.getId().equals(administrator.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the given user is the primary administrator of this business
     * @param user The user to check primary administrative status of
     * @return true if user primary admin, false otherwise
     */
    public boolean checkUserIsPrimaryAdministrator(User user) {
        return primaryAdministrator.getId().equals(user.getId());
    }


}
