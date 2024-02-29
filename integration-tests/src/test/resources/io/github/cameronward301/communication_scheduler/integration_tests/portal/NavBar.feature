# Created by Cameron at 27/02/2024
@WebPortal
@CloseBrowserAfterScenario
Feature: Navigation Bar
  Tests the functionality of the navigation bar

  Scenario: Expanding Navigation Bar Reveals Labels
    Given I navigate to "/"
    When I click by id on "navbar-expand"
    Then The nav item with id "home" should have the text "Home"
    And The nav item with id "history" should have the text "Communication History"
    And The nav item with id "schedules" should have the text "Communication Schedules"
    And The nav item with id "gateways" should have the text "Communication Gateways"
    And The nav item with id "preferences" should have the text "Platform Preferences"
    And The nav item with id "monitoring" should have the text "Platform Monitoring"

  Scenario: Clicking Menu Items Navigates to Correct Pages
    Given I navigate to "/"
    And I click by id on "nav-home"
    Then The URI is now "/"
    And I click by id on "nav-history"
    Then The URI is now "/history"
    When I navigate to "/"
    And I click by id on "nav-schedules"
    Then The URI is now "/schedules"
    When I navigate to "/"
    And I click by id on "nav-gateways"
    Then The URI is now "/gateways"
    When I navigate to "/"
    And I click by id on "nav-preferences"
    Then The URI is now "/preferences"
    When I navigate to "/"
    And I click by id on "nav-stats"
    Then The URI is now "/stats"
