Feature: Modify Business
  As a logged in user
  and an administrator of a business
  I can modify that business' details

  Background: As an administrator of a business I can modify its details
    Given A user is logged on with the email "d@d"
    And The user is an administrator for a business with name "Wonka Water"

  Scenario: AC1: As an administrator of the business account, I can modify any of the attributes.
    When The User modifies his business with name: "Wonka Chocolate", description: "I like chocolates", business type: "Charitable organisation", address with the details, country "New Zealand",  streetNumber "3/24",  streetName "Ilam Road",  suburb "Upper Riccarton", city "Christchurch",  region "Canterbury",  postcode "90210"
    Then The business is modified with an ok result

  Scenario: AC1: If a user is not an administrator of the business account, they cannot modify the business.
    Given A user is logged on with the email "e@e"
    When The User modifies the business who he is not an admin for with name: "Wonka Chocolate"
    Then The User who is modifying will receive a forbidden error

  Scenario: AC2: If a user modifies a business with invalid requests, they cannot modify the business.
    When The User modifies his business with an invalid business type: "Supermarket"
    Then The User who is modifying the business will receive an error




