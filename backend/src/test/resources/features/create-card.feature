Feature: UCM1 - Card Creation
  As a user I need to be able to create a card

  Scenario: AC1, AC3, AC5 - I can create a card with a section, a title, and with keywords that are all valid
    Given I am logged in as the user "a@a"
    When I create a card with section "ForSale", title "1982 Lada Samara", keywords "Vehicle", "Cars"
    Then The card is created

  Scenario: AC1 - I cannot create a card if I input an invalid section
    Given I am logged in as the user "a@a"
    When I try to create a card with section "Invalid", title "Example", keywords "Vehicle"
    Then The card is not created

  Scenario: AC3 - I cannot create a card if I input a title that is longer than 50 (too long to fit tabular display)
    Given I am logged in as the user "a@a"
    When I try to create a card with section "ForSale", and a long title "A very very very very very very very very long title", keywords "Vehicle"
    Then The card is not created

  Scenario: AC5 - I cannot create a card if I input more than 5 keywords
    Given I am logged in as the user "a@a"
    When I try to create a card with section "ForSale", title "Example", and too many keywords "Vehicle", "Car", "Vintage", "Old", "Priceless", "Sedan"
    Then The card is not created