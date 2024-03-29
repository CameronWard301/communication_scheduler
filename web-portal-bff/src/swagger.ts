// Adapted from https://swagger-autogen.github.io/docs/getting-started/quick-start
const fs = require("fs");
const path = require("path");
const options = {
  openapi: "3.0.0",
}
const swaggerAutogen = require("swagger-autogen")(options);
const outputFile = './src/swagger_output.json'
const doc = {
  info: {
    version: '1.0.0',
    title: 'Communication Scheduler BFF API',
    description:
      'Documentation automatically generated by the <b>swagger-autogen</b> module.'
  },
  host: 'localhost:3000',
  basePath: '/v1/bff/',
  schemes: ['https', "http"],
  consumes: ['application/json'],
  produces: ['application/json'],
  tags: [
    {
      name: 'Auth',
      description: 'Authentication related end-points'
    },
    {
      name: 'Preferences',
      description: 'Preferences related end-points'
    }
  ],
  securityDefinitions: {
    bearerAuth: {
      type: 'http',
      scheme: 'bearer'
    }
  },
  components: {
    schemas: {
      AuthToken: {
        token: 'string',
        expires: 'string'
      },
      AuthScopes: ['GATEWAY:READ', 'GATEWAY:WRITE'],
      Gateway: {
        id: 'string',
        endpointUrl: "https://example.com/email",
        friendlyName: "My Email Gateway",
        description: "This is a gateway for sending emails",
        dateCreated: "03/03/2024 16:45:12"
      },
      GatewayPage: {
        gateways: [{
          id: 'string',
          endpointUrl: "https://example.com/email",
          friendlyName: "My Email Gateway",
          description: "This is a gateway for sending emails",
          dateCreated: "03/03/2024 16:45:12"
        }],
        totalElements: 1,
        pageSize: 5,
        pageNumber: 0,
      },
      TotalMatches: {
        total: 1
      },
      Preferences: {
        gatewayTimeoutSeconds: 100,
        maximumAttempts: 200,
        backoffCoefficient: 4.0,
        initialInterval: {
          value: 1,
          unit: 'S'
        },
        maximumInterval: {
          value: 100,
          unit: 'D'
        },
        startToCloseTimeout: {
          value: 100,
          unit: 'H'
        }
      }
    }
  }
}

/**
 * Find all the controller files in the project
 * @param root of the project relative to this file
 * @returns a list of paths to the controller files
 */
const GetControllerFilePaths = (root: string): string[] => {
  const controllerPaths: string[] = []

  const traverseDirectory = (dir: string): void => {
    const files = fs.readdirSync(dir)
    console.log('files: ' + files)
    for (const file of files) {
      const filePath = path.join(dir, file)
      const stats = fs.statSync(filePath)

      if (stats.isDirectory()) {
        traverseDirectory(filePath)
      } else if (stats.isFile()) {
        const fileName = path.basename(file)
        if (fileName.endsWith('controller.ts')) {
          const relativePath = path.relative(root, filePath)
          controllerPaths.push(root + relativePath)
        }
      }
    }
  }

  traverseDirectory(root)
  console.log('controllerPaths: ' + controllerPaths)
  return controllerPaths
}

// Will prodice a swagger_output.json file in the root of the project that reflects the current state of the project
swaggerAutogen(outputFile, GetControllerFilePaths('./src/'), doc).then()
