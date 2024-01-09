# Created by Cameron at 09/01/2024
Feature: Invalid gateway integration tests
  Test the workflow to handle gateways that don't exist in the database

  Scenario: Starting a workflow with an invalid gateway id
    Given I am user: "Unknown"
    And Using gateway "Unknown"
    And Workflow timeout is 5 seconds

    When A CommunicationWorkflow is started

    Then Workflow status is WORKFLOW_EXECUTION_STATUS_FAILED
    And Application failure message contains: "Gateway with id unknown not found"
