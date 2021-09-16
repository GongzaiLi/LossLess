Feature: U700 Extended Sales Report
  So that I can make better decisions for my business in the future, as a logged-in business administrator, I need to be able to access extended data about how my listings are being purchased.
  Background:
    Given  We are logged in as the individual user with email  "a@a"
    And The user "a@a" is an administrator for business 1
    And The following product "b" exists
    And the following purchases have been made:
      |1       |1        |2050-01-01|1             |2050-01-01  |2050-03-01  |1      |1       |1      |
      |1       |1        |2050-01-02|1             |2050-01-01  |2050-03-02  |1      |2       |10     |
      |1       |1        |2050-02-01|1             |2050-01-01  |2050-02-01  |1      |3       |100    |
      |1       |1        |2050-02-02|1             |2050-01-01  |2050-02-03  |1      |4       |1000   |
      |1       |1        |2050-01-01|1             |2050-01-01  |2051-03-01  |1      |5       |10000  |

  Scenario: The Extended Sales Report will display a graph of the number of sales listings grouped by the duration between the listings’ purchase and closing dates.
    When I view the extended sales report starting "2050-01-01" and ending "2051-02-03"
    Then The counts of listings grouped by duration are:
      | 0   | 1 |
      | 1   | 1 |
      | 59  | 2 |
      | 424 | 1 |