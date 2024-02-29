@HistoryAPI
Feature: History API Scenarios
  Tests the history API to make sure previous communications can be fetched, queried and running workflows can be stopped

#
#  #########################
#  # GET ALL WORKFLOWS
#  #########################
#
  @TerminateExistingWorkflows
  @CreateTestWorkflows
  @RemoveTestWorkflows
  Scenario: Should get workflows by no filter
    Given I have a bearer token with the "HISTORY:READ" scope
    When I get all workflows
    Then the I receive a page of history workflows with a size greater than or equal to 10 and status code 200

  @TerminateExistingWorkflows
  @CreateTestWorkflows
  @RemoveTestWorkflows
  Scenario: Should get workflows by filter
    Given I have a bearer token with the "HISTORY:READ" scope
    And I set the history pageSize to be 1
    And I set the history pageNumber to be 0
    And I set the history userId filter to be "test-user-id"
    And I set the history gatewayId filter to be "test-gateway-id"
    And I set the history scheduleId filter to be "test-schedule-id"
    And I set the status filter to be "RUNNING"
    When I get all workflows
    Then the I receive a page of history workflows with a size of 1 and status code 200
    And the total number of workflows matching the filter is 10

  @TerminateExistingWorkflows
  @CreateTestWorkflows
  @RemoveTestWorkflows
  Scenario: Should get workflows by filter and have 0 results on the third page
    Given I have a bearer token with the "HISTORY:READ" scope
    And I set the history pageSize to be 5
    And I set the history pageNumber to be 2
    And I set the history userId filter to be "test-user-id"
    And I set the history gatewayId filter to be "test-gateway-id"
    And I set the history scheduleId filter to be "test-schedule-id"
    And I set the status filter to be "RUNNING"
    When I get all workflows
    Then the I receive a page of history workflows with a size of 0 and status code 200
    And the total number of workflows matching the filter is 10

  @TerminateExistingWorkflows
  @CreateTestWorkflows
  @RemoveTestWorkflows
  Scenario: Should get workflows by invalid filter produces an empty page
    Given I have a bearer token with the "HISTORY:READ" scope
    And I set the history pageSize to be 100
    And I set the history userId filter to be "test-user-id-invalid"
    And I set the history gatewayId filter to be "test-gateway-id"
    And I set the history scheduleId filter to be "test-schedule-id"
    And I set the status filter to be "RUNNING"
    When I get all workflows
    Then the I receive a page of history workflows with a size of 0 and status code 200

  Scenario: User gets all history with wrong scope
    Given I have a bearer token with the "HISTORY:TERMINATE" scope
    When I get all workflows
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User gets all history with no auth token
    Given I have no token
    When I get all workflows
    Then the response code is 401 and message: "401 : [no body]"


#
#  #########################
#  # GET WORKFLOW BY ID
#  #########################
#
  @TerminateExistingWorkflows
  @CreateTestWorkflow
  @RemoveTestWorkflows
  Scenario: Should get workflow by id
    Given I have a bearer token with the "HISTORY:READ" scope
    And I set the workflow and run id to be from the created workflow
    When I get get the workflow by id
    Then the I receive a history workflow with status code 200

  Scenario: Should get 404 if workflow does not exist
    Given I have a bearer token with the "HISTORY:READ" scope
    And I set the workflow and run id to be "invalid-id"
    When I get get the workflow by id
    Then the response code is 404 and message: "404 : \"Could not find workflow with id invalid-id and runId invalid-id\""

  Scenario: User gets workflow by id with wrong scope
    Given I have a bearer token with the "HISTORY:TERMINATE" scope
    And I set the workflow and run id to be "invalid-id"
    When I get get the workflow by id
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User gets workflow by id with no auth token
    Given I have no token
    And I set the workflow and run id to be "invalid-id"
    When I get get the workflow by id
    Then the response code is 401 and message: "401 : [no body]"

#
#  #########################
#  # TERMINATE WORKFLOW BY ID
#  #########################
#
  @TerminateExistingWorkflows
  @CreateTestWorkflow
  Scenario: Should terminate workflow by id
    Given I have a bearer token with the "WORKFLOW:TERMINATE" scope
    And I set the workflow and run id to be from the created workflow
    When I terminate the workflow by id
    Then the workflow is deleted with response code 204

  @TerminateExistingWorkflows
  @CreateTestWorkflow
  Scenario: Should get 404 if workflow is already terminated
    Given I have a bearer token with the "WORKFLOW:TERMINATE" scope
    And I set the workflow and run id to be from the created workflow
    When I terminate the workflow by id
    Then the workflow is deleted with response code 204
    When I terminate the workflow by id
    Then the response code is 404 and message is contains the workflow id and run id

  Scenario: User terminates workflow by id with wrong scope
    Given I have a bearer token with the "HISTORY:READ" scope
    And I set the workflow and run id to be from the created workflow
    When I terminate the workflow by id
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User terminates workflow by id with no auth token
    Given I have no token
    And I set the workflow and run id to be from the created workflow
    When I terminate the workflow by id
    Then the response code is 401 and message: "401 : [no body]"

#
#  #########################
#  # GET WORKFLOW TOTAL BY FILTER
#  #########################
#
  @TerminateExistingWorkflows
  @CreateTestWorkflows
  @RemoveTestWorkflows
  Scenario: Should get total workflows by filter
    Given I have a bearer token with the "HISTORY:READ" scope
    And I set the history pageSize to be 1
    And I set the history userId filter to be "test-user-id"
    And I set the history gatewayId filter to be "test-gateway-id"
    And I set the history scheduleId filter to be "test-schedule-id"
    And I set the status filter to be "RUNNING"
    When I get total workflows
    Then the I receive a total number of 10 and status code 200

  @TerminateExistingWorkflows
  @CreateTestWorkflows
  @RemoveTestWorkflows
  Scenario: Should get total workflows by filter with no results
    Given I have a bearer token with the "HISTORY:READ" scope
    And I set the history pageSize to be 1
    And I set the history userId filter to be "test-user-id-invalid"
    And I set the history gatewayId filter to be "test-gateway-id-invalid"
    And I set the history scheduleId filter to be "test-schedule-id-invalid"
    And I set the status filter to be "RUNNING"
    When I get total workflows
    Then the I receive a total number of 0 and status code 200

  Scenario: User gets history total with wrong scope
    Given I have a bearer token with the "HISTORY:TERMINATE" scope
    When I get all workflows
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User gets history total with no auth token
    Given I have no token
    When I get total workflows
    Then the response code is 401 and message: "401 : [no body]"
