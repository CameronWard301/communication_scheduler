# Web Portal BFF API

<!-- TOC -->
* [Web Portal BFF API](#web-portal-bff-api)
  * [API Specification](#api-specification)
  * [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Configuration](#configuration)
    * [Installing & Running Locally](#installing--running-locally)
  * [Running the tests](#running-the-tests)
  * [Deployment](#deployment)
  * [Generate Swagger API Documentation](#generate-swagger-api-documentation)
  * [Built With](#built-with)
<!-- TOC -->

This is a middleware API built with Node.js with Typescript and Express to communicate with the backend services (
gateway-api, schedule-api, history-api, auth-api, preferences-api and Grafana) and the [web-portal](../web-portal).

## API Specification

See [swagger specification](src/swagger_output.json) for the exposed endpoints.

## Getting Started

Follow these instructions to run the project locally and configure for kubernetes deployment

### Prerequisites

Ensure you have the following installed and configured locally:

- [Node 20.11.1 or newer](https://nodejs.org/en)
- [OpenSSL](https://www.openssl.org/)

See [helm-deployment](../deployment/helm) to deploy the following components to a kubernetes cluster if not running
locally:

- [Auth API](../auth-api) running locally or in a development environment
- [Gateway API](../gateway-api) running locally or in a development environment
- [Schedule API](../schedule-api) running locally or in a development environment
- [History API](../history-api) running locally or in a development environment
- [Preferences API](../preferences-api) running locally or in a development environment
- Grafana running locally or in a development environment (helm deployment only)

### Configuration

This section describes the configuration options available for the Web Portal BFF API via environment variables

- Run the project with the `HTTPS_ENABLED` environment variable set to true to enable SSL
- To generate self-signed certificate files for development purposes, run the following command in the project
  directory:
    - `openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout ./private.key -out public.crt`
        - Follow prompts to enter the certificate information.
        - This will create a private.key file and a public.crt file in the project directory.
    - Use the contents of these files in `PRIVATE_KEY` and `CERTIFICATE` environment variables.

> [!CAUTION]
> Setting SSL_VERIFICATION to false or ALLOW_ORIGIN to "*" should NOT be used in a live production environment, it
> should only be used when testing with self-signed SSL certificates.

| Environment Variable  | Description                                                                                                                          | Default Value                       | Required         |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------|------------------|
| PORT                  | The port number for the application to run on                                                                                        | 9090                                | Y                |
| AUTH_API_URL          | The base URL for the Auth API                                                                                                        | https://localhost:53655             | Y                |
| PREFERENCES_API_URL   | The base URL for the Preferences API (include /preferences here)                                                                     | https://localhost:53933/preferences | Y                |
| GATEWAY_API_URL       | The base URL for the Gateway API (include /gateway here)                                                                             | https://localhost:64854/gateway     | Y                |
| HISTORY_API_URL       | The base URL for the History API (include /workflow here)                                                                            | https://localhost:54427/workflow    | Y                |
| SCHEDULE_API_URL      | The base URL for the Schedule API (include /schedule here)                                                                           | https://localhost:53286/schedule    | Y                |
| GRAFANA_API_URL       | The base URL for the Grafana service.                                                                                                | http://localhost:61125              | Y                |
| HTTPS_ENABLED         | If set to true, the server will start in HTTPs mode, make sure to set the PRIVATE_KEY and CERTIFICATE environment variables.         | true                                | Y                |
| PRIVATE_KEY           | The contents of the private.key file generated using the openssl command                                                             |                                     | Y if using HTTPs |
| CERTIFICATE           | The contents of the public.crt file generated using the openssl command                                                              |                                     | Y if using HTTPs |
| ALLOW_ORIGIN          | A string containing the allowed origins that can request resources from the BFF API                                                  | *                                   | Y                |
| LIMIT_PERIOD_SECONDS  | Sets the rate limiter period in seconds. I.e. How long to measure the number of requests coming from one IP address                  | 120                                 | Y                |
| LIMIT_NUMBER_REQUESTS | Sets the number of requests allowed in the given period for the rate limiter. I.e. 1000 means 1000 requests can be made in 2 minutes | 1000                                | Y                |
| SSL_VERIFICATION      | If using self signed certificates for SSL, set this to false for **non production environments only** for development                | true                                | N                |

### Installing & Running Locally

Follow the instructions below to get a development environment running:

1. Clone the repository
2. Navigate to the project directory
3. Run `npm i` to install the project dependencies
4. Run `npm run dev` to start the server

## Running the tests

- To run the unit tests, run `npm run test` in the project directory
- To run the integration tests see the [Integration Tests Project](../integration-tests)
  and make sure that the `@WebPortal` is added to the filter expression.

## Deployment

- Run the command from the project root to build and push a new image for both arm and amd platforms.
    - `docker buildx build --platform linux/amd64,linux/arm64 -t <account name>/<image-name>:<image-tag> --push .`
- See [helm deployment](../deployment/helm) to deploy the Web Portal BFF API to a kubernetes cluster.

## Generate Swagger API Documentation

This project uses the [Swagger Autogen](https://swagger-autogen.github.io/docs/getting-started/quick-start/) package to
generate the swagger documentation.

- Once an update has been made to the API documentation, run `npm run swagger-autogen` to generate the updated swagger
  documentation.
    - The output file is located in `src/swagger_output.json` and can be accessed by
      visiting [`http://localhost:9090/api-docs`](http://localhost:9090/api-docs)
      or [`https://localhost:9090/api-docs`](https://localhost:9090/api-docs) when the server is running.

## Built With

- [NodeJS](https://nodejs.org/en)
- [Express](https://expressjs.com/)
- [Jest](https://jestjs.io/)
- [Swagger Autogen](https://swagger-autogen.github.io/docs/getting-started/quick-start/)
