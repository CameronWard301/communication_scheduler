# Created by Cameron at 09/01/2024
Feature: # Enter feature name here
  Integration testing for sending communications through the Email gateway

  Scenario: Send a successful Email Communication
    Given I am user: "EmailUser1"
    And Using gateway "Email"

    When A CommunicationWorkflow is started

    Then Workflow status is WORKFLOW_EXECUTION_STATUS_COMPLETED
    And Communication response is ok
