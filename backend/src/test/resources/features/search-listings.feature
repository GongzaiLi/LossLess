Feature: U29 - Browse/Search Sale listings
  As a logged-in individual user
  I need a way to browse currently-available sales listings
  So that I can find sales I might be interested in.

  Background:
    Given We are logged in as the individual user with email "a@a"
    And The following listings exist:
      | Black Water No Sugar |
      | Back Water           |
      | Willy Wonka          |

  Scenario: AC2 - If no filtering is applied then all current sales listings are displayed.
    When I search for listings with no filtering
    Then The results contain the following products:
      | Black Water No Sugar |
      | Back Water           |
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
