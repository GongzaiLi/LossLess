Feature: Create Inventory
  As a business administrator
  I need to be able to manage the inventory of my business
  So that I can manage my stocks of products

  Scenario: Access Inventory of My Business
    Given We are logged in as the user "a@a"
    And The user "a@a" is an administrator for business 1
    When The user accesses the inventory for business 1
    Then The user will be able to see the inventory

  Scenario: Access Inventory of Other User's Business
    Given We are logged in as the user "b@b"
    And The user "b@b" is not an administrator for business 1
    When The user accesses the inventory for business 1
    Then The user cannot see the inventory

  Scenario: Add inventory entry
    Given We are logged in as the user "a@a"
    And The user "a@a" is an administrator for business 1
    And The product with id "1-PRODUCT" exists in the catalogue for business 1
    When The user creates an inventory entry for business 1 with product "1-PRODUCT", quantity 1, and expiry date "2050-05-12"
    Then The inventory entry is created

  Scenario: Add inventory entry with zero quantity
    Given We are logged in as the user "a@a"
    And The user "a@a" is an administrator for business 1
    And The product with id "1-PRODUCT" exists in the catalogue for business 1
    When The user creates an inventory entry for business 1 with product "1-PRODUCT", quantity 0, and expiry date "2050-05-12"
    Then The inventory entry is not created

  Scenario: Add inventory entry with expiry in the past
    Given We are logged in as the user "a@a"
    And The user "a@a" is an administrator for business 1
    And The product with id "1-PRODUCT" exists in the catalogue for business 1
    When The user creates an inventory entry for business 1 with product "1-PRODUCT", quantity 1, and expiry date "2000-05-12"
    Then The inventory entry is not created