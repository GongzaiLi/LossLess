Feature: Modify User
  As a logged on user
  I can modify my own user details

  Background: As a User can modify their own user profile
    Given I am logged in as a user with the email "c@c"

    Scenario: AC1: As a registered individual, I can update any of my attributes
      When The User modifies his profile with firstName: "John", lastName: "Smith", date of Birth: "1999-04-27", password: "demo", newPassword: "2145", middleName: "Hector", nickname "Jonny", bio: "beach", phoneNumber: "+64 3 555 0129", email "c@c", country "New Zealand",  streetNumber "3/24",  streetName "Ilam Road",  suburb "Upper Riccarton", city "Christchurch",  region "Canterbury",  postcode "90210"
      Then The User is modified with an ok result

    Scenario: AC2: A user tries to modify his date of birth so he's under 13
      When The User modifies his profile with the date of birth: "2021-04-27"
      Then The User who is modifying will receive an error

    Scenario: AC2: A user tries to modify his password but his inputted current password is wrong
      When The User modifies his password to "newPassword"
      Then The User who is modifying will receive an error

    Scenario: AC2: A user tries to modify his email but his inputted email already exists
      Given A user exists with the email "a@a"
      When The User modifies his email to "a@a"
      Then The User who is modifying will receive an email taken error

    Scenario: AC3: A user tries to modify but is missing an email
      When The User modifies his profile with the email: ""
      Then The User who is modifying will receive an error

    Scenario: AC3: A user tries to modify but is missing a first name
      When The User modifies his profile with the firstname: ""
      Then The User who is modifying will receive an error

    Scenario: AC3: A user tries to modify but is missing a last name
      When The User modifies his profile with the lastname: ""
      Then The User who is modifying will receive an error

    Scenario: AC3: A user tries to modify but is missing a home address
      When The User modifies his profile with the home address: ""
      Then The User who is modifying will receive an error

  Scenario: AC4: A user changes their country and gets a notification
      When The User modifies his profile with the country: "New Zealand"
      Then The User who is modifying will have a notification saved

