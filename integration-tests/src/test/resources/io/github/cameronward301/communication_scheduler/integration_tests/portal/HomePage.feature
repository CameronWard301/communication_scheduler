# Created by Cameron at 27/02/2024
@WebPortal
@CloseBrowserAfterScenario
Feature: Home Page
  Test home page functionalities

  Scenario: Should navigate to home page
    When I navigate to "/"
    Then I should see the home page

  Scenario: Should navigate to each page
    When I navigate to "/"
    And I click by id on "history-card"
    Then The URI is now "/history"
    When I navigate to "/"
    And I click by id on "schedules-card"
    Then The URI is now "/schedules"
    When I navigate to "/"
    And I click by id on "gateways-card"
    Then The URI is now "/gateways"
    When I navigate to "/"
    And I click by id on "preferences-card"
    Then The URI is now "/preferences"
    When I navigate to "/"
    And I click by id on "stats-card"
    Then The URI is now "/stats"
