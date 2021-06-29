Feature: U22 - List sale
  As a user
  I have a business
  I am the business administrator for this business
  I want to be able to create a new listing on the listing page

  Scenario: User creates a Sale Listing Successfully
    Given The user with email "a@a" is logged in
    And The user with email "a@a" is an administrator for business 1
    And There is an inventory item with an inventory id 1 and productId "1-PRODUCT-1"
    When The user creates a listing with the inventory item ID 1, quantity 1, price 20.00, moreInfo "Seller may be willing to consider near offers", and closes "2022-05-23"
    Then The user will be able to see the new listing


  Scenario: User creates a Sale Listing but Inventory is not set
    Given The user with email "a@a" is logged in
    And The user with email "a@a" is an administrator for business 1
    And There is an inventory item with an inventory id 1 and productId "1-PRODUCT-1"
    When The user creates a listing without inventory item ID but has quantity 1, price 20.0, moreInfo "Seller may be willing to consider near offers" and closes "2022-05-23"
    Then The user will receive a bad request error


  Scenario: User creates a Sale Listing but Inventory does not exist
    Given The user with email "a@a" is logged in
    And The user with email "a@a" is an administrator for business 1
    And There is an inventory item with an inventory id 1 and productId "1-PRODUCT-1"
    When The user creates a listing with a nonexistent inventory item Id 999, quantity 1, price 20.00, moreInfo "Seller may be willing to consider near offers", and closes "2022-05-23"
    Then The user will receive a bad request error and a message "Inventory with given id does not exist"


  Scenario: User creates a Sale Listing but with a closing date in the past
    Given The user with email "a@a" is logged in
    And The user with email "a@a" is an administrator for business 1
    And There is an inventory item with an inventory id 1 and productId "1-PRODUCT-1"
    When The user creates a Listing with inventory item Id 2, quantity 1, price 20.00, moreInfo "Seller may be willing to consider near offers", and close date in the past "2020-03-21"
    Then The user will receive a bad request error


  Scenario: User creates a Sale Listing but quantity over the Inventory quantity
    Given The user with email "a@a" is logged in
    And The user with email "a@a" is an administrator for business 1
    And There is an inventory item with an inventory id 1 and productId "1-PRODUCT-1"
    When The user creates a listing with quantity over the inventory quantity, inputting inventory item Id 1, quantity 15, price 20.00, moreInfo "Seller may be willing to consider near offers", closes "2022-05-23"
    Then The user will receive a bad request error and a message "Listing quantity greater than available inventory quantity."


  Scenario: User creates a Sale Listing but without quantity
    Given The user with email "a@a" is logged in
    And The user with email "a@a" is an administrator for business 1
    And There is an inventory item with an inventory id 1 and productId "1-PRODUCT-1"
    When The user creates a listing without quantity and inputs inventory item Id 1, price 20.0, moreInfo "Seller may be willing to consider near offers", and closes "2022-05-23"
    Then The user will receive a bad request error

  Scenario: User creates a Sale Listing but the quantity is 0
    Given The user with email "a@a" is logged in
    And The user with email "a@a" is an administrator for business 1
    And There is an inventory item with an inventory id 1 and productId "1-PRODUCT-1"
    When The user creates a listing with the quantity being zero, inputting inventory item Id 1, quantity 0, price 20.00, moreInfo "Seller may be willing to consider near offers", closes "2022-05-23"
    Then The user will receive a bad request error

  Scenario: User can see other business's listings
    Given The business with id 1 exists
    And The business with id 1 has a listing with the inventory item ID 1, quantity 1, price 20.00, moreInfo "Seller may be willing to consider near offers", and closes "2022-05-23"
    Then Another user with email "b@b" can see that listing