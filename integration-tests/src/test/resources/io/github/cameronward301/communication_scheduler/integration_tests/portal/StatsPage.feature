# Created by Cameron at 16/04/2024
@WebPortal
@CloseBrowserAfterScenario
Feature: Stats Web Portal Page
  Tests for the Stats Web Portal Page

  Scenario: Can load the stats page:
    Given I navigate to "/stats"
    Then the element with id "stats-page-heading" should be set to: "System Monitoring"
    And the button with id "refresh-graphs" should not be disabled
    And there are 11 iframes loaded
