Feature: Communication Workflow
  Integration testing for sending communications through the SMS gateway

  Scenario: Send a successful SMS Communication
    Given I am user: "SmsUser1"
    And Using gateway "Sms"

    When A CommunicationWorkflow is started

    Then Workflow status is WORKFLOW_EXECUTION_STATUS_COMPLETED
    And Communication response is ok
