#
#
#Feature: U5 - Creating Business Accounts
#  Scenario: AC1: As a logged-in individual, I can create/register a Business Account.
#    Given There is no business with name "Matthias' Minions"
#    When I create a business with name "Matthias' Minions", description "The finest minion memes", type of business "Charitable organisation", street number "420", street name "Sesame Street", city "SENG City", country "New Zealand", postcode "1234"
#    Then The event is created with the correct name, description, address and type