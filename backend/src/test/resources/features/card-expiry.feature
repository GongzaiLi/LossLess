Feature: UCM5 - Card Expiry
  As a user, I need to be able to remove cards that are no longer needed.

  Scenario: AC1 - I can delete a card that i created
    Given I am logged in as the user "user@test.com" with UserId 2
    And a Card exists with creatorId 2
    When I delete the card
    Then The users card is deleted

  Scenario: AC1 - I cannot delete a card that someone else created
    Given I am logged in as the user "user@test.com" with UserId 2
    And a Card exists with a different creatorId 3
    When I delete the other card
    Then The other card is not deleted

  Scenario: AC1 - As an Gaa i can delete a card that is not mine
    Given I am logged in as the GAA "admin@test.com" with UserId 3
    And a Card exists with creatorId 2
    When I delete the card as admin
    Then The users card is deleted