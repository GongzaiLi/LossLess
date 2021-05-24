Feature: U22 - List sale
  As a user
  Has a business
  As a business administrator
  I want to create a new Listing

  Scenario: User creates a Sale Listing Successfully
    Given User: "a@a" is logged in
    And User: "a@a" is an administrator for business 1
    And There is an inventory item with an inventory id 1, productId "1-PRODUCT-1"
    When Create A Listing with full detail: inventory item Id 1, quantity 1, price 20.00, moreInfo "Seller may be willing to consider near offers", closes "2022-05-23"
    Then The user will be able to see the new Listing


  Scenario: User creates a Sale Listing but Inventory is not set
    Given User: "a@a" is logged in
    And User: "a@a" is an administrator for business 1
    And There is an inventory item with an inventory id 1, productId "1-PRODUCT-1"
    When Create A Listing without inventory item Id: quantity 1, price 20.0, moreInfo "Seller may be willing to consider near offers", closes "2022-05-23"
    Then The user will be able to see the bad request

  Scenario: User creates a Sale Listing but Inventory do not exist
    Given User: "a@a" is logged in
    And User: "a@a" is an administrator for business 1
    And There is an inventory item with an inventory id 1, productId "1-PRODUCT-1"
    When Create A Listing with not exist inventory item Id: inventory item Id 999, quantity 1, price 20.00, moreInfo "Seller may be willing to consider near offers", closes "2022-05-23"
    Then The user will receive will receive an error message of "Inventory with given id does not exist"


  Scenario: User creates a Sale Listing but without an expires date
    Given User: "a@a" is logged in
    And User: "a@a" is an administrator for business 1
    And There is an inventory item with an inventory id 2, productId "1-PRODUCT-1"
    When Create A Listing without closes: inventory item Id 2, quantity 1, price 20.00, moreInfo "Seller may be willing to consider near offers"
    Then The user will be able to see the new Listing

  Scenario: User creates a Sale Listing but quantity over the Inventory had
    Given User: "a@a" is logged in
    And User: "a@a" is an administrator for business 1
    And There is an inventory item with an inventory id 1, productId "1-PRODUCT-1"
    When Create A Listing with quantity over the Inventory had: inventory item Id 1, quantity 15, price 20.00, moreInfo "Seller may be willing to consider near offers", closes "2022-05-23"
    Then The user will receive an Listing created successfully message and The Listing ID "Listing quantity greater than available inventory quantity."

  Scenario: User creates a Sale Listing but without quantity
    Given User: "a@a" is logged in
    And User: "a@a" is an administrator for business 1
    And There is an inventory item with an inventory id 1, productId "1-PRODUCT-1"
    When Create A Listing without quantity: inventory item Id 1, price 20.0, moreInfo "Seller may be willing to consider near offers", closes "2022-05-23"
    Then The user will be able to see the bad request