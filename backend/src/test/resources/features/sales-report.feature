Feature: U41 Sales report
  So that I can manage my business effectively, as a logged-in business admin,
  I need to be able to see how much has been sold in a period of time.

  Background:
    Given  We are logged in as the individual user with email  "a@a"
    And The user "a@a" is an administrator for business 1
    And The following product "b" exists
    And the following purchases have been made:
    |1       |1        |2050-01-01|1             |2050-01-01  |2050-01-01  |1      |1       |1      |
    |1       |1        |2050-01-02|1             |2050-01-01  |2050-01-01  |1      |2       |10    |
    |1       |1        |2050-02-01|1             |2050-01-01  |2050-01-01  |1      |3       |100    |
    |1       |1        |2050-02-03|1             |2050-01-01  |2050-01-01  |1      |4       |1000   |
    |1       |1        |2051-01-01|1             |2050-01-01  |2050-01-01  |1      |5       |10000  |

  Scenario:AC3: I can also specify a custom period by selecting when it starts and ends.
    When I search for a sales report starting "2050-02-01" and ending "2050-02-03" with period "day"
    Then 3 items are returned

  Scenario: AC4: I can select the granularity of the report. By default,
  I will just see the total number and total value of all purchases made during the period,
  together with the details of the period, and any other relevant detail (e.g. the business name)
    When I search for a sales report starting "2050-01-01" and ending "2050-12-30" with no period specified
    Then I get a response with:
      |2050-01-01|2050-12-30|4|1111|

  Scenario: AC5: I can also select finer granularity (e.g. monthly).
  In this case the report would have a line for each month, including the month name/number
  and the correspondent total number and total value for that month.
    When I search for a sales report starting "2050-01-01" and ending "2050-02-03" with period "month"
    Then The following is returned:
      |2050-01-01|2050-01-31|2             |11        |
      |2050-02-01|2050-02-03|2             |1100      |