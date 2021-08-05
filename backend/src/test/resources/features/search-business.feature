Feature: U23 Search for business

  Background: As a User can search any businesses
    Given A user is logged in as the user "a@a"
    And There are 20 businesses in the database with names business 1 through business 20


  Scenario: AC2: I can enter a partial name as a search term and paginate to return 5 results on page 2 and sort by name asc
    When The User searches businesses with partial name: "business-" searchQuery with default pagination and sort by "name" sort direction "asc"
    Then The 5 matching businesses are returned in ascending name order

  Scenario: AC2: I can enter a partial name as a search term and paginate to return 5 results on page 2 and sort by name desc
    When The User searches businesses with partial name: "business-" searchQuery with default pagination and sort by "name" sort direction "desc"
    Then The 5 matching businesses are returned in descending name order

  Scenario: AC2: I can enter an empty as a search term.
    When The User searches businesses with an empty: "" searchQuery with default pagination and sorting
    Then The first 20 matching businesses are returned

  Scenario: AC2: I can enter a full name as a search term.
    When The User searches businesses with full name: "business-11" searchQuery with default pagination and sorting
    Then The one matching business with name "business-11" is returned

  Scenario: AC2: I can enter a partial name as a search term.
    When The User searches businesses with partial name: "business-" searchQuery with default pagination and sorting
    Then The first 20 matching businesses are returned

  Scenario: AC2: I can enter a partial name as a search term and paginate to return 5 results
    When The User searches businesses with partial name: "business-" searchQuery with size of 5 and default page and sorting
    Then The first 5 matching businesses are returned

  Scenario: AC2: I can enter a partial name as a search term and paginate to return 5 results on page 2
    When The User searches businesses with partial name: "business-" searchQuery with size of 5 and page 2 and default sorting
    Then The second 5 matching businesses are returned

