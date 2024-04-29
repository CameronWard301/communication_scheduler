# Created by Cameron at 02/04/2024
@WebPortal
@CloseBrowserAfterScenario
Feature: Schedule Web Portal Page
  Allows searching, modifying, removing, and adding schedules for the web portal.

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @MongoDbSetupEntity
  @RemoveSchedule
  @MongoDbRemoveEntity
  Scenario: User should be able to filter schedules by ID
    Given I navigate to "/schedules"
    When I set the "schedule-id-filter-input" to be the schedule id
    And I press enter on the field with id "schedule-id-filter-input"
    And I click by id on "refresh-schedules"
    Then I should see the schedule
    And the total schedule results should be 1

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @MongoDbSetupEntity
  @RemoveSchedule
  @MongoDbRemoveEntity
  Scenario: User should be able to filter schedules by gateway
    Given I navigate to "/schedules"
    When I click by id on "gateway-filter-button"
    And I set the "gateway-id-search" to be the gateway id
    And I click by id on "gateway-filter-search"
    And I click the first gateway in the data grid filter results and refresh with: "gateway-filter-search"
    And I click by id on "gateway-filter-apply-button"
    Then I should see the schedule

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @MongoDbSetupEntity
  @RemoveSchedule
  @MongoDbRemoveEntity
  Scenario: User should be able to filter schedules by user ID
    Given I navigate to "/schedules"
    When I set the "user-id-filter-input" to be the user id
    And I press enter on the field with id "user-id-filter-input"
    Then I should see the schedule

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should be able to reset all filters
    Given I navigate to "/schedules"
    And I set the "schedule-id-filter-input" to be the schedule id
    And I click by id on "gateway-filter-button"
    And I set the "gateway-id-search" to be the gateway id
    And I click by id on "gateway-filter-search"
    And I click the first gateway in the data grid filter results and refresh with: "gateway-filter-search"
    And I click by id on "gateway-filter-apply-button"
    And I set the "user-id-filter-input" to be the user id
    And I press enter on the field with id "user-id-filter-input"
    When I click by id on "reset-filters-button"
    Then the field with id "schedule-id-filter-input" should be set to: ""
    And the field with id "user-id-filter-input" should be set to: ""
    When I click by id on "gateway-filter-button"
    Then the button with id "gateway-filter-apply-button" should be disabled

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  @RemoveSchedule
  Scenario: User should be able to pause a schedule from the table
    Given I navigate to "/schedules"
    And I set the "schedule-id-filter-input" to be the schedule id
    And I press enter on the field with id "schedule-id-filter-input"
    When I "pause" the schedule by id
    Then I should see the confirm schedule modal
    And the element with id "transition-modal-title" should be set to: "Are you sure you want to pause this schedule?"
    When I click by id on "confirm-modal-button"
    Then I should see the schedule status as "Paused"
    And I should see a snackbar message with the text "Schedule Paused"


  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  @RemoveSchedule
  Scenario: User should be able to resume a schedule from the table
    Given I navigate to "/schedules"
    And I set the "schedule-id-filter-input" to be the schedule id
    And I press enter on the field with id "schedule-id-filter-input"
    When I "pause" the schedule by id
    Then I should see the confirm schedule modal
    When I click by id on "confirm-modal-button"
    When I "resume" the schedule by id
    Then I should see the confirm schedule modal
    And the element with id "transition-modal-title" should be set to: "Are you sure you want to run this schedule?"
    When I click by id on "confirm-modal-button"
    Then I should see the schedule status as "Running"
    And I should see a snackbar message with the text "Schedule Running"


  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should be able to delete a schedule from the table
    Given I navigate to "/schedules"
    And I set the "schedule-id-filter-input" to be the schedule id
    And I press enter on the field with id "schedule-id-filter-input"
    When I "delete" the schedule by id
    Then I should see the confirm schedule modal
    And the element with id "transition-modal-title" should be set to: "Are you sure you want to delete this schedule?"
    When I click by id on "confirm-modal-button"
    And I should see a snackbar message with the text "Schedule Deleted"
    When I click by id on "refresh-schedules"
    Then the total schedule results should be 0

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  @RemoveSchedule
  Scenario: User should be able to get to the modify page from the table and go back
    Given I navigate to "/schedules"
    And I set the "schedule-id-filter-input" to be the schedule id
    And I press enter on the field with id "schedule-id-filter-input"
    When I "modify" the schedule by id
    Then The URI is set to edit the schedule
    And I can see the edit schedule page
    When I click by id on "go-back"
    And I click by id on "reset-filters-button"
    Then The URI is now "/schedules"

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  @RemoveScheduleByUserId
  Scenario: User should be able to create a new schedule with interval
    Given I navigate to "/schedules"
    When I click by id on "create-new-schedule"
    Then the element with id "add-schedule-user-id-title" should be set to: "User ID*"
    Then the button with id "back-step" should be disabled
    Then the button with id "next-step" should be disabled
    And I set the "user-id-input" field to be "my-test-user-id-integration-test"
    And I save the user ID for the created schedule as "my-test-user-id-integration-test"
    When I click by id on "next-step"
    Then the element with id "select-gateway-title" should be set to: "Select Gateway*"
    Then the button with id "next-step" should be disabled
    When I search for the existing gateway
    And I click by id on "refresh-gateways-button"
    And I click the first gateway in the data grid filter results and refresh with: "refresh-gateways-button"
    When I click by id on "next-step"
    Then the element with id "create-schedule-recurring-interval-title" should be set to: "Recurring Interval"
    Then the button with id "next-step" should be disabled
    When I set the "days-interval-input" field to be "1"
    And I set the "hours-interval-input" field to be "1"
    And I set the "minutes-interval-input" field to be "1"
    And I set the "seconds-interval-input" field to be "1"
    Then the button with id "next-step" should not be disabled
    When I click by id on "offset-time"
    And I click by id on "offset-time-days-item"
    And I set the "offset-interval-input" field to be "2"
    Then the button with id "next-step" should be disabled
    And I set the "offset-interval-input" field to be "1"
    When I click by id on "next-step"
    Then the button with id "next-step" should be disabled
    Then the element with id "review-title" should be set to: "Review Details:"
    And the element with id "user-id" should be set to: "User ID: my-test-user-id-integration-test"
    And I should see the existing gateway details
    And I should see the list with id "upcoming-communication-list" with 7 results
    When I click by id on "create-schedule-button"
    Then I should see a snackbar message with the text "Schedule Created"
    And the field with id "schedule-id-filter-input" is not: ""
    And the total schedule results should be 1

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  @RemoveScheduleByUserId
  Scenario: User should be able to create a schedule by days of the week
    Given I navigate to "/schedules"
    When I click by id on "create-new-schedule"
    And I set the "user-id-input" field to be "my-test-user-id-integration-test"
    And I save the user ID for the created schedule as "my-test-user-id-integration-test"
    When I click by id on "next-step"
    When I search for the existing gateway
    And I click by id on "refresh-gateways-button"
    And I click the first gateway in the data grid filter results and refresh with: "refresh-gateways-button"
    When I click by id on "next-step"
    And I click by id on "calendar-week"
    Then the element with id "calendar-week-title" should be set to: "Recurring Day(s)"
    When I click by id on "schedule-type-Monday"
    Then the button with id "next-step" should not be disabled
    When I press DEL by name on "schedule-time"
    Then the button with id "next-step" should be disabled
    When I send the keys "1220P" to the field with name "schedule-time"
    Then the button with id "next-step" should not be disabled
    When I click by id on "next-step"
    Then the button with id "next-step" should be disabled
    Then the element with id "review-title" should be set to: "Review Details:"
    And the element with id "user-id" should be set to: "User ID: my-test-user-id-integration-test"
    And I should see the existing gateway details
    And I should see the list with id "upcoming-communication-list" with 7 results
    When I click by id on "create-schedule-button"
    Then I should see a snackbar message with the text "Schedule Created"
    And the field with id "schedule-id-filter-input" is not: ""
    And the total schedule results should be 1

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  @RemoveScheduleByUserId
  Scenario: User should be able to create a schedule by day of the month
    Given I navigate to "/schedules"
    When I click by id on "create-new-schedule"
    And I set the "user-id-input" field to be "my-test-user-id-integration-test"
    And I save the user ID for the created schedule as "my-test-user-id-integration-test"
    When I click by id on "next-step"
    When I search for the existing gateway
    And I click by id on "refresh-gateways-button"
    And I click the first gateway in the data grid filter results and refresh with: "refresh-gateways-button"
    When I click by id on "next-step"
    And I click by id on "calendar-month"
    Then the element with id "calendar-month-title" should be set to: "Recurring Month(s)*"
    When I set the "day-of-the-month" field to be "0"
    Then the button with id "next-step" should be disabled
    When I set the "day-of-the-month" field to be "31"
    And I click by id on "schedule-type-April"
    Then the button with id "next-step" should be disabled
    When I set the "day-of-the-month" field to be "2"
    Then the button with id "next-step" should not be disabled
    When I press DEL by name on "schedule-time"
    Then the button with id "next-step" should be disabled
    When I send the keys "1220P" to the field with name "schedule-time"
    Then the button with id "next-step" should not be disabled
    When I click by id on "next-step"
    Then the button with id "next-step" should be disabled
    Then the element with id "review-title" should be set to: "Review Details:"
    And the element with id "user-id" should be set to: "User ID: my-test-user-id-integration-test"
    And I should see the existing gateway details
    And I should see the list with id "upcoming-communication-list" with 7 results
    When I click by id on "create-schedule-button"
    Then I should see a snackbar message with the text "Schedule Created"
    And the field with id "schedule-id-filter-input" is not: ""
    And the total schedule results should be 1

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  @RemoveScheduleByUserId
  Scenario: User should be able to create a schedule by cron string
    Given I navigate to "/schedules"
    When I click by id on "create-new-schedule"
    And I set the "user-id-input" field to be "my-test-user-id-integration-test"
    And I save the user ID for the created schedule as "my-test-user-id-integration-test"
    When I click by id on "next-step"
    When I search for the existing gateway
    And I click by id on "refresh-gateways-button"
    And I click the first gateway in the data grid filter results and refresh with: "refresh-gateways-button"
    When I click by id on "next-step"
    And I click by id on "cron-string"
    Then the element with id "cron-string-title" should be set to: "Cron String (Advanced)"
    And the button with id "next-step" should not be disabled
    When I set the "cron-string-input" field to be ""
    Then the button with id "next-step" should be disabled
    When I set the "cron-string-input" field to be "0 8-10 * * *"
    Then the button with id "next-step" should not be disabled
    When I click by id on "next-step"
    Then the button with id "next-step" should be disabled
    Then the element with id "review-title" should be set to: "Review Details:"
    And the element with id "user-id" should be set to: "User ID: my-test-user-id-integration-test"
    And I should see the existing gateway details
    And I should see the list with id "upcoming-communication-list" with 7 results
    When I click by id on "create-schedule-button"
    Then I should see a snackbar message with the text "Schedule Created"
    And the field with id "schedule-id-filter-input" is not: ""
    And the total schedule results should be 1

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  @RemoveSchedule
  Scenario: User can navigate to the edit schedule page
    When I navigate to the edit schedule page
    Then I can see the edit schedule page

  Scenario: User navigates to the edit schedule page with an invalid schedule id
    When I navigate to "/schedule/--test--"
    And I wait 1 second
    Then I should see a snackbar message with the text "Schedule not found with id: --test--"

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User that doesn't make any edits can't save the schedule
    Given I navigate to the edit schedule page
    When I click by id on "confirm-edit-button"
    Then I should see a snackbar message with the text "No changes to save, please edit a field and try again"

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User can edit the user id of a schedule
    Given I navigate to the edit schedule page
    And I set the "user-id-input" field to be "my-test-user-id"
    When I click by id on "confirm-edit-button"
    Then the element with id "user-id-new-value" should be set to: "my-test-user-id"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Schedule Updated"
    And the field with id "user-id-input" should be set to: "my-test-user-id"

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @MongoDbSetupEntity
  @MongoDbAddMultipleEntities
  @MongoDbRemoveEntity
  @MongoDbRemoveMultipleEntities
  @RemoveSchedule
  Scenario: User can change the gateway of a schedule
    Given I navigate to the edit schedule page
    And I click by id on "change-gateway-button"
    And I search for the new gateway
    And I click by id on "refresh-gateways-button"
    And I click the first gateway in the data grid filter results and refresh with: "refresh-gateways-button"
    When I click by id on "confirm-modal-button"
    Then I should see the new gateway details for gateway "1" with id prefix "new-"
    When I click by id on "confirm-edit-button"
    Then I should see the old and new gateway confirm modal for gateway "1"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Schedule Updated"
    And I should see the new gateway details for gateway "1" with id prefix ""

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User can change the schedule to a new interval
    Given I navigate to the edit schedule page
    And I click by id on "change-schedule"
    And I set the "days-interval-input" field to be "2"
    And I set the "hours-interval-input" field to be "2"
    And I set the "minutes-interval-input" field to be "2"
    And I set the "seconds-interval-input" field to be "2"
    And I click by id on "offset-time"
    And I click by id on "offset-time-days-item"
    And I set the "offset-interval-input" field to be "1"
    And I click by id on "confirm-modal-button"
    And I should see the list with id "current-schedule" with 7 results
    And I should see the list with id "upcoming-communication-list" with 7 results
    When I click by id on "confirm-edit-button"
    Then I should see the list with id "schedule-old-value" with 7 results
    And I should see the list with id "schedule-new-value" with 7 results
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Schedule Updated"

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User can change the schedule to a day of the week
    Given I navigate to the edit schedule page
    And I click by id on "change-schedule"
    And I click by id on "calendar-week"
    And I click by id on "schedule-type-Tuesday"
    And I press DEL by name on "schedule-time"
    And I send the keys "1220P" to the field with name "schedule-time"
    And I click by id on "confirm-modal-button"
    And I should see the list with id "current-schedule" with 7 results
    And I should see the list with id "upcoming-communication-list" with 7 results
    When I click by id on "confirm-edit-button"
    Then I should see the list with id "schedule-old-value" with 7 results
    And I should see the list with id "schedule-new-value" with 7 results
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Schedule Updated"

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User can change the schedule to a day of the month
    Given I navigate to the edit schedule page
    And I click by id on "change-schedule"
    And I click by id on "calendar-month"
    And I set the "day-of-the-month" field to be "2"
    And I click by id on "schedule-type-April"
    And I press DEL by name on "schedule-time"
    And I send the keys "1220P" to the field with name "schedule-time"
    And I click by id on "confirm-modal-button"
    And I should see the list with id "current-schedule" with 7 results
    And I should see the list with id "upcoming-communication-list" with 7 results
    When I click by id on "confirm-edit-button"
    Then I should see the list with id "schedule-old-value" with 7 results
    And I should see the list with id "schedule-new-value" with 7 results
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Schedule Updated"

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User can change the schedule to a cron schedule
    Given I navigate to the edit schedule page
    And I click by id on "change-schedule"
    And I click by id on "cron-string"
    And I set the "cron-string-input" field to be "0 8-10 * * *"
    And I click by id on "confirm-modal-button"
    And I should see the list with id "current-schedule" with 7 results
    And I should see the list with id "upcoming-communication-list" with 7 results
    When I click by id on "confirm-edit-button"
    Then I should see the list with id "schedule-old-value" with 7 results
    And I should see the list with id "schedule-new-value" with 7 results
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Schedule Updated"

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User can't confirm the change gateway modal without selecting a gateway
    Given I navigate to the edit schedule page
    And I click by id on "change-gateway-button"
    And the button with id "confirm-modal-button" should be disabled

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  @RemoveSchedule
  Scenario: The user can pause a schedule from the edit page
    Given I navigate to the edit schedule page
    And I click by id on "pause-schedule-button"
    And I should see the confirm schedule modal
    And the element with id "transition-modal-title" should be set to: "Are you sure you want to pause this schedule?"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Schedule Paused"
    And the element with id "status" should be set to: "Status: Paused"

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  @RemoveSchedule
  Scenario: The user can resume a schedule from the edit page
    Given I navigate to the edit schedule page
    And I click by id on "pause-schedule-button"
    And I click by id on "confirm-modal-button"
    And I should see a snackbar message with the text "Schedule Paused"
    Then I close the snackbar
    When I click by id on "run-schedule-button"
    Then I should see the confirm schedule modal
    And the element with id "transition-modal-title" should be set to: "Are you sure you want to run this schedule?"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Schedule Running"
    And the element with id "status" should be set to: "Status: Running"

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: The user can delete a schedule from the edit page
    Given I navigate to the edit schedule page
    And I click by id on "delete-schedule-button"
    And I should see the confirm schedule modal
    And the element with id "transition-modal-title" should be set to: "Are you sure you want to delete this schedule?"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Schedule Deleted"
    And The URI is now "/schedules"
    When I set the "schedule-id-filter-input" to be the schedule id
    And I press enter on the field with id "schedule-id-filter-input"
    Then the total schedule results should be 0

  @RemoveExistingSchedules
  @CreateScheduleWithInterval
  @RemoveSchedule
  Scenario: User can only click on the bulk action if they have set a filter
    Given I navigate to "/schedules"
    And the button with id "bulk-actions" should be disabled
    When I set the "user-id-filter-input" to be the user id
    And I press enter on the field with id "user-id-filter-input"
    And the total schedule results should be "1–1 of 1" after clicking by id on "refresh-schedules"
    Then the button with id "bulk-actions" should be disabled
    When I press the select all button in the data grid
    Then the button with id "bulk-actions" should not be disabled
    And I click by id on "refresh-schedules"
    When I press the select all button in the data grid
    Then the button with id "bulk-actions" should be disabled
    When I click the first schedule in the data grid filter results with the user ID
    Then the button with id "bulk-actions" should not be disabled
    When I click by id on "reset-selection-button"
    Then the button with id "bulk-actions" should be disabled

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: User can bulk delete all selected schedules
    Given I navigate to "/schedules?userId=my-test-user-id-2-integration-test"
    When I press the select all button in the data grid
    And I click by id on "bulk-actions"
    Then The URI is now "/schedule/actions"
    And the button with id "next-step" should be disabled
    When I click by id on "delete-schedules-card"
    And I click by id on "next-step"
    Then the element with id "bulk-action-review-type" should be set to: "Delete"
    And the element with id "selected-schedule-number" should be set to: "2" after clicking by id on "refresh-selection"
    And the total schedule results should be "1–2 of 2"
    When I click by id on "apply-bulk-action-button"
    Then the element with id "bulk-action-type" should be set to: "Delete"
    And the element with id "total-schedules-affected" should be set to: "2"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Batch update operation success. Affected 2 schedules"
    And the total schedule results should be 0 after clicking by id on "refresh-schedules"

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @CheckSchedulesAreCreated
  @RemoveMultipleSchedules
  Scenario: User can bulk delete selected schedules
    Given I navigate to "/schedules?userId=my-test-user-id-2-integration-test"
    When I click the first schedule in the data grid filter results with user ID "my-test-user-id-2-integration-test"
    And the element with css class "MuiDataGrid-selectedRowCount" should be set to: "1 row selected"
    And I click by id on "bulk-actions"
    Then The URI is now "/schedule/actions"
    And the button with id "next-step" should be disabled
    When I click by id on "delete-schedules-card"
    And I click by id on "next-step"
    Then the element with id "bulk-action-review-type" should be set to: "Delete"
    And the element with id "selected-schedule-number" should be set to: "1" after clicking by id on "refresh-selection"
    And the total schedule results should be 1
    When I click by id on "apply-bulk-action-button"
    Then the element with id "bulk-action-type" should be set to: "Delete"
    And the element with id "total-schedules-affected" should be set to: "1"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Batch update operation success. Affected 1 schedules"
    And the total schedule results should be 1 after clicking by id on "refresh-schedules"

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @CheckSchedulesAreCreated
  @RemoveMultipleSchedules
  Scenario: User can bulk pause all selected schedules
    Given I navigate to "/schedules?userId=my-test-user-id-2-integration-test"
    When I press the select all button in the data grid
    And I click by id on "bulk-actions"
    Then The URI is now "/schedule/actions"
    And the button with id "next-step" should be disabled
    When I click by id on "pause-schedules-card"
    And I click by id on "next-step"
    Then the element with id "bulk-action-review-type" should be set to: "Pause"
    And the element with id "selected-schedule-number" should be set to: "2" after clicking by id on "refresh-selection"
    And the total schedule results should be "1–2 of 2"
    When I click by id on "apply-bulk-action-button"
    Then the element with id "bulk-action-type" should be set to: "Pause"
    And the element with id "total-schedules-affected" should be set to: "2"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Batch update operation success. Affected 2 schedules"
    And the total schedule results should be "1–2 of 2" after clicking by id on "refresh-schedules"
    And the selected schedules now have a status of "Paused"

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: User can bulk pause selected schedules
    Given I navigate to "/schedules?userId=my-test-user-id-2-integration-test"
    When I click the first schedule in the data grid filter results with user ID "my-test-user-id-2-integration-test"
    And the element with css class "MuiDataGrid-selectedRowCount" should be set to: "1 row selected"
    And I click by id on "bulk-actions"
    Then The URI is now "/schedule/actions"
    And the button with id "next-step" should be disabled
    When I click by id on "pause-schedules-card"
    And I click by id on "next-step"
    Then the element with id "bulk-action-review-type" should be set to: "Pause"
    And the element with id "selected-schedule-number" should be set to: "1" after clicking by id on "refresh-selection"
    And the total schedule results should be 1
    When I click by id on "apply-bulk-action-button"
    Then the element with id "bulk-action-type" should be set to: "Pause"
    And the element with id "total-schedules-affected" should be set to: "1"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Batch update operation success. Affected 1 schedules"
    And the total schedule results should be "1–2 of 2" after clicking by id on "refresh-schedules"
    And the first schedule has a status of "Paused"
    And the last schedule has a status of "Running"

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: User can bulk run all selected schedules
    Given I navigate to "/schedules?userId=my-test-user-id-2-integration-test"
    When I press the select all button in the data grid
    And I click by id on "bulk-actions"
    Then The URI is now "/schedule/actions"
    And the button with id "next-step" should be disabled
    When I click by id on "resume-schedules-card"
    And I click by id on "next-step"
    Then the element with id "bulk-action-review-type" should be set to: "Resume"
    And the element with id "selected-schedule-number" should be set to: "2" after clicking by id on "refresh-selection"
    And the total schedule results should be "1–2 of 2"
    When I click by id on "apply-bulk-action-button"
    Then the element with id "bulk-action-type" should be set to: "Resume"
    And the element with id "total-schedules-affected" should be set to: "2"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Batch update operation success. Affected 2 schedules"
    And the total schedule results should be "1–2 of 2" after clicking by id on "refresh-schedules"
    And the selected schedules now have a status of "Running"

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @RemoveMultipleSchedules
  Scenario: User can bulk run selected schedules
    Given I navigate to "/schedules?userId=my-test-user-id-2-integration-test"
    When I click the first schedule in the data grid filter results with user ID "my-test-user-id-2-integration-test"
    And the element with css class "MuiDataGrid-selectedRowCount" should be set to: "1 row selected"
    And I click by id on "bulk-actions"
    Then The URI is now "/schedule/actions"
    And the button with id "next-step" should be disabled
    When I click by id on "resume-schedules-card"
    And I click by id on "next-step"
    Then the element with id "bulk-action-review-type" should be set to: "Resume"
    And the element with id "selected-schedule-number" should be set to: "1" after clicking by id on "refresh-selection"
    And the total schedule results should be 1
    When I click by id on "apply-bulk-action-button"
    Then the element with id "bulk-action-type" should be set to: "Resume"
    And the element with id "total-schedules-affected" should be set to: "1"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Batch update operation success. Affected 1 schedules"
    And the total schedule results should be "1–2 of 2" after clicking by id on "refresh-schedules"
    And the first schedule has a status of "Running"
    And the last schedule has a status of "Running"

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @MongoDbSetupEntity
  @MongoDbAddMultipleEntities
  @MongoDbRemoveEntity
  @MongoDbRemoveMultipleEntities
  @RemoveMultipleSchedules
  Scenario: User can bulk update all selected schedule's gateway
    Given I navigate to "/schedules?userId=my-test-user-id-2-integration-test"
    And the total schedule results should be "2–2 of 2" after clicking by id on "refresh-schedules"
    When I press the select all button in the data grid
    And the element with css class "MuiDataGrid-selectedRowCount" should be: "2 rows selected"
    And I click by id on "bulk-actions"
    Then The URI is now "/schedule/actions"
    And the button with id "next-step" should be disabled
    When I click by id on "gateway-card"
    And I click by id on "next-step"
    Then the element with id "select-gateway-title" should be set to: "Select Gateway*"
    And the button with id "next-step" should be disabled
    When I set the "gateway-id-filter-input" to be the gateway id
    And I press enter on the field with id "gateway-id-filter-input"
    When I click the first gateway in the data grid filter results and refresh with: "refresh-gateways-button"
    Then the button with id "next-step" should not be disabled
    When I click by id on "next-step"
    Then the element with id "bulk-action-review-type" should be set to: "Update Gateway"
    And the element with id "selected-schedule-number" should be set to: "2" after clicking by id on "refresh-selection"
    And the total schedule results should be "1–2 of 2"
    And I should see the existing gateway details
    When I click by id on "apply-bulk-action-button"
    Then the element with id "bulk-action-type" should be set to: "Update Gateway"
    And the element with id "total-schedules-affected" should be set to: "2"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Batch update operation success. Affected 2 schedules"
    And the total schedule results should be "1–2 of 2" after clicking by id on "refresh-schedules"

  @RemoveExistingSchedules
  @CreateMultipleSchedules
  @MongoDbSetupEntity
  @MongoDbAddMultipleEntities
  @MongoDbRemoveEntity
  @MongoDbRemoveMultipleEntities
  @RemoveMultipleSchedules
  Scenario: User can bulk update selected schedule's gateway
    Given I navigate to "/schedules?userId=my-test-user-id-2-integration-test"
    And the total schedule results should be "2–2 of 2" after clicking by id on "refresh-schedules"
    When I click the first schedule in the data grid filter results with user ID "my-test-user-id-2-integration-test"
    And the element with css class "MuiDataGrid-selectedRowCount" should be set to: "1 row selected"
    And I click by id on "bulk-actions"
    Then The URI is now "/schedule/actions"
    And the button with id "next-step" should be disabled
    When I click by id on "gateway-card"
    And I click by id on "next-step"
    Then the element with id "select-gateway-title" should be set to: "Select Gateway*"
    And the button with id "next-step" should be disabled
    When I set the "gateway-id-filter-input" to be the gateway id
    And I press enter on the field with id "gateway-id-filter-input"
    When I click the first gateway in the data grid filter results and refresh with: "refresh-gateways-button"
    Then the button with id "next-step" should not be disabled
    When I click by id on "next-step"
    Then the element with id "bulk-action-review-type" should be set to: "Update Gateway"
    And the element with id "selected-schedule-number" should be set to: "1" after clicking by id on "refresh-selection"
    And the total schedule results should be 1
    And I should see the existing gateway details
    When I click by id on "apply-bulk-action-button"
    Then the element with id "bulk-action-type" should be set to: "Update Gateway"
    And the element with id "total-schedules-affected" should be set to: "1"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Batch update operation success. Affected 1 schedules"
    And the total schedule results should be "1–2 of 2" after clicking by id on "refresh-schedules"
