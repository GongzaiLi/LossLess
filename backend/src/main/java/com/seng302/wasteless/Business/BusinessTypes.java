package com.seng302.wasteless.Business;

public enum BusinessTypes {
    ACCOMMODATION_AND_FOOD_SERVICES("Accommodation and Food Services"),
    RETAIL_TRADE("Retail Trade"),
    CHARITABLE_ORGANISATION("Charitable organisation"),
    NON_PROFIT_ORGANISATION("Non-profit organisation")
    ;

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
