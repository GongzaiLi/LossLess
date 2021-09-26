package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.SalesReportSinglePeriod;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Data transfer object for SalesReport endpoint, used to return the correct data in the correct format.
 * The object includes a list of the total purchased listings for a business with their total value during different periods.
 * Also includes fields which let the client know whether the start or end date truncate the start and end periods they have specified.
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class SalesReportDto {
    private List<SalesReportSinglePeriod> reportData;
    private Boolean startTruncated = false;
    private Boolean endTruncated = false;

    public SalesReportDto(List<SalesReportSinglePeriod> reportData) {
        setReportData(reportData);
    }
}
