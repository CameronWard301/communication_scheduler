# Created by Cameron at 02/04/2024
@WebPortal
@CloseBrowserAfterScenario
Feature: # Gateway Filter Component
  # Tests the functionality of the Gateway Filter Component on the schedule page

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should be able to filter for gateway by id
    Given I navigate to "/schedules"
    And I click by id on "gateway-filter-button"
    When I set the "gateway-id-search" to be the gateway id
    And I press enter on the field with id "gateway-id-search"
    And I click by id on "gateway-filter-search"
    Then I should see the gateway with the id in the filter table
    And the total gateway results should be 1

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should be able to filter for gateway by name
    Given I navigate to "/schedules"
    And I click by id on "gateway-filter-button"
    When I set the "gateway-name-search" to be the gateway name
    And I press enter on the field with id "gateway-name-search"
    And I click by id on "gateway-filter-search"
    Then I should see the gateway with the id in the filter table

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should be able to filter for gateway by description
    Given I navigate to "/schedules"
    And I click by id on "gateway-filter-button"
    When I set the "gateway-description-search" to be the gateway description
    And I click by id on "gateway-filter-search"
    Then I should see the gateway with the id in the filter table

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should be able to filter for gateway by endpoint url
    Given I navigate to "/schedules"
    And I click by id on "gateway-filter-button"
    When I set the "gateway-url-search" to be the gateway endpoint url
    And I click by id on "gateway-filter-search"
    Then I should see the gateway with the id in the filter table

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User should be able to filter by name, description and endpoint url
    Given I navigate to "/schedules"
    And I click by id on "gateway-filter-button"
    When I set the "gateway-name-search" to be the gateway name
    And I set the "gateway-description-search" to be the gateway description
    And I set the "gateway-url-search" to be the gateway endpoint url
    And I click by id on "gateway-filter-search"
    Then I should see the gateway with the id in the filter table

  Scenario: User should be able to reset filters
    Given I navigate to "/schedules"
    And I click by id on "gateway-filter-button"
    And I set the "gateway-name-search" to be the gateway name
    And I set the "gateway-description-search" to be the gateway description
    And I set the "gateway-url-search" to be the gateway endpoint url
    And I click by id on "gateway-filter-reset-button"
    And I click by id on "gateway-filter-button"
    Then the field with id "gateway-name-search" should be set to: ""
    And the field with id "gateway-description-search" should be set to: ""
    And the field with id "gateway-url-search" should be set to: ""
