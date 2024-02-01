@GatewayAPI
Feature: Gateway Api
  Tests the Gateway Api GET, POST, PUT, DELETE methods

  Scenario: User creates a gateway
    Given I have a gateway with the following information:
      | endpointUrl            | friendlyName | description  |
      | http://example.com/api | Test Gateway | Gateway Desc |
    And I have a bearer token with the "GATEWAY:WRITE" scope
    When I create the gateway
    Then a gateway is returned with a status code of 201
    And the test framework removes the gateway

  Scenario: User creates a gateway without description
    Given I have a gateway with the following information:
      | endpointUrl            | friendlyName |
      | http://example.com/api | Test Gateway |
    And I have a bearer token with the "GATEWAY:WRITE" scope
    When I create the gateway
    Then a gateway is returned with a status code of 201
    And the test framework removes the gateway

  Scenario: User creates a gateway without url
    Given I have a gateway with the following information:
      | friendlyName | description  |
      | Test Gateway | Gateway Desc |
    And I have a bearer token with the "GATEWAY:WRITE" scope
    When I create the gateway
    Then the response code is 400 and message: "400 : \"'endpointUrl' cannot be empty\""

  Scenario: User creates a gateway without friendly name
    Given I have a gateway with the following information:
      | endpointUrl            | description  |
      | http://example.com/api | Gateway Desc |
    And I have a bearer token with the "GATEWAY:WRITE" scope
    When I create the gateway
    Then the response code is 400 and message: "400 : \"'friendlyName' cannot be empty\""

  Scenario: User creates a gateway without a valid scope
    Given I have a gateway with the following information:
      | endpointUrl            | friendlyName |
      | http://example.com/api | Test Gateway |
    And I have a bearer token with the "GATEWAY:READ" scope
    When I create the gateway
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User creates a gateway without an auth token
    Given I have a gateway with the following information:
      | endpointUrl            | friendlyName |
      | http://example.com/api | Test Gateway |
    And I have no token
    When I create the gateway
    Then the response code is 401 and message: "401 : [no body]"

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User gets a gateway by id
    Given I have a bearer token with the "GATEWAY:READ" scope
    When I get the gateway by id
    Then a gateway is returned with a status code of 200

  Scenario: User gets a gateway by unknown id
    Given I have a bearer token with the "GATEWAY:READ" scope
    When I get the gateway by an unknown id: "573df1e-0b99-4bea-a38b-a8defdf0c6f0"
    Then the response code is 404 and message: "404 : \"Gateway with id '573df1e-0b99-4bea-a38b-a8defdf0c6f0' not found\""

  @MongoDbAddMultipleEntities
  @MongoDbRemoveMultipleEntities
  Scenario: User gets a list of gateways
    Given I have a bearer token with the "GATEWAY:READ" scope
    When I get the list of gateways
    Then the list of gateways is returned with a status code of 200
    And there are 5 gateways in the list

  @MongoDbAddMultipleEntities
  @MongoDbRemoveMultipleEntities
  Scenario: User gets a list of gateways with query parameters
    Given I set the "friendlyName" query parameter to "test"
    And I set the "pageSize" query parameter to "10"
    And I have a bearer token with the "GATEWAY:READ" scope
    When I get the list of gateways
    Then the list of gateways is returned with a status code of 200
    And there are 10 gateways in the list

  Scenario: User gets a gateway with invalid scope
    Given I have a bearer token with the "GATEWAY:WROTE" scope
    When I get the gateway by an unknown id: "573df1e-0b99-4bea-a38b-a8defdf0c6f0"
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User gets a gateway with no auth token
    Given I have no token
    When I get the gateway by an unknown id: "573df1e-0b99-4bea-a38b-a8defdf0c6f0"
    Then the response code is 401 and message: "401 : [no body]"

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User updates a gateway
    Given I have a gateway with the following information:
      | endpointUrl                    | friendlyName         | description          |
      | http://updated-example.com/api | Updated Test Gateway | Updated Gateway Desc |
    And I have a bearer token with the "GATEWAY:WRITE" scope
    When I update the existing gateway
    Then a gateway is returned with a status code of 200

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User updates a gateway and tires to remove the endpointUrl
    Given I have a gateway with the following information:
      | friendlyName         | description          |
      | Updated Test Gateway | Updated Gateway Desc |
    And I have a bearer token with the "GATEWAY:WRITE" scope
    When I update the existing gateway
    Then the response code is 400 and message: "400 : \"'endpointUrl' cannot be empty\""

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User updates a gateway and tires to remove the friendlyName
    Given I have a gateway with the following information:
      | endpointUrl          | description          |
      | Updated Test Gateway | Updated Gateway Desc |
    And I have a bearer token with the "GATEWAY:WRITE" scope
    When I update the existing gateway
    Then the response code is 400 and message: "400 : \"'friendlyName' cannot be empty\""

  Scenario: User updates a gateway without an id
    Given I have a gateway with the following information:
      | endpointUrl                    | friendlyName         | description          |
      | http://updated-example.com/api | Updated Test Gateway | Updated Gateway Desc |
    And I have a bearer token with the "GATEWAY:WRITE" scope
    When I update the gateway without an id
    Then the response code is 400 and message: "400 : \"Please provide a gateway 'id' in the request body\""

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User updates a gateway with invalid scope
    Given I have a gateway with the following information:
      | endpointUrl                    | friendlyName         | description          |
      | http://updated-example.com/api | Updated Test Gateway | Updated Gateway Desc |
    And I have a bearer token with the "GATEWAY:READ" scope
    When I update the existing gateway
    Then the response code is 403 and message: "403 : [no body]"

  @MongoDbSetupEntity
  @MongoDbRemoveEntity
  Scenario: User updates a gateway with no auth token
    Given I have a gateway with the following information:
      | endpointUrl                    | friendlyName         | description          |
      | http://updated-example.com/api | Updated Test Gateway | Updated Gateway Desc |
    And I have no token
    When I update the existing gateway
    Then the response code is 401 and message: "401 : [no body]"

  @MongoDbSetupEntity
  Scenario: User deletes a gateway by its id
    Given I have a bearer token with the "GATEWAY:DELETE" scope
    When I delete the existing gateway by id
    Then the response code is 204

  Scenario: User deletes a gateway by an unknown id
    Given I have a bearer token with the "GATEWAY:DELETE" scope
    When I delete the gateway with an unknown id: "573df1e-0b99-4bea-a38b-a8defdf0c6f0"
    Then the response code is 404 and message: "404 : \"Gateway with id '573df1e-0b99-4bea-a38b-a8defdf0c6f0' not found\""

  Scenario: User deletes a gateway by an invalid scope
    Given I have a bearer token with the "GATEWAY:WRITE" scope
    When I delete the gateway with an unknown id: "573df1e-0b99-4bea-a38b-a8defdf0c6f0"
    Then the response code is 403 and message: "403 : [no body]"

  Scenario: User deletes a gateway with no auth token
    Given I have no token
    When I delete the gateway with an unknown id: "573df1e-0b99-4bea-a38b-a8defdf0c6f0"
    Then the response code is 401 and message: "401 : [no body]"
