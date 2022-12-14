package com.seng302.wasteless.model;


import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.view.CardViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * An implementation of Card model for marketPlace.
 * This class creates a card JPA entity that is mapped to an SQL table.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method, excluded to prevent recursive problems
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@Accessors(chain = true) //Allows chaining of getters and setters
public class Card {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private Integer id;

    @ManyToOne
    @JsonView(CardViews.GetCardView.class)
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "section")
    @NotNull(message = "Section is mandatory")
    private CardSections section;

    @Column(name = "title")
    @Size(min = 1, max = 25)
    @NotNull(message = "Title is mandatory")
    private String title;

    @Column(name = "description")
    @Size(max = 250)
    private String description;

    @ElementCollection
    @Column(name = "keywords")
    private List<@NotBlank @NotNull @Size(max = 10)String> keywords;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "displayPeriodEnd")
    private LocalDateTime displayPeriodEnd;

    @Column(name = "expiryWarningAlreadySent", columnDefinition = "boolean default false")
    private Boolean expiryWarningAlreadySent;
}