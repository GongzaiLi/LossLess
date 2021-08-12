Feature: U30 - Individual full sale listing
  So that I can take action on an individual sale,
  as a logged-in individual user,
  I need to be able to inspect a sale listing and take appropriate action.


  Background:
    Given We are logged in as a user with email "a@a"
    And There exists the following listings:
      | Literally anything else | 1   |

  Scenario: AC3 -  I can like a listing.
    Given I have not liked the listing with id "1"
    When I like a listing with id "1"
    Then The listing with id "1" is added to the list of my liked listings and total likes on the listing are increased

  Scenario: AC5 - I can like a listing at most once.
    Given I have liked the listing  with id "1"
    When I like a listing with id "1"
    Then The listing with id "1" is no longer in the list of my liked listings

  Scenario: AC6 - I can unlike a listing which will decrement the total likes on the listing.
    Given I have liked the listing  with id "1"
    When I like a listing with id "1"
    Then The listing with id "1" is no longer in the list of my liked listings and total likes on the listing are decreased


