Feature: U32 Managing my feed
  So that my home page feed doesn't get cluttered,
  As a logged-in user,
  I need to be able to manage the items in my feed.

  Background:
    Given We are logged in as a person with email "a@a"
    And We have a notification

  Scenario: AC1 - I can delete any item from my feed.
    When I delete the notification with id 1
    Then The users notification with id 1 is deleted

  Scenario: AC2: I can easily distinguish between items I have clicked on (“read”) and those I haven’t (“unread”)
    Given My notification has not been read
    When I mark it as read
    Then The notification appears as read

  Scenario: AC3: I can “star” items to mark them as high importance.
    Given My notification had not been starred and is not the newest notification
    When I star it
    Then The starred notification is at the top of my feed

  Scenario: AC3: Starred items remain at the top of my feed even when new items arrive.
    Given My notification has been starred
    When A new notification arrives
    Then The starred notification is at the top of my feed

  Scenario: AC4: I can archive items. Archived items are removed from the feed.
    Given My notification had not been archived
    When I archive it
    Then The notification no longer appears in my feed

  Scenario: AC6: I can “tag” an item.
    Given My notification has no tag
    When I add the tag "YELLOW"
    Then The notification appears as tagged "YELLOW"

  Scenario: AC6: Each item can have at most one tag.
    Given My notification has been tagged as "BLUE"
    When I add the tag "YELLOW"
    Then The notification appears as tagged "YELLOW"

  Scenario: AC6: Tags can be removed.
    Given My notification has been tagged as "BLUE"
    When I remove the tag
    Then My tag has been removed

  Scenario: AC7: Filter notifications by one tag.
    Given We have 10 notifications and notifications with odd are tagged as "RED" and notification with even id are tagged as "BLACK"
    When filter notifications by tags:
      | RED |
    Then The Filtered notifications result are all tagged as "RED"

  Scenario: AC7: Filter notifications by tags.
    Given We have 10 notifications and notifications with odd are tagged as "RED" and notification with even id are tagged as "BLACK"
    When filter notifications by tags:
      | RED   |
      | BLACK |
    Then The Filtered notifications result are all tagged as tagged "RED" or "BLACK"