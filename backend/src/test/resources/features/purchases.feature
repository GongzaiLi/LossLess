Feature: U31 - Purchases
  As a logged-in user,
  I can buy something offered for sale,
  So that I can make purchases.

  Background:
    Given We are logged in as the user that has email "a@a"

  Scenario: AC3: The seller’s inventory is updated to reflect the fact that the goods sold are no longer in stock.
    Given A listing exists with quantity 2 and its inventory item has quantity 4
    When I purchase that listing
    Then The inventory item's quantity is 2

  Scenario: AC3: The seller’s inventory is updated to reflect the fact that the inventory is sold out.
    Given A listing exists with quantity 2 and its inventory item has quantity 2
    When I purchase that listing
    Then The inventory item's quantity is 0

  Scenario: AC4: The sale listing is removed and will not appear in future searches.
    Given A listing exists with name "Very Unique Name Please Don't Duplicate This Anywhere Else", quantity 2 and its inventory item has quantity 2
    When I purchase that listing
    Then The sale listing does not appear when I search for it by name
