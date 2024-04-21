# Created by cameron at 16/02/2024
@ScheduleAPI
Feature: Schedule API Scenarios
  Tests the schedule API to make sure schedules can be queried, created, deleted and updated

#
#  #########################
#  # GET ALL SCHEDULES
#  #########################
#
  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: Get all schedules with no filters
    Given I have a bearer token with the "SCHEDULE:READ" scope
    When I get all schedules
    Then I receive a page of schedules with at least 3 items with status code 200

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: Get all schedules with no filters adjusting page size
    Given I set the pageSize to be "1"
    And I set the pageNumber to be "0"
    Given I have a bearer token with the "SCHEDULE:READ" scope
    When I get all schedules
    Then I receive a page of schedules with 1 items with status code 200

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: Get all schedules with userId filter
    Given I set the gatewayId filter to be: "gateway1"
    And I set the userId filter to be: "my-test-user-id-1-integration-test"
    And I have a bearer token with the "SCHEDULE:READ" scope
    When I get all schedules
    Then I receive a page of schedules with 1 items with status code 200

  Scenario: User gets all schedules with wrong scope
    Given I have a bearer token with the "SCHEDULE:WRITE" scope
    When I get all schedules
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User gets all schedules with no auth token
    Given I have no token
    When I get all schedules
    Then the response code is 401 and message: "401 : [no body]"

  #########################
  # GET SCHEDULE
  #########################

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User gets a schedule by id
    Given I have a bearer token with the "SCHEDULE:READ" scope
    When I get the schedule by id
    Then the schedule is returned with a status code of 200

  Scenario: User gets a schedule by unknown id
    Given I have a bearer token with the "SCHEDULE:READ" scope
    When I get the schedule by an unknown id: "unknown-id"
    Then the response code is 404 and message: "404 : \"Could not find Schedule with Id: unknown-id\""

  Scenario: User gets a schedule with wrong scope
    Given I have a bearer token with the "SCHEDULE:WRITE" scope
    When I get the schedule by id
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User gets a schedule with no auth token
    Given I have no token
    When I get the schedule by id
    Then the response code is 401 and message: "401 : [no body]"

#  #########################
#  # CREATE SCHEDULE
#  #########################

  Scenario: User creates a new schedule with interval
    Given I have a schedule with the following details:
      | gatewayId  | userId  | paused | interval   |
      | my gateway | my user | false  | 10 seconds |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I create the schedule
    Then the new or updated schedule is returned with status code of 201
    And the test framework removes the schedule

  Scenario: User creates a new schedule with calendar
    Given I have a schedule with the following details:
      | gatewayId  | userId  | paused | calendar   |
      | my gateway | my user | false  | every year |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I create the schedule
    Then the new or updated schedule is returned with status code of 201
    And the test framework removes the schedule

  Scenario: User creates a new schedule with cron
    Given I have a schedule with the following details:
      | gatewayId  | userId  | paused | cron        |
      | my gateway | my user | false  | * * * * MON |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I create the schedule
    Then the new or updated schedule is returned with status code of 201
    And the test framework removes the schedule

  Scenario: User creates a new schedule with invalid cron
    Given I have a schedule with the following details:
      | gatewayId  | userId  | paused | cron  |
      | my gateway | my user | false  | hello |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I create the schedule
    Then the response code is 400 and message: "400 : \"INVALID_ARGUMENT: Invalid schedule spec: CronString does not have 5-7 fields\""

  Scenario: User creates a new schedule with interval without gatewayId
    Given I have a schedule with the following details:
      | userId  | paused | interval   |
      | my user | false  | 10 seconds |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I create the schedule
    Then the response code is 400 and message: "400 : \"'gatewayId' cannot be empty\""

  Scenario: User creates a new schedule with interval without userId
    Given I have a schedule with the following details:
      | gatewayId  | paused | interval   |
      | my gateway | false  | 10 seconds |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I create the schedule
    Then the response code is 400 and message: "400 : \"'userId' cannot be empty\""

  Scenario: User creates a new schedule with no schedule specification
    Given I have a schedule with the following details:
      | gatewayId  | userId  | paused |
      | my gateway | my user | false  |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I create the schedule
    Then the response code is 400 and message: "400 : \"Please provide exactly one schedule configuration, either: 'calendar', 'interval' or 'cronExpression'\""

  Scenario: User creates a schedule with wrong scope
    Given I have a schedule with the following details:
      | gatewayId  | userId  | paused |
      | my gateway | my user | false  |
    And I have a bearer token with the "SCHEDULE:READ" scope
    When I create the schedule
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User creates a schedule with no auth token
    Given I have no token
    When I create the schedule
    Then the response code is 401 and message: "401 : [no body]"

