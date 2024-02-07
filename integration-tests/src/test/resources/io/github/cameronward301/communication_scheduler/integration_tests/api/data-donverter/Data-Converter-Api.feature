@DataConverterAPI
Feature: Data Converter API
  Test that the data converter API can encode and decode payloads.

  Scenario: Should encode payload
    Given I have an unencrypted payload
    When I POST to data-converter "encode"
    Then the response returned contains 1 payload
    When I convert the response back to a payload
    Then the payload returned is encrypted
    And the encrypted headers have been set

  Scenario: Should decode payload
    Given I have an encrypted payload
    When I POST to data-converter "decode"
    Then the response returned contains 1 payload
    When I convert the response back to a payload
    Then the payload returned is decrypted
    And the decrypted headers have been set
