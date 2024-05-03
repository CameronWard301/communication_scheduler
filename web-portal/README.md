# Web Portal

<!-- TOC -->
* [Web Portal](#web-portal)
  * [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Cluster Configuration Only](#cluster-configuration-only)
    * [Cluster And Local Configuration](#cluster-and-local-configuration)
    * [Installing & Running Locally](#installing--running-locally)
    * [Running in a Cluster](#running-in-a-cluster)
  * [Running the tests](#running-the-tests)
  * [Deployment](#deployment)
  * [Built With](#built-with)
<!-- TOC -->

This is a React project built with Node.js, Typescript and MobX to provide a user interface to interact with the
communication platform.

## Getting Started

Follow these instructions to run the project locally and configure for kubernetes deployment

### Prerequisites

Ensure you have the following installed and configured locally:

- [Node 20.11.1 or newer](https://nodejs.org/en)
- [OpenSSL](https://www.openssl.org/)

See [helm-deployment](../deployment/helm) to deploy the following components to a kubernetes cluster if not running
locally:

- [BFF API](../web-portal-bff) running locally or in a development environment

### Cluster Configuration Only

This section describes the configuration options available for the Web Portal via environment variables for the web
server when running in a cluster. These environment variables are set in the [helm chart](../deployment/helm) and are
used with the [config file](#cluster-and-local-configuration) configuration below.

- The file `public/server.cjs` is a server file that is used to serve the static files in the `build` directory when
  deployed in the cluster.
    - It must run in HTTPS mode, follow the instructions below to generate the required SSL keys and set the correct
      environment variables.
        - `openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout ./private.key -out public.crt`
            - Follow prompts to enter the certificate information.
            - This will create a private.key file and a public.crt file in the project directory.
        - Use the contents of these files in `PRIVATE_KEY` and `CERTIFICATE` environment variables.

> [!CAUTION]
> Setting SSL_VERIFICATION to false should NOT be used in a live production environment, it should only be used when
> testing with self-signed SSL certificates.

| Environment Variable | Description                                                                                                           | Default Value | Required |
|----------------------|-----------------------------------------------------------------------------------------------------------------------|---------------|----------|
| PORT                 | The port number for the application to run on                                                                         | 9090          | Y        |
| BFF_API_IRL          | The base URL for the BFF API                                                                                          |               | Y        |
| PRIVATE_KEY          | The contents of the private.key file generated using the openssl command                                              |               | Y        |
| CERTIFICATE          | The contents of the public.crt file generated using the openssl command                                               |               | Y        |
| SSL_VERIFICATION     | If using self signed certificates for SSL, set this to false for **non production environments only** for development | true          | N        |

### Cluster And Local Configuration

This section describes the configuration options set by the `src/config/config.json` file. Note that when running
locally, this file is used for the configuration. When running in a cluster, the config.json file should be mapped to
the root directory of the container and is then sent by the server to the client. This is configured automatically in
the [helm chart](../deployment/helm) configuration.

> [!CAUTION]
> Setting verifyHttps to false should NOT be used in a live production environment, it should only be used when testing
> with self-signed SSL certificates.

| Key           | Description                                                                                                                              | Default Value                 | Required |
|---------------|------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------|----------|
| bffBaseUrl    | The url for the BFF API running locally or in a cluster. In the cluster this should just be the base URI e.g. "/v1/bff"                  | http://localhost:9090/v1/bff  | Y        |
| bffGrafanaUrl | The url for the BFF API with the grafana endpoint configured, when deployed from helm this is set to /grafana                            | http://localhost:9090/grafana | Y        |
| verifyHttps   | If using self signed certificates for SSL, set this to false for **non production environments only** for development                    | true                          | Y        |
| environment   | The environment name the portal will run in, can be set to any value. It's used to configure the cookie name that stores the auth token. | local                         | Y        |

### Installing & Running Locally

Follow the instructions below to get a development environment running:

1. Clone the repository
2. Navigate to the project directory
3. Configure the src/config/config.json file with the correct values for the BFF API URL and the Grafana URL
4. Run `npm i` to install the project dependencies
5. Run `npm run dev` to start the server

### Running in a Cluster

- Deploy the Web Portal to a kubernetes cluster using the [helm chart](../deployment/helm) configuration.
- Port forward the `cs-web-portal-service` to access the Web Portal locally. Use a kubectl command or a software such
  as [Lens](https://k8slens.dev/) to port forward the service.
- Go to browser and type https://localhost:3000 (Use your port number chosen and make sure its HTTPs).
    - If using self-signed certificates, the browser will display an unsafe warning, click anywhere on the page and
      type `thisisunsafe` to bypass the warning.
    - The web portal will then be able to connect to the server.

## Running the tests

- To run the integration tests see the [Integration Tests Project](../integration-tests)
  and make sure that the `@WebPortal` is added to the filter expression.

## Deployment

- Run the command from the project root to build and push a new image for both arm and amd platforms.
    - `docker buildx build --platform linux/amd64,linux/arm64 -t <account name>/<image-name>:<image-tag> --push .`
- See [helm deployment](../deployment/helm) to deploy the Web Portal a kubernetes cluster.

## Built With

- [NodeJS](https://nodejs.org/en)
- [React](https://react.dev/)
- [Vite](https://vitejs.dev/)
- [MobX](https://mobx.js.org/README.html)
- [Material UI](https://material-ui.com/)

