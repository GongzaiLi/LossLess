package com.seng302.wasteless.Business;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.User.User;
import com.seng302.wasteless.User.UserViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString(exclude = {"primaryAdministrator", "administrators"}) // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Business {

    @JsonView({BusinessViews.GetBusinessView.class, UserViews.GetUserView.class})
    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private Integer id;

    @JoinColumn(name = "primary_administrator")
    @OneToOne
    private User primaryAdministrator;

    @JsonView({BusinessViews.GetBusinessView.class})
    @Column(name = "administrators")
    @ManyToMany
    private List<User> administrators;

    @JsonView({BusinessViews.PostBusinessRequestView.class, BusinessViews.GetBusinessView.class})
    @NotBlank(message = "name is mandatory")
    @Column(name = "name")
    private String name;

    @JsonView({BusinessViews.PostBusinessRequestView.class, BusinessViews.GetBusinessView.class})
    @Column(name = "description")
    private String description;

    @JsonView({BusinessViews.PostBusinessRequestView.class, BusinessViews.GetBusinessView.class})
    @NotBlank(message = "address is mandatory")
    @Column(name = "address")
    private String address;

    @JsonView({BusinessViews.PostBusinessRequestView.class, BusinessViews.GetBusinessView.class})
    @NotNull(message = "businessType is mandatory")
    @Column(name = "business_type")
    private BusinessTypes businessType;

    @JsonView({BusinessViews.GetBusinessView.class})
    @Column(name = "created")
    private LocalDate created;

}