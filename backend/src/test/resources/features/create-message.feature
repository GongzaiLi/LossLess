Feature: UCM8 - Contacting other marketplace
  As a user I can ask questions about items for sale.

  Background:
    Given A user "x@x" has a card with card id 10, section "ForSale", title "1982 Lada Samara", keywords "Vehicle"

  Scenario: AC3 - Sending a message in a new item on the recipient’s.
    Given A user logged in as a user with email "d@d"
    When the user send a message to the user "x@x" regarding card with id 10, with the text "Hello"
    Then A message is created