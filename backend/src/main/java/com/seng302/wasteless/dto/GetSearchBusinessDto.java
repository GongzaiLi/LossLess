package com.seng302.wasteless.dto;


import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.view.BusinessViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Data transfer object for Search Business endpoint, used to return the correct data in the correct format.
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class GetSearchBusinessDto {

    @JsonView({BusinessViews.SearchBusinessesView.class})
    private List<Business> businesses;

    @JsonView({BusinessViews.SearchBusinessesView.class})
    private Integer totalItems;
}
