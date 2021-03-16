package com.seng302.wasteless.Business;

/**
 * This enum specifies types for businesses and provides functionality
 * for checking to see if a business is of a legal type
 */
public enum BusinessTypes {
    ACCOMMODATION_AND_FOOD_SERVICES("Accommodation and Food Services"),
    RETAIL_TRADE("Retail Trade"),
    CHARITABLE_ORGANISATION("Charitable organisation"),
    NON_PROFIT_ORGANISATION("Non-profit organisation")
    ;

    /**
     * The text of the enum element
     */
    private final String text;

    /**
     * @param text  The text of the enum
     */
    BusinessTypes(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }

}
