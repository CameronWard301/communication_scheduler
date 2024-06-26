// Adapted from https://swagger-autogen.github.io/docs/getting-started/quick-start
const fs = require("fs");
const path = require("path");
const options = {
  openapi: "3.0.0"
};
const swaggerAutogen = require("swagger-autogen")(options);
const outputFile = "./src/swagger_output.json";
const doc = {
  info: {
    version: "1.0.0",
    title: "Communication Scheduler BFF API",
    description:
      "Documentation automatically generated by the <b>swagger-autogen</b> module."
  },
  host: "localhost:3000",
  basePath: "/v1/bff/",
  schemes: ["https", "http"],
  consumes: ["application/json"],
  produces: ["application/json"],
  tags: [
    {
      name: "Auth",
      description: "Authentication related end-points"
    },
    {
      name: "Preferences",
      description: "Preferences related end-points"
    }
  ],
  securityDefinitions: {
    bearerAuth: {
      type: "http",
      scheme: "bearer"
    }
  },
  components: {
    schemas: {
      AuthToken: {
        token: "string",
        expires: "string"
      },
      AuthScopes: ["GATEWAY:READ", "GATEWAY:WRITE"],
      Gateway: {
        id: "string",
        endpointUrl: "https://example.com/email",
        friendlyName: "My Email Gateway",
        description: "This is a gateway for sending emails",
        dateCreated: "03/03/2024 16:45:12"
      },
      GatewayPage: {
        gateways: [{
          id: "string",
          endpointUrl: "https://example.com/email",
          friendlyName: "My Email Gateway",
          description: "This is a gateway for sending emails",
          dateCreated: "03/03/2024 16:45:12"
        }],
        totalElements: 1,
        pageSize: 5,
        pageNumber: 0
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
          unit: "S"
        },
        maximumInterval: {
          value: 100,
          unit: "D"
        },
        startToCloseTimeout: {
          value: 100,
          unit: "H"
        }
      },
      ClientSchedulePage: {
        schedules: [{
          id: "1234",
          status: "Paused",
          gatewayName: "myGateway",
          gatewayId: "1234",
          gateway: {
            id: "string",
            endpointUrl: "https://example.com/email",
            friendlyName: "My Email Gateway",
            description: "This is a gateway for sending emails",
            dateCreated: "03/03/2024 16:45:12"
          },
          userId: "1234",
          nextRun: "03/03/2024 16:45:12",
          lastRun: "03/03/2024 16:45:12"
        }],
        totalElements: 1,
        pageSize: 25,
        pageNumber: 0
      },
      ClientScheduleCreateRequest: {
        $gatewayId: "1234",
        $userId: "54653",
        $scheduleType: "Interval",
        intervalSpec: {
          days: "1",
          hours: "0",
          minutes: "0",
          seconds: "0",
          offset: "0",
          offsetPeriod: "S"
        },
        calendarWeekSpec: {
          dayOfWeek: "Monday",
          hours: "0",
          minutes: "0"
        },
        calendarMonthSpec: {
          dayOfMonth: 1,
          month: "January",
          hours: 0,
          minutes: 0
        },
        cronSpec: "0 0 0 0 0"
      },
      ClientScheduleEditRequest: {
        $scheduleId: "1234",
        gatewayId: "1234",
        userId: "54653",
        scheduleType: "Interval",
        intervalSpec: {
          days: "1",
          hours: "0",
          minutes: "0",
          seconds: "0",
          offset: "0",
          offsetPeriod: "S"
        },
        calendarWeekSpec: {
          dayOfWeek: "Monday",
          hours: "0",
          minutes: "0"
        },
        calendarMonthSpec: {
          dayOfMonth: 1,
          month: "January",
          hours: 0,
          minutes: 0
        },
        cronSpec: "0 0 0 0 0"
      },
      ClientSchedule: {
        id: "1234",
        status: "Paused",
        gatewayName: "myGateway",
        gatewayId: "1234",
        gateway: {
          id: "string",
          endpointUrl: "https://example.com/email",
          friendlyName: "My Email Gateway",
          description: "This is a gateway for sending emails",
          dateCreated: "03/03/2024 16:45:12"
        },
        userId: "1234",
        nextRun: "03/03/2024 16:45:12",
        lastRun: "03/03/2024 16:45:12"
      },
      ClientScheduleCreateEdit: {
        id: "1234",
        status: "Paused",
        gatewayName: "myGateway",
        gatewayId: "1234",
        userId: "1234",
        cronExpression: "0 0 0 0 0",
        interval: {
          days: "1",
          hours: "0",
          minutes: "0",
          seconds: "0",
          offset: "0",
          offsetPeriod: "S"
        },
        calendar: {
          comment: "",
          seconds: [{
            start: "0",
            end: "0",
            step: "1"
          }],
          minutes: [{
            start: "0",
            end: "0",
            step: "1"
          }],
          hour: [{
            start: "0",
            end: "0",
            step: "1"
          }],
          dayOfMonth: [{
            start: "0",
            end: "0",
            step: "1"
          }],
          dayOfWeek: [{
            start: "0",
            end: "0",
            step: "1"
          }],
          year: []
        },
        nextRun: "03/03/2024 16:45:12",
        lastRun: "03/03/2024 16:45:12"
      },
      ClientBulkUpdateScheduleRequest: {
        paused: false,
        gatewayId: "1234",
        $actionType: "Pause"
      },
      BulkActionResult: {
        success: true,
        failureReasons: [],
        totalModified: 10
      },
      GatewayName: {
        friendlyName: "My Gateway Name"
      },
      ClientHistoryPage: {
        historyItems: [{
          workflowId: "1234",
          id: "1234",
          userId: "1234",
          gatewayId: "1234",
          gatewayName: "My Gateway",
          scheduleId: "1234",
          status: "Running",
          startTime: "03/03/2024 16:45:12",
          endTime: "03/03/2024 16:45:12"
        }],
        totalElements: 1,
        pageSize: 25,
        pageNumber: 0
      }
    }
  }
};

/**
 * Find all the controller files in the project
 * @param root of the project relative to this file
 * @returns a list of paths to the controller files
 */
const GetControllerFilePaths = (root: string): string[] => {
  const controllerPaths: string[] = [];

  const traverseDirectory = (dir: string): void => {
    const files = fs.readdirSync(dir);
    console.log("files: " + files);
    for (const file of files) {
      const filePath = path.join(dir, file);
      const stats = fs.statSync(filePath);

      if (stats.isDirectory()) {
        traverseDirectory(filePath);
      } else if (stats.isFile()) {
        const fileName = path.basename(file);
        if (fileName.endsWith("controller.ts")) {
          const relativePath = path.relative(root, filePath);
          controllerPaths.push(root + relativePath);
        }
      }
    }
  };

  traverseDirectory(root);
  console.log("controllerPaths: " + controllerPaths);
  return controllerPaths;
};

// Will prodice a swagger_output.json file in the root of the project that reflects the current state of the project
swaggerAutogen(outputFile, GetControllerFilePaths("./src/"), doc).then();
