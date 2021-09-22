Feature: UCM8 - Contacting other marketplace
  As a user I can ask questions about items for sale.

  Background:
    Given A user "x@x" has a card with card id 10, section "ForSale", title "10982 Lada Samara", keywords "Vehicle"

  Scenario: AC3 - Sending a message in a new item on the recipient’s.
    Given A user logged in as a user with email "d@d"
    When the user send a message to the user "x@x" regarding card with id 10, with the text "Hi, Scott"
    Then A message is created

  Scenario: AC3 - card owner receiver message to the user who sent the message.
    Given A user logged in as a user with email "x@x"
    When the user send a message to the user "d@d" regarding card with id 10, with the text "Hi, Nitish"
    Then A message is created


  Scenario: AC3 - A user can read the all message the user sent or received
    Given A user logged in as a user with email "d@d"
    When the user read messages with card id 10
    Then The user "d@d" get all message
      | Hi, Scott  |
      | Hi, Nitish |

  Scenario: AC3 - A card owner can read the all message they have sent or received
    Given A user logged in as a user with email "x@x"
    When the user read messages with card id 10
    Then The card owner "x@x" get all message
      | Hi, Scott  |
      | Hi, Nitish |