Feature: U16 - Product Images

  Background: As a business administrator, I need to be able to associate images with them.
    Given We are logged in as the user "a@a", and The user is an administrator for business 1
    And A product with id: "1-Back-Water", name: "Back Water" exists in the catalogue for business 1

  Scenario: AC1 - Upload an image for the product with a wrong type
    Given  There are 0 images of the product with id: "1-Back-Water"
    When Upload an image with a name: "wrongType.txt" in the product with id: "1-Back-Water"
    Then The user will be able to see having 0 images from the product.
    And The user will get an error message "Invalid Image type"

  Scenario: AC1,2,3 - Upload an image and a thumbnail for the product with a correct type
    Given There are 0 images of the product with id: "1-Back-Water"
    When Upload an image with a name: "1.png" in the product with id: "1-Back-Water"
    Then The user will be able to see having 1 image with the id 1 from the product.
    And The current primary image is this product's image id: 1

  Scenario: AC1,2,3 - Upload another 4 images and thumbnails of different correct types of the product
    Given There is 1 image of the product with id: "1-Back-Water"
    When Upload four images with name: "2.png", "3.jpeg", "4.jpg", "5.gif" in the product with id: "1-Back-Water"
    Then The user will be able to see having 5 images with the id 2, 3, 4, 5 from the product.
    And The current primary image is this product's image id: 1

  Scenario: AC1 - Upload another 1 image for the product when the product has already uploaded 5 images
    Given There is 5 image of the product with id: "1-Back-Water"
    When Upload an image with a name: "6.png" in the product with id: "1-Back-Water"
    Then The user will be able to see having 5 images from the product.
    And The user will get an error message "Cannot upload product image, limit reached for this product."

  Scenario: AC2 - Change the primary image in the product
    Given There are 5 images of the product with id: "1-Back-Water"
    And The product with id: "1-Back-Water" and the current primary image with id: 1
    When Set the image with id: 5 is a new primary image of the product with id: "1-Back-Water"
    Then The current primary image is this product's image id: 5

  Scenario: AC4 - Delete a product's image
    Given There are 5 images of the product with id: "1-Back-Water"
    When Delete an image with id: 1 in the product with id: "1-Back-Water"
    And The user will be able to see having 4 images from the product.
    Then The current primary image is this product's image id: 5

  Scenario: AC4 - Delete a product's primary image
    Given There are 4 images of the product with id: "1-Back-Water", and the current primary image with id: 5
    When Delete an image with id: 5 in the product with id: "1-Back-Water"
    And The user will be able to see having 3 images from the product.
    Then The current primary image is this product's image id: 2

  Scenario: AC4 - Delete all product's image
    Given There are 3 images of the product with id: "1-Back-Water", and the current primary image with id: 2
    When Delete all images in the product with id: "1-Back-Water"
    And The user will be able to see having 0 images from the product.
    Then The current primary image is this product's image will be empty