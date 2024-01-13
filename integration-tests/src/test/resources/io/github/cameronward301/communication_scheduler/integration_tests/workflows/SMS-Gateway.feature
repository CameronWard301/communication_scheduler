Feature: Communication Workflow
  Integration testing for sending communications through the SMS gateway

  Scenario: Send a successful SMS Communication
    Given I am user: "SmsUser1"
    And Using gateway "Sms"

    When A CommunicationWorkflow is started

    Then Workflow status is WORKFLOW_EXECUTION_STATUS_COMPLETED
    And Communication response is ok

  Scenario: Send an SMS Communication with unknown user id
    Given I am user: "Unknown"
    And Using gateway "Sms"
    And Workflow timeout is 5 seconds

    When A CommunicationWorkflow is started

    Then Workflow status is WORKFLOW_EXECUTION_STATUS_FAILED
    And Communication response is Status: 500
