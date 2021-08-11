Feature: U29 - Browse/Search Sale listings
  As a logged-in individual user
  I need a way to browse currently-available sales listings
  So that I can find sales I might be interested in.

  Background:
    Given We are logged in as the individual user with email "a@a"
    And The following listings exist:
      | Black Water No Sugar | 1   | Albania | Samarkand  | Ulugbek |2050-01-01|
      | Back Water           | 1.5 | Russia  | Moscow     | Kremlin |2050-01-02|
      | Willy Wonka          | 2   | Albania | Sypki      | Say     |2050-01-03|
      | Wonka Willy          | 100 | Russia  | Stalingrad | Kgb     |2050-01-04|

  Scenario: AC2 - If no filtering is applied then all current sales listings are displayed.
    When I search for listings with no filtering
    Then The results contain the following products:
      | Black Water No Sugar |
      | Back Water           |
      | Willy Wonka          |
      | Willy Wonka          |

  Scenario: AC6 - I can limit the results by typing, in a suitable field, part of a product name.
    When I search for listings by product name "water"
    Then The results contain exclusively the following products:
      | Black Water No Sugar |
      | Back Water           |

  Scenario: AC6 - I can limit the results by typing, in a suitable field, all of a product name.
    When I search for listings by product name "Back Water"
    Then The results contain exclusively the following products:
      | Back Water |

  Scenario: AC6 - No results shown when no product names match.
    When I search for listings by product name "Fab"
    Then No results are given

  Scenario: AC6 - I can limit the results by setting a price range.
    When I search for listings by min price 1.1 and max price 2
    Then The results contain exclusively the following products:
      | Back Water  |
      | Willy Wonka |

  Scenario: AC9 - I can limit the results by address, in a city name.
    When I search for listings by address "Samarkand"
    Then The results contain exclusively the following products:
      | Black Water No Sugar |

  Scenario: AC9 - I can limit the results by address, in a country name.
    When I search for listings by address "Albania"
    Then The results contain exclusively the following products:
      | Black Water No Sugar |
      | Willy Wonka          |

  Scenario: AC9 - I can limit the results by address, in a suburb name.
    When I search for listings by address "Kgb"
    Then The results contain exclusively the following products:
      | Wonka Willy |

  Scenario: AC10 - I can limit the results by setting a closing date range.
    When I search for listings by closing date between "2050-01-02" and "2050-01-03"
    Then The results contain exclusively the following products:
      | Back Water  |
      | Willy Wonka |

  Scenario: AC10 - I can limit the results by setting a latest closing date.
    When I search for listings with closing dates on or before "2050-01-03"
    Then The results contain exclusively the following products:
      | Black Water No Sugar |
      | Back Water           |
      | Willy Wonka          |

  Scenario: AC10 - I can limit the results by setting a earliest closing date .
    When I search for listings with closing dates on or after "2050-01-02"
    Then The results contain exclusively the following products:
      | Back Water           |
      | Willy Wonka          |
      | Wonka Willy          |