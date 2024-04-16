# Created by Cameron at 10/04/2024
@WebPortal
@CloseBrowserAfterScenario
Feature: History Pages
  Tests the history pages of the application


  Scenario: Should load history page:
    When I navigate to "/history"
    Then the element with id "history-page-heading" should be set to: "Communication History"

  @TerminateExistingWorkflows
  @MongoDbSetupEntity
  @CreateTestWorkflow
  @MongoDbRemoveEntity
  @RemoveTestWorkflows
  Scenario: Should load correct history item
    Given I navigate to "/history"
    When I set the status filter to "running-checkbox"
    And I click by id on "gateway-filter-button"
    And I set the "gateway-id-search" to be the gateway id
    And I click the first gateway in the data grid filter results
    And I click by id on "gateway-filter-apply-button"
    And I set the userId filter to be: "test-user-id-integration-test"
    And I set the "user-id-filter-input" to be the user id
    And I press enter on the field with id "user-id-filter-input"
    And I set the "schedule-id-filter-input" to be the schedule id
    And I press enter on the field with id "schedule-id-filter-input"
    When I click by id on "refresh-history-button"
    Then the total history results should be "1–1 of 1"
    And the correct history item should be displayed

  @TerminateExistingWorkflows
  @MongoDbSetupEntity
  @CreateTestWorkflow
  @MongoDbRemoveEntity
  @RemoveTestWorkflows
  Scenario: Inspecting the history item is correct
    Given I navigate to the history page with filters correctly set
    When I click view on the history item
    Then the history item should be displayed correctly with running status "Running"

  @TerminateExistingWorkflows
  @MongoDbSetupEntity
  @CreateTestWorkflow
  @MongoDbRemoveEntity
  @RemoveTestWorkflows
  Scenario: Should be able to stop the workflow from the history list
    Given I navigate to the history page with filters correctly set
    When I click stop on the history item
    Then I see the confirm stop modal
    When I click by id on "stop-com-confirm-modal-button"
    Then the status cell of the first item should be set to "Terminated"
    And the endTime cell should be set to a valid date

  @TerminateExistingWorkflows
  @MongoDbSetupEntity
  @CreateTestWorkflow
  @MongoDbRemoveEntity
  @RemoveTestWorkflows
  Scenario: Should be able to stop the workflow from the inspect modal
    Given I navigate to the history page with filters correctly set
    And I click view on the history item
    When I click by id on "stop-inspect-communication"
    Then I see the confirm stop modal
    When I click by id on "stop-com-confirm-modal-button"
    Then the history item should be displayed correctly with running status "Terminated"
    When I click by id on "cancel-modal-button"
    Then the status cell of the first item should be set to "Terminated"

  @TerminateExistingWorkflows
  @CreateTestWorkflows
  @RemoveTestWorkflows
  Scenario: Should load history items
    When I navigate to "/history?userId=user-integration-test-1&status=Running"
    Then the total history results should be "1–10 of 10"
    And the status cell of the first item should be set to "Running"

  @TerminateExistingWorkflows
  @MongoDbSetupEntity
  @CreateTestWorkflow
  @MongoDbRemoveEntity
  @RemoveTestWorkflows
  Scenario: Should navigate to the correct page when viewing the communication's gateway
    Given I navigate to the history page with filters correctly set
    And I click view on the history item
    When I click by id on "view-gateway-button"
    Then the url should contain the gateway id filter

  @TerminateExistingWorkflows
  @MongoDbSetupEntity
  @CreateTestWorkflow
  @MongoDbRemoveEntity
  @RemoveTestWorkflows
  Scenario: Should navigate to the correct page when viewing the communication's schedule
    Given I navigate to the history page with filters correctly set
    And I click view on the history item
    When I click by id on "view-schedule-button"
    Then the url should contain the schedule id filter
