Feature: Register
  As a non-existing user
  I want to create an account
  So that I can login to to my profile and use the application


  Scenario: Register user with no first name
    Given User is not registered and is on the register page
    When User tries to create an account with no first name, last name "Smith", email "johnsmith99@gmail.com", date of birth "1999-04-27", country "New Zealand",  streetNumber "3/24",  streetName "Ilam Road",  city "Christchurch",  region "Canterbury",  postcode "90210" and password "securepassword"
    Then The user will receive an error message of "firstName is mandatory"

  Scenario: Register user with no last name
    Given User is not registered and is on the register page
    When User tries to create an account with first name "John", no last name, email "johnsmith99@gmail.com", date of birth "1999-04-27", country "New Zealand",  streetNumber "3/24",  streetName "Ilam Road",  city "Christchurch",  region "Canterbury",  postcode "90210" and password "securepassword"
    Then The user will receive an error message of "lastname is mandatory"

  Scenario: Register user with no email address
    Given User is not registered and is on the register page
    When User tries to create an account with first name "John", last name "Smith", email "johnsmith99@gmail.com", date of birth "1999-04-27", country "New Zealand",  streetNumber "3/24",  streetName "Ilam Road",  city "Christchurch",  region "Canterbury",  postcode "90210" and password "securepassword"
    Then The user will receive an error message of "email is mandatory"

  Scenario: Register user with no date of birth
    Given User is not registered and is on the register page
    When User tries to create an account with first name "John", last name "Smith", email "johnsmith99@gmail.com", date of birth "", home address "{"country": "New Zealand",  "streetNumber": "3/24",  "streetName": "Ilam Road",  "city": "Christchurch",  "region": "Canterbury",  "postcode": "90210"}" and password "securepassword"
    Then The user will receive an error message of "dateOfBirth is mandatory"

  Scenario: Register user with no home address
    Given User is not registered and is on the register page
    When User tries to create an account with first name "John", last name "Smith", email "johnsmith99@gmail.com", date of birth "1999-04-27", home address "" and password "securepassword"
    Then The user will receive an error message of "streetNumber is mandatory"

        #Tests for all address parts

  Scenario: Register user with no password
    Given User is not registered and is on the register page
    When User tries to create an account with first name "John", last name "", email "johnsmith99@gmail.com", date of birth "1999-04-27", home address "{"country": "New Zealand",  "streetNumber": "3/24",  "streetName": "Ilam Road",  "city": "Christchurch",  "region": "Canterbury",  "postcode": "90210"}" and password ""
    Then The user will receive an error message of "password is mandatory"


  Scenario: Register with invalid email format
    Given User is not registered and is on the register page
    When User tries to create an account with first name "John", last name "Smith", email "johnsmith99gmail.com", date of birth "1999-04-27", home address "{"country": "New Zealand",  "streetNumber": "3/24",  "streetName": "Ilam Road",  "city": "Christchurch",  "region": "Canterbury",  "postcode": "90210"}" and password "securepassword"
    Then The user will receive an error message of "Email address is invalid"


  Scenario: Register with email that is already registered
    Given The user with email "fabian@email.com" has an account and user is on register page
    When User tries to create an account with first name "fabian", last name "g", email "fabian@email.com", date of birth "1999-04-27", home address "{"country": "New Zealand",  "streetNumber": "3/24",  "streetName": "Ilam Road",  "city": "Christchurch",  "region": "Canterbury",  "postcode": "90210"}" and password "securepassword"
    Then The user will receive an error message of "Attempted to create user with already used email"


  Scenario: Register a user that is below 16 years of age
    Given User is not registered and is on the register page
    When User tries to create an account with first name "", last name "Smith", email "johnsmith99@gmail.com", date of birth "2015-04-27", home address "{"country": "New Zealand",  "streetNumber": "3/24",  "streetName": "Ilam Road",  "city": "Christchurch",  "region": "Canterbury",  "postcode": "90210"}" and password "securepassword"
    Then The user will receive an error message of "Date out of expected range"



