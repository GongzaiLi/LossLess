Feature: UCM1 - Card Creation

  Scenario: AC1 - I can create a card
    Given I am logged in as the user "a@a"
    When I create a card with creatorId 1, section "ForSale", title "1982 Lada Samara", keywords "Vehicle, Vintage"
    Then The card is created