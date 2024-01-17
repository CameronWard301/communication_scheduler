# Created by Cameron at 09/01/2024
Feature: Email Gateway Integration Tests
  Integration testing for sending communications through the Email gateway

  Scenario: Send a successful Email Communication
    Given I am user: "EmailUser1"
    And Using gateway "Email"

    When A CommunicationWorkflow is started

    Then Workflow status is WORKFLOW_EXECUTION_STATUS_COMPLETED
    And Communication response is ok

  Scenario: Send an Email Communication with unknown user id
    Given I am user: "Unknown"
    And Using gateway "Email"
    And Workflow timeout is 5 seconds

    When A CommunicationWorkflow is started

    Then Workflow status is WORKFLOW_EXECUTION_STATUS_FAILED
    And Communication response is Status: 500
