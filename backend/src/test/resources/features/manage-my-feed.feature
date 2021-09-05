Feature: U32 Managing my feed
  So that my home page feed doesn't get cluttered,
  As a logged-in user,
  I need to be able to manage the items in my feed.

  Background:
    Given We are logged in as a person with email "a@a"
    And We have a notification

  Scenario: AC2: I can easily distinguish between items I have clicked on (“read”) and those I haven’t (“unread”)
    Given My notification has not been read
    When I mark it as read
    Then The notification appears as read

  Scenario: AC3: I can “star” items to mark them as high importance.
    Given My notification had not been starred
    When I star it
    Then The notification appears as starred

  Scenario: AC4: I can archive items. Archived items are removed from the feed.
    Given My notification had not been archived
    When I archive it
    Then The notification no longer appears in my feed

