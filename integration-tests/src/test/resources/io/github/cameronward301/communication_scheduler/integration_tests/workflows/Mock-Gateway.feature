@MockGateway
Feature: Mock Gateway Integration Tests
  Integration testing for sending communications through the Mock gateway

  Scenario: Send a successful Mock Communication
    Given I am user: "Unknown"
    And Using gateway "Mock"

    When A CommunicationWorkflow is started

    Then Workflow status is WORKFLOW_EXECUTION_STATUS_COMPLETED
    And Communication response is ok
