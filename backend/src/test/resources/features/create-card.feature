Feature: UCM1 - Card Creation
  As a user I need to be able to create a card

  Scenario: AC1, AC3, AC5 - I can create a card with a section, a title, and with keywords
    Given I am logged in as the user "a@a"
    When I create a card with creatorId 2, section "ForSale", title "1982 Lada Samara", keywords "Vehicle, Vintage"
    Then The card is created