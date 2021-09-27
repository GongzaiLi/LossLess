package com.seng302.wasteless.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Class representing a Notification that a user receives.
 * This class creates a Notification JPA entity that is mapped to an SQL table.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Notification {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private Integer id;

    @Column(name = "user_id")
    @NotNull(message = "User ID is mandatory")
    private Integer userId;

    @NotNull(message = "Type is mandatory")
    @Column(name = "type")
    private NotificationType type;

    @Column(name = "message")
    @Size(max = 250)
    private String message;

    /**
     * The Id of the 'subject' the notification refers to. What exactly the subject is is context-dependent.
     * For example, if this notification is for a purchase of a listing, then the ID will be the ID of the
     * corresponding sale record. If this is for a marketplace card expiry warning, then the ID is the ID of the
     * marketplace card.
     */
    @Column(name = "subjectId")
    private String subjectId;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "notification_read") //Was called "read". Which was a reserved word in MariaDB. Had to be renamed!
    private Boolean read = false;

    @Column(name = "archived")
    private Boolean archived = false;

    @Column(name = "starred")
    private Boolean starred = false;

    @Column(name = "tag")
    private NotificationTag tag;
}

