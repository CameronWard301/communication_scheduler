@PreferencesAPI
Feature: Preference API Scenarios
  Tests the preferences API to make sure that the config map can be updated, read, and is secured with appropriate scopes

  Scenario: Should get preferences
    Given I have a bearer token with the "PREFERENCES:READ" scope
    When I get the preferences
    Then preferences are returned with a status code of 200

  Scenario: Should throw 401 when no auth token supplied
    Given I have no token
    When I get the preferences
    Then the response code is 401 and message: "401 : [no body]"

  Scenario: Should throw 403 when wrong scope is provided when getting a gateway
    Given I have a bearer token with the "PREFERENCES:WRITE" scope
    When I get the preferences
    Then the response code is 403 and message: "403 : [no body]"

  @GetExistingPreferences
  @RevertPreferenceChanges
  Scenario: Should update retry policy
    Given I have a retry policy with the following data:
      | maximumAttempts | backoffCoefficient | initialInterval | maximumInterval | startToCloseTimeout |
      | 100             | 4.0                | PT2S            | PT200S          | PT20S               |
    And I have a bearer token with the "PREFERENCES:WRITE" scope
    When I update the retryPolicy
    Then the updated retryPolicy is returned with status code of 200

  @GetExistingPreferences
  @RevertPreferenceChanges
  Scenario: Should produce a bad request when trying to update the retry policy with a missing field
    Given I have a retry policy with the following data:
      | maximumAttempts | backoffCoefficient | initialInterval | startToCloseTimeout |
      | 100             | 4.0                | PT2S            | PT20S               |
    And I have a bearer token with the "PREFERENCES:WRITE" scope
    When I update the retryPolicy
    Then the response code is 400 and message: "400 : \"'maximumInterval' cannot be empty\""

  @GetExistingPreferences
  @RevertPreferenceChanges
  Scenario: Should produce a 401 when no auth token is supplied when trying to update the retry-policy
    Given I have a retry policy with the following data:
      | maximumAttempts | backoffCoefficient | initialInterval | startToCloseTimeout |
      | 100             | 4.0                | PT2S            | PT20S               |
    And I have no token
    When I update the retryPolicy
    Then the response code is 401 and message: "401 : [no body]"

  @GetExistingPreferences
  @RevertPreferenceChanges
  Scenario: Should produce a 403 when auth token with the wrong scope is used when trying to update the retry-policy
    Given I have a retry policy with the following data:
      | maximumAttempts | backoffCoefficient | initialInterval | startToCloseTimeout |
      | 100             | 4.0                | PT2S            | PT20S               |
    And I have a bearer token with the "PREFERENCES:READ" scope
    When I update the retryPolicy
    Then the response code is 403 and message: "403 : [no body]"


  @GetExistingPreferences
  @RevertPreferenceChanges
  Scenario: Should update gateway timeout
    Given I have a gateway timeout with value: 120
    And I have a bearer token with the "PREFERENCES:WRITE" scope
    When I update the gatewayTimeout preference
    Then the updated gatewayTimeout is returned with status code of 200

  @GetExistingPreferences
  @RevertPreferenceChanges
  Scenario: Should produce 400 bad request when sending a request to update the gateway timeout with no value set
    Given I have a bearer token with the "PREFERENCES:WRITE" scope
    And I have a gateway timeout with value: null
    When I update the gatewayTimeout preference
    Then the response code is 400 and message: "400 : \"'gatewayTimeoutSeconds' cannot be null\""

  @GetExistingPreferences
  @RevertPreferenceChanges
  Scenario: Should produce a 401 when no auth token is supplied when trying to update the gateway-timeout
    Given I have a gateway timeout with value: 120
    And I have no token
    When I update the gatewayTimeout preference
    Then the response code is 401 and message: "401 : [no body]"

  @GetExistingPreferences
  @RevertPreferenceChanges
  Scenario: Should produce a 403 when wrong scope is supplied when trying to update the gateway-timeout
    Given I have a gateway timeout with value: 120
    And I have a bearer token with the "PREFERENCES:READ" scope
    When I update the gatewayTimeout preference
    Then the response code is 403 and message: "403 : [no body]"
