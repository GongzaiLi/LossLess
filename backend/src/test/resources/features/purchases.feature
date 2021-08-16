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

  Scenario: AC3: The seller’s inventory is updated to reflect the fact that the inventory is sold out.
    Given A listing exists with quantity 2 and its inventory item has quantity 2
    When I purchase that listing
    Then Information about the sale (sale date, listing date, product, amount, number of likes) is recorded in a sales history for the seller’s business.

  Scenario: AC1:  When I purchase listing any other users who have liked the corresponding sale listing are notified.
    Given A listing exists with 10 user who have liked it
    When I purchase that listing
    Then Notifications are created for the 10 users who have liked the listing that was purchased

  Scenario: AC2:  When I purchase listing I receive a notification that I have purchased it
    Given A listing exists with quantity 2 and its inventory item has quantity 2
    When I purchase that listing
    Then A notifications is created telling me I have purchased the listing
