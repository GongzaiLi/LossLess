Feature: Modify Inventory
  As a business administrator
  I can modify any of the inventory entries
  So that the inventory of the business is correct and up to date

  Scenario: Modify all inventory attributes
    Given We are logged in as the user "a@a"
    And The user "a@a" is an administrator for business 1
    And The product with id "1-PRODUCT" exists in the catalogue for business 1
    And The inventory item with id 1 exists for business 1
    When The user modifies the inventory item with id 1 for business 1 with fields product "1-PRODUCT", quantity 1, expiry date "2050-05-12", best before "2050-05-10", sell by "2050-05-12", and manufactured "2010-05-12"
    Then The inventory item is modified with the given fields

  Scenario: Modify inventory with product that does not exist
    Given We are logged in as the user "a@a"
    And The user "a@a" is an administrator for business 1
    And The inventory item with id 1 exists for business 1
    And The product with id "2-PRODUCT" does not exist
    When The user modifies the inventory item with id 1 for business 1 with fields product "2-PRODUCT", quantity 1, expiry date "2050-05-12", best before "2050-05-10", sell by "2050-05-12", and manufactured "2010-05-12"
    Then The inventory item is not modified with an error message of "Product with given id does not exist"

  Scenario: Modify inventory with product that does not exist
    Given We are logged in as the user "a@a"
    And The user "a@a" is an administrator for business 1
    And The inventory item with id 1 exists for business 1
    And The product with id "2-PRODUCT" does not exist
    When The user modifies the inventory item with id 1 for business 1 with no product code
    Then The inventory item is not modified with an error message of "{\"productId\":\"Product Code is Mandatory\"}"