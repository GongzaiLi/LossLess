Feature: U32 - Managing my feed
  So that my home page feed doesn't get cluttered, as a logged-in user, I need to be able to manage the items in my feed.

  Scenario: AC1 - I can delete any item from my feed.
    Given I am logged in as a user with email "user@test.com" and UserId 2
    And A Notification exists with id 1
    When I delete the notification with id 1
    Then The users notification with id 1 is deleted

