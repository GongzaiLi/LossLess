Feature: U11 - Business Profile Image

  Background: As a logged in user, I need to be able to upload a profile image for a business I am admin of
    Given A user exists and is logged in as the user "a@a" and is an admin of a business called "Wonka"

  Scenario: AC4 - Upload an image for the business with a wrong type
    Given There is no image for the business
    When Upload an image to the business with a name: "wrongType.txt"
    Then The user will get an error message: "Invalid Image type"

  Scenario: AC4 - Upload an image for the business with a correct type
    Given There is no image for the business
    When Upload an image to the business with a name: "1.png"
    Then The business will be have their image saved

  Scenario: AC4 - Upload an image for a business when they already have an image
    Given There is an image for the business
    When Upload an image to the business with a name: "2.png"
    Then The business image is updated to the new one

  Scenario: AC6 - A thumbnail of the primary image is created automatically
    Given There is no image for the business
    When Upload an image to the business with a name: "1.png"
    Then The thumbnail of the business image is created