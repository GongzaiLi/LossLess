Feature: Login
  As a user
  I want to log in
  So that I can access my profile and other features of the system


  Scenario: Log in with incorrect password
    Given The user with email "johnsmith@email.com" exists and has password "securepassword"
    When The user logs in with user email "johnsmith@email.com" and password "password"
    Then The user will receive an error message of "Incorrect email/password. User is unauthorized to access the system"


  Scenario: Log in with incorrect email
    Given The user with email "johnsmith@email.com" exists and has password "securepassword"
    When The user logs in with user email "janedoe@email.com" and password "securepassword"
    Then The user will receive an error message of "Incorrect email/password. User is unauthorized to access the system"


  Scenario: Log in with nonexistent account
    Given The user with email "fabian@email.com" does not exist
    When The user logs in with user email "fabian@email.com" and password "securepassword"
    Then The user will receive an error message of "You have tried to log into an account with an email fabian@email.com that is not registered."


  Scenario: Log in without an email
    When The user logs in without an email and with password "securepassword"
    Then The user will receive an error message of "Bad Request."


  Scenario: Log in without a password
    When The user logs in with user email "janedoe@email.com" and without a password
    Then The user will receive an error message of "Bad Request."

  Scenario: Log in with correct details
    Given The user with email "johnsmith@email.com" exists and has password "securepassword"
    When The user logs in with user email "johndoe@email.com" and password "securepassword"
    Then The user will be logged in as themselves