#  #########################
#  # UPDATE SCHEDULE
#  #########################
#
  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User updates an existing schedule with calendar and pauses
    Given I have a schedule with the following details:
      | gatewayId  | userId  | paused | calendar   |
      | my gateway | my user | true   | every year |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I update the schedule
    Then the new or updated schedule is returned with status code of 200

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User updates an existing schedule with cron string
    Given I have a schedule with the following details:
      | gatewayId  | userId  | paused | cron         |
      | my gateway | my user | true   | * * 10 * MON |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I update the schedule
    Then the new or updated schedule is returned with status code of 200

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User updates a schedule with invalid cron
    Given I have a schedule with the following details:
      | gatewayId  | userId  | paused | cron  |
      | my gateway | my user | false  | hello |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I update the schedule
    Then the response code is 400 and message: "400 : \"INVALID_ARGUMENT: Invalid schedule spec: CronString does not have 5-7 fields\""

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User updates a schedule with interval without gatewayId
    Given I have a schedule with the following details:
      | userId  | paused | interval   |
      | my user | false  | 10 seconds |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I update the schedule
    Then the new or updated schedule is returned with status code of 200

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User updates a schedule with interval without userId
    Given I have a schedule with the following details:
      | gatewayId  | paused | interval   |
      | my gateway | false  | 10 seconds |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I update the schedule
    Then the new or updated schedule is returned with status code of 200

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User updates a new schedule with no schedule specification
    Given I have a schedule with the following details:
      | gatewayId  | userId  | paused |
      | my gateway | my user | false  |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I update the schedule
    Then the new or updated schedule is returned with status code of 200

  Scenario: User updates an existing schedule without providing a schedule id
    Given I have a schedule with the following details:
      | scheduleId | gatewayId  | userId  | paused | cron         |
      | null       | my gateway | my user | true   | * * 10 * MON |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I update the schedule
    Then the response code is 400 and message: "400 : \"Please provide a 'scheduleId' in the request body to update a schedule\""

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User updates an existing schedule with an unknown id
    Given I have a schedule with the following details:
      | scheduleId | gatewayId  | userId  | paused | cron         |
      | 123        | my gateway | my user | true   | * * 10 * MON |
    And I have a bearer token with the "SCHEDULE:WRITE" scope
    When I update the schedule
    Then the response code is 404 and message: "404 : \"Could not find Schedule with Id: integration-test-123\""

  Scenario: User updates a schedule with wrong scope
    Given I have a schedule with the following details:
      | scheduleId | gatewayId  | userId  | paused | cron         |
      | test-123   | my gateway | my user | true   | * * 10 * MON |
    And I have a bearer token with the "SCHEDULE:READ" scope
    When I update the schedule
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User updates a schedule with no auth token
    Given I have no token
    When I update the schedule
    Then the response code is 401 and message: "401 : [no body]"
#
#  #########################
#  # BATCH UPDATE SCHEDULE
#  #########################
#
  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: User pauses multiple schedules matching the userId filter
    Given I have a bearer token with the "SCHEDULE:WRITE" scope
    And I set the userId filter to be: "my-test-user-id-1-integration-test"
    And I have the following patch DTO:
      | paused |
      | true   |
    When I batch update existing schedules
    Then a modified DTO is returned with status 200 and message "Completed" and totalModified is 1

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: User updates multiple schedules matching the gateway filter
    Given I have a bearer token with the "SCHEDULE:WRITE" scope
    And I set the gatewayId filter to be: "gateway3"
    And I have the following patch DTO:
      | gatewayId           |
      | test-gateway-update |
    When I batch update existing schedules
    Then a modified DTO is returned with status 200 and message "Completed" and totalModified is 1

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: User updates multiple schedules matching the gateway and user filter
    Given I have a bearer token with the "SCHEDULE:WRITE" scope
    And I set the gatewayId filter to be: "gateway1"
    And I set the userId filter to be: "my-test-user-id-1-integration-test"
    And I have the following patch DTO:
      | gatewayId           | paused |
      | test-gateway-update | true   |
    When I batch update existing schedules
    Then a modified DTO is returned with status 200 and message "Completed" and totalModified is 1

  Scenario: User updates multiple schedules without specifying a filter should throw error
    Given I have a bearer token with the "SCHEDULE:WRITE" scope
    And I have the following patch DTO:
      | gatewayId           | paused |
      | test-gateway-update | true   |
    When I batch update existing schedules
    Then the response code is 400 and message: "400 : \"Must supply at least one of 'userId' or 'gatewayId' as a query parameter\""

  Scenario: User updates a schedule with wrong scope
    Given I have a bearer token with the "SCHEDULE:READ" scope
    And I have the following patch DTO:
      | gatewayId           | paused |
      | test-gateway-update | true   |
    When I batch update existing schedules
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User updates a schedule with no auth token
    Given I have no token
    When I batch update existing schedules
    Then the response code is 401 and message: "401 : [no body]"


