Feature: Register
  As a non-existing user
  I want to create an account
  So that I can login to to my profile and use the application


  Scenario: Register user with no first name
    Given User is not registered and is on the register page
    When User tries to create an account with no first name, last name "Smith", email "johnsmith99@gmail.com", date of birth "1999-04-27", country "New Zealand",  streetNumber "3/24",  streetName "Ilam Road",  city "Christchurch",  region "Canterbury",  postcode "90210" and password "securepassword"
    Then The registering user will receive an error message of "{\"firstName\":\"firstName is mandatory\"}"

  Scenario: Register user with no last name
    Given User is not registered and is on the register page
    When User tries to create an account with first name "John", no last name, email "johnsmith99@gmail.com", date of birth "1999-04-27", country "New Zealand",  streetNumber "3/24",  streetName "Ilam Road",  city "Christchurch",  region "Canterbury",  postcode "90210" and password "securepassword"
    Then The registering user will receive an error message of "{\"lastName\":\"lastName is mandatory\"}"

  Scenario: Register user with no email address
    Given User is not registered and is on the register page
    When User tries to create an account with first name "John", last name "Smith", no email, date of birth "1999-04-27", country "New Zealand",  streetNumber "3/24",  streetName "Ilam Road",  city "Christchurch",  region "Canterbury",  postcode "90210" and password "securepassword"
    Then The registering user will receive an error message of "{\"email\":\"email is mandatory\"}"

  Scenario: Register user with no date of birth
    Given User is not registered and is on the register page
    When User tries to create an account with first name "John", last name "Smith", email "johnsmith99@gmail.com", no date of birth, country "New Zealand",  streetNumber "3/24",  streetName "Ilam Road",  city "Christchurch",  region "Canterbury",  postcode "90210" and password "securepassword"
    Then The registering user will receive an error message of "{\"dateOfBirth\":\"dateOfBirth is mandatory\"}"

  Scenario: Register user with no home address
    Given User is not registered and is on the register page
    When User tries to create an account with first name "John", last name "Smith", email "johnsmith99@gmail.com", date of birth "1999-04-27", no home address and password "securepassword"
    Then The registering user will receive an error message of "{\"homeAddress\":\"homeAddress is mandatory\"}"


        #Tests for all address parts

  Scenario: Register user with no password
    Given User is not registered and is on the register page
    When User tries to create an account with first name "John", last name "Smith", email "johnsmith@99gmail.com", date of birth "1998-04-27", country "New Zealand",  streetNumber "3/24",  streetName "Ilam Road",  city "Christchurch",  region "Canterbury",  postcode "90210" and password ""
    Then The registering user will receive an error message of "{\"password\":\"password is mandatory\"}"


  Scenario: Register a user that is below 13 years of age
    Given User is not registered and is on the register page
    When User tries to create an account with first name "John", last name "Smith", email "johnsmith999@gmail.com", date of birth "2020-04-27", country "New Zealand",  streetNumber "3/24",  streetName "Ilam Road",  city "Christchurch",  region "Canterbury",  postcode "90210" and password "securepassword"
    Then The registering user will receive an error message of "Date out of expected range"

  Scenario: Register with invalid email format
    Given User is not registered and is on the register page
    When User tries to create an account with first name "John", last name "Smith", email "johnsmith99gmail.com", date of birth "1998-04-27", country "New Zealand",  streetNumber "3/24",  streetName "Ilam Road",  city "Christchurch",  region "Canterbury",  postcode "90210" and password "securepassword"
    Then The registering user will receive an error message of "{\"email\":\"must be a well-formed email address\"}"


  Scenario: Register with email that is already registered
    Given The user with email "fabian@email.com" has an account and user is on register page
    When User tries to create an account with first name "John", last name "Smith", email "fabian@email.com", date of birth "1998-04-27", country "New Zealand",  streetNumber "3/24",  streetName "Ilam Road",  city "Christchurch",  region "Canterbury",  postcode "90210" and password "securepassword"
    Then The registering user will receive an error message of "Attempted to create user with already used email"




