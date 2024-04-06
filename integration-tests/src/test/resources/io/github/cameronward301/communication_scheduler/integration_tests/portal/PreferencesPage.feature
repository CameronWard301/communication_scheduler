# Created by Cameron at 28/02/2024
@WebPortal
@CloseBrowserAfterScenario
Feature: Preferences Page
  Tests preferences page functionality

  Scenario: Navigating to the preferences page
    Given I navigate to "/preferences"
    Then I should see the preferences page

  Scenario: Expanding advanced features, reveals advanced options
    Given I navigate to "/preferences"
    Then I should not see the advanced preference options
    When I click by id on "advanced-options-button"
    Then I should see the advanced preference options

  Scenario: Clicking helper buttons modifies the preference fields
    Given I navigate to "/preferences"
    And I click by id on "advanced-options-button"
    And preference fields are set to:
      | maximumAttempts | gatewayTimeout | backoffCoefficient | initialInterval | maximumInterval | startToCloseTimeout |
      | 1234            | 1234           | 1234               | 1234            | 1234            | 1234                |
    When I click by id on "unlimited-maximum-attempts-btn"
    Then the field with id "max-attempts-input" should be set to: "0"
    When I click by id on "disable-backoff-coefficient-btn"
    Then the field with id "backoff-coefficient-input" should be set to: "1"
    When I click by id on "no-limit-maximum-interval-btn"
    Then the field with id "maximum-interval-input" should be set to: "0"
    When I click by id on "no-limit-start-to-close-timeout-btn"
    Then the field with id "start-to-close-timeout-input" should be set to: "0"

  Scenario: Saving preferences with no changes displays warning
    Given I navigate to "/preferences"
    When I click by id on "save-preferences-btn"
    Then I should see a snackbar message with the text "You haven't made any changes to save"

  Scenario: Saving preferences with changes displays modal with changes
    Given I navigate to "/preferences"
    And I click by id on "advanced-options-button"
    And preference fields are set to:
      | maximumAttempts | gatewayTimeout | backoffCoefficient | initialInterval | maximumInterval | startToCloseTimeout |
      | 12456           | 123789         | 567890             | 90909090        | 11111111        | 987654              |
    When I click by id on "save-preferences-btn"
    Then I should see the preference confirmation modal with new values:
      | maximumAttempts | gatewayTimeout | backoffCoefficient | initialInterval  | maximumInterval  | startToCloseTimeout |
      | 12456           | 123789 Seconds | 567890             | 90909090 Seconds | 11111111 Seconds | 987654 Seconds      |
    When I click by id on "cancel-modal-button"
    And I set the time periods to:
      | gatewayTimeout | initialInterval | maximumInterval | startToCloseTimeout |
      | M              | H               | D               | M                   |
    And I click by id on "save-preferences-btn"
    Then I should see the preference confirmation modal with new values:
      | maximumAttempts | gatewayTimeout | backoffCoefficient | initialInterval | maximumInterval | startToCloseTimeout |
      | 12456           | 123789 Minutes | 567890             | 90909090 Hours  | 11111111 Days   | 987654 Minutes      |

  @GetExistingPreferences
  @RevertPreferenceChanges
  Scenario: Should save preferences to the server
    Given I navigate to "/preferences"
    And I click by id on "advanced-options-button"
    And preference fields are set to:
      | maximumAttempts | gatewayTimeout | backoffCoefficient | initialInterval | maximumInterval | startToCloseTimeout |
      | 12456           | 123789         | 567890             | 90909090        | 11111111        | 987654              |
    When I click by id on "save-preferences-btn"
    And I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Preferences updated"
    Then I close the snackbar
    When I click by id on "save-preferences-btn"
    Then I should see a snackbar message with the text "You haven't made any changes to save"
