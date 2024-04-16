# Created by Cameron at 07/03/2024
@WebPortal
@CloseBrowserAfterScenario
Feature: Gateway Web portal page
  Allows searching, modifying, removing and adding of gateways

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should be able to filter for gateway by id
    Given I navigate to "/gateways"
    When I set the "gateway-id-filter-input" to be the gateway id
    And I press enter on the field with id "gateway-id-filter-input"
    And I click by id on "refresh-gateways-button"
    Then I should see the gateway with the id
    And the total gateway results should be 1

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should be able to filter for gateway by name
    Given I navigate to "/gateways"
    When I set the "gateway-name-filter-input" to be the gateway name
    And I press enter on the field with id "gateway-name-filter-input"
    Then I should see the gateway with the name

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should be able to filter for gateway by description
    Given I navigate to "/gateways"
    When I set the "gateway-description-filter-input" to be the gateway description
    And I click by id on "gateway-description-filter-apply-button"
    Then I should see the gateway with the description

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should be able to filter for gateway by endpoint url
    Given I navigate to "/gateways"
    When I set the "gateway-url-filter-input" to be the gateway endpoint url
    And I click by id on "gateway-url-filter-apply-button"
    Then I should see the gateway with the endpoint url

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should be able to filter by name, description and endpoint url
    Given I navigate to "/gateways"
    When I set the "gateway-name-filter-input" to be the gateway name
    And I set the "gateway-description-filter-input" to be the gateway description
    And I set the "gateway-url-filter-input" to be the gateway endpoint url
    And I click by id on "gateway-url-filter-apply-button"
    Then I should see the gateway with the name, description and endpoint url

  Scenario: User should be able to reset filters
    Given I navigate to "/gateways"
    And I set the "gateway-name-filter-input" to be the gateway name
    And I set the "gateway-description-filter-input" to be the gateway description
    And I set the "gateway-url-filter-input" to be the gateway endpoint url
    And I click by id on "gateway-url-filter-apply-button"
    When I click by id on "reset-filters-button"
    Then the field with id "gateway-name-filter-input" should be set to: ""
    And the field with id "gateway-description-filter-input" should be set to: ""
    And the field with id "gateway-url-filter-input" should be set to: ""

  @RemoveExistingSchedules
  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should be able to modify gateway
    Given I navigate to "/gateways"
    And I set the "gateway-id-filter-input" to be the gateway id
    And I press enter on the field with id "gateway-id-filter-input"
    And I click the modify gateway button
    When I set the "gateway-name-input" field to be "updated gateway name"
    And I set the "gateway-description-input" field to be "updated gateway description"
    And I set the "gateway-url-input" field to be "http://updated.com/gateway/url"
    And I click by id on "confirm-edit-button"
    Then the element with id "friendly-name-new-value" should be set to: "updated gateway name"
    Then the element with id "description-new-value" should be set to: "updated gateway description"
    Then the element with id "endpoint-url-new-value" should be set to: "http://updated.com/gateway/url"
    Then the element with id "affected-schedules-count" should be set to: "0"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Gateway updated"

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should not be able to modify gateway if no fields are changed
    Given I navigate to "/gateways"
    And I set the "gateway-id-filter-input" to be the gateway id
    And I press enter on the field with id "gateway-id-filter-input"
    And I click the modify gateway button
    When I click by id on "confirm-edit-button"
    Then I should see a snackbar message with the text "No changes to save, please edit a field and try again"

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should not be able to modify gateway if gateway name is left blank
    Given I navigate to "/gateways"
    And I set the "gateway-id-filter-input" to be the gateway id
    And I press enter on the field with id "gateway-id-filter-input"
    And I click the modify gateway button
    When I set the "gateway-name-input" field to be ""
    Then the button with id "confirm-edit-button" should be disabled

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should not be able to modify gateway if gateway url is left blank
    Given I navigate to "/gateways"
    And I set the "gateway-id-filter-input" to be the gateway id
    And I press enter on the field with id "gateway-id-filter-input"
    And I click the modify gateway button
    When I set the "gateway-url-input" field to be ""
    Then the button with id "confirm-edit-button" should be disabled

  @RemoveExistingSchedules
  @MongoDbSetupEntity
  Scenario: User should be able remove a gateway from the table
    Given I navigate to "/gateways"
    And I set the "gateway-id-filter-input" to be the gateway id
    And I press enter on the field with id "gateway-id-filter-input"
    When I click the delete gateway button
    Then the delete gateway fields are shown
    Then the element with id "affected-schedules-count" should be set to: "0"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Gateway deleted"

  @RemoveExistingSchedules
  @MongoDbSetupEntity
  Scenario: User should be able remove a gateway from the edit screen
    Given I navigate to "/gateways"
    And I set the "gateway-id-filter-input" to be the gateway id
    And I press enter on the field with id "gateway-id-filter-input"
    And I click the modify gateway button
    When I click by id on "delete-gateway-button"
    Then the delete gateway fields are shown
    Then the element with id "affected-schedules-count" should be set to: "0"
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Gateway deleted"

  Scenario: User should not be able to create a gateway with empty fields
    Given I navigate to "/gateways"
    When I click by id on "add-gateway-button"
    Then the button with id "confirm-add-button" should be disabled
    When I set the "gateway-name-input" field to be "test-create-gateway"
    Then the button with id "confirm-add-button" should be disabled
    When I set the "gateway-description-input" field to be "test-create-gateway-description"
    Then the button with id "confirm-add-button" should be disabled
    When I set the "gateway-url-input" field to be "http://test-create-gateway.com"
    Then the button with id "confirm-add-button" should not be disabled
    When I set the "gateway-name-input" field to be ""
    Then the button with id "confirm-add-button" should be disabled

  @MongoDbRemoveEntityFromWorld
  Scenario: User should be able to add a new gateway
    Given I navigate to "/add-gateway"
    And I have a gateway with the following information:
      | friendlyName        | description                     | endpointUrl                    |
      | test-create-gateway | test-create-gateway-description | http://test-create-gateway.com |
    And I set the input fields to be the gateway information
    When I click by id on "confirm-add-button"
    Then the add gateway fields are shown
    When I click by id on "confirm-modal-button"
    Then I should see a snackbar message with the text "Gateway added"
    And the field with id "gateway-id-filter-input" should be set to the created gateway id
