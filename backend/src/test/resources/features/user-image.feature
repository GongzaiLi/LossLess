Feature: U10 - User Image

  Background: As a logged in user, I need to be able to upload a profile image for myself
    Given A user exists and is logged in as the user "a@a"

  Scenario: AC5 - Upload an image for the user with a wrong type
    Given There is no image for the user
    When Upload an image with a name: "wrongType.txt"
    Then The user will get the error message "Invalid Image type"

  Scenario: AC5 - Upload an image for the user with a correct type
    Given There is no image for the user
    When Upload an image with a name: "1.png"
    Then The user will be able to their image

  Scenario: AC5 - A thumbnail of the primary image is created automatically
    Given There is no image for the user
    When Upload an image with a name: "1.png"
    Then The thumbnail of the user image is created

  Scenario: AC5 - Upload an image for a user when they already have an image
    Given There is an image for the user
    When Upload an image with a name: "2.png"
    Then The user image is updated to the new one