#  #########################
#  # BATCH DELETE SCHEDULES
#  #########################

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: Delete all schedules matching the gatewayId
    Given I set the gatewayId filter to be: "gateway3"
    And I have a bearer token with the "SCHEDULE:DELETE" scope
    When I batch delete schedules
    Then a modified DTO is returned with status 200 and message "Successfully Deleted" and totalModified is 1

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: Delete all schedules matching the userId
    Given I set the userId filter to be: "my-test-user-id-2-integration-test"
    And I have a bearer token with the "SCHEDULE:DELETE" scope
    When I batch delete schedules
    Then a modified DTO is returned with status 200 and message "Successfully Deleted" and totalModified is 2

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: Delete all schedules matching the userId and gatewayId
    Given I set the gatewayId filter to be: "user1"
    And I set the gatewayId filter to be: "gateway1"
    And I have a bearer token with the "SCHEDULE:DELETE" scope
    When I batch delete schedules
    Then a modified DTO is returned with status 200 and message "Successfully Deleted" and totalModified is 1

  Scenario: User deletes multiple schedules without specifying a filter should throw error
    Given I have a bearer token with the "SCHEDULE:DELETE" scope
    When I batch delete schedules
    Then the response code is 400 and message: "400 : \"Must provide at least one of 'gatewayId' or 'userId' filters\""

  Scenario: User deletes all schedules with wrong scope
    Given I have a bearer token with the "SCHEDULE:WRITE" scope
    When I batch delete schedules
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User deletes all schedules with no auth token
    Given I have no token
    When I batch delete schedules
    Then the response code is 401 and message: "401 : [no body]"


#  #########################
#  # GET SCHEDULE COUNT
#  #########################
  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: Get the number of schedules matching the userId and gatewayId
    Given I set the gatewayId filter to be: "user1"
    And I set the gatewayId filter to be: "gateway1"
    And I have a bearer token with the "SCHEDULE:READ" scope
    When I get the schedule count
    Then a CountDTO is returned with total at least: 1 and status code 200

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: Get the number of schedules matching an empty filter
    Given I have a bearer token with the "SCHEDULE:READ" scope
    When I get the schedule count
    Then a CountDTO is returned with total at least: 3 and status code 200

  Scenario: User gets total schedules with wrong scope
    Given I have a bearer token with the "SCHEDULE:WRITE" scope
    When I batch delete schedules
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User gets total schedules with no auth token
    Given I have no token
    When I batch delete schedules
    Then the response code is 401 and message: "401 : [no body]"
#
#  #########################
#  # DELETE BY ID
#  #########################
  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  Scenario: User deletes an existing schedule by id
    Given I have a bearer token with the "SCHEDULE:DELETE" scope
    When I delete the schedule by id
    Then the schedule is deleted with response code 204

  Scenario: User deletes an existing schedule by an unknown id
    Given I have a bearer token with the "SCHEDULE:DELETE" scope
    When I delete the schedule by an unknown id: "573df1e-0b99-4bea-a38b-a8defdf0c6f0"
    Then the response code is 404 and message: "404 : \"Could not find Schedule with Id: 573df1e-0b99-4bea-a38b-a8defdf0c6f0\""

  Scenario: User gets total schedules with wrong scope
    Given I have a bearer token with the "SCHEDULE:WRITE" scope
    When I delete the schedule by an unknown id: "573df1e-0b99-4bea-a38b-a8defdf0c6f0"
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User gets total schedules with no auth token
    Given I have no token
    When I delete the schedule by an unknown id: "573df1e-0b99-4bea-a38b-a8defdf0c6f0"
    Then the response code is 401 and message: "401 : [no body]"
