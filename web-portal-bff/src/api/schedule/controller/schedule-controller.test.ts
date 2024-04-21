import express from "express";
import router from "./schedule-controller";
import { ScheduleService } from "../service/schedule-service";
import request from "supertest";
import { BFFResponse } from "../../../model/BFFResponse";
import {
  BaseClientSchedule,
  BulkActionResult,
  BulkActionType,
  ClientBulkUpdateScheduleRequest,
  ClientSchedule,
  ClientScheduleCreateEdit,
  ClientScheduleCreateRequest,
  ClientScheduleEditRequest,
  ClientSchedulePage,
  ScheduleStatus
} from "../model/Schedules";
import { Gateway } from "../../gateways/model/Gateways";

jest.mock("../service/schedule-service");

const app = express();
app.use(express.json());
app.use(router);

describe("Schedule Controller", () => {
  it("should get schedules", async () => {
    const mockScheduleResponse = createMockSchedulePageSuccessResponse();

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        getSchedules: jest.fn().mockResolvedValue(mockScheduleResponse)
      };
    });

    const result = await request(app).get("/v1/bff/schedule");

    expect(result.status).toBe(200);
    expect(result.body).toEqual(mockScheduleResponse.data);
  });

  it("should handle error when getting schedules", async () => {
    const mockError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        getSchedules: jest.fn().mockRejectedValue(mockError)
      };
    });

    await request(app).get("/v1/bff/schedule").expect(400).expect("Bad Request");
  });

  it("should get schedules for bulk action review", async () => {
    const mockScheduleResponse = createMockSchedulePageSuccessResponse();

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        getSchedulesForBulkAction: jest.fn().mockResolvedValue(mockScheduleResponse)
      };
    });

    await request(app).get("/v1/bff/schedule/review").expect(200).expect(mockScheduleResponse.data);
  });


  it("should handle error when getting schedules for bulk review", async () => {
    const mockError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        getSchedulesForBulkAction: jest.fn().mockRejectedValue(mockError)
      };
    });

    await request(app).get("/v1/bff/schedule/review").expect(400).expect("Bad Request");
  });

  it("should return created schedule when creating a new schedule", () => {
    const mockScheduleResponse = createMockScheduleSuccessResponse();


    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        createSchedule: jest.fn().mockResolvedValue(mockScheduleResponse)
      };
    });

    return request(app)
      .post("/v1/bff/schedule")
      .send({
        gatewayId: "1",
        userId: "1",
        status: ScheduleStatus.Running,
        nextRun: "2024-04-03T00:00:00Z",
        lastRun: "2024-04-03T00:00:00Z",
        cronSpec: "0 0 0 0 0",
        scheduleType: "Cron"
      } as ClientScheduleCreateRequest)
      .expect(200)
      .expect(mockScheduleResponse.data);
  });

  it("should handle error when creating a new schedule", () => {
    const mockError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        createSchedule: jest.fn().mockRejectedValue(mockError)
      };
    });

    return request(app)
      .post("/v1/bff/schedule")
      .send({
        gatewayId: "1",
        userId: "1",
        status: ScheduleStatus.Running,
        nextRun: "2024-04-03T00:00:00Z",
        lastRun: "2024-04-03T00:00:00Z",
        cronSpec: "0 0 0 0 0",
        scheduleType: "Cron"
      } as ClientScheduleCreateRequest)
      .expect(400)
      .expect("Bad Request");
  });

  it("should update existing schedule", () => {
    const mockScheduleResponse = createMockScheduleSuccessResponse();

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        updateSchedule: jest.fn().mockResolvedValue(mockScheduleResponse)
      };
    });

    return request(app)
      .put("/v1/bff/schedule")
      .send({
        gatewayId: "1",
        userId: "1",
        status: ScheduleStatus.Running,
        nextRun: "2024-04-03T00:00:00Z",
        lastRun: "2024-04-03T00:00:00Z",
        cronSpec: "0 0 0 0 0",
        scheduleType: "Cron",
        scheduleId: "1"
      } as ClientScheduleEditRequest)
      .expect(200)
      .expect(mockScheduleResponse.data);
  });

  it("should handle error when updating a schedule", () => {
    const mockError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        updateSchedule: jest.fn().mockRejectedValue(mockError)
      };
    });

    return request(app)
      .put("/v1/bff/schedule")
      .send({
        gatewayId: "1",
        userId: "1",
        status: ScheduleStatus.Running,
        nextRun: "2024-04-03T00:00:00Z",
        lastRun: "2024-04-03T00:00:00Z",
        cronSpec: "0 0 0 0 0",
        scheduleType: "Cron",
        scheduleId: "1"
      } as ClientScheduleEditRequest)
      .expect(400)
      .expect("Bad Request");
  });

  it("should bulk update schedules", () => {
    const mockBulkUpdateResponse: BFFResponse<BulkActionResult> = {
      status: 200,
      data: {
        success: true,
        totalModified: 1,
        failureReasons: []
      }
    };

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        bulkUpdateSchedules: jest.fn().mockResolvedValue(mockBulkUpdateResponse)
      };
    });

    return request(app)
      .patch("/v1/bff/schedule")
      .send({
        gatewayId: "1",
        actionType: BulkActionType.Pause,
        paused: true
      } as ClientBulkUpdateScheduleRequest)
      .expect(200)
      .expect(mockBulkUpdateResponse.data);
  });

  it("should return error when performing a bulk update", () => {
    const mockError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        bulkUpdateSchedules: jest.fn().mockRejectedValue(mockError)
      };
    });

    return request(app)
      .patch("/v1/bff/schedule")
      .send({
        gatewayId: "1",
        actionType: BulkActionType.Pause,
        paused: true
      } as ClientBulkUpdateScheduleRequest)
      .expect(400)
      .expect("Bad Request");
  });

  it("should get a schedule by its id", () => {
    const mockScheduleResponse = createMockScheduleSuccessResponse();

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        getScheduleById: jest.fn().mockResolvedValue(mockScheduleResponse)
      };
    });

    return request(app).get("/v1/bff/schedule/1").expect(200).expect(mockScheduleResponse.data);
  });

  it("should return an error when getting a schedule by id", () => {
    const mockError = {
      response: {
        status: 404,
        data: "Not Found"
      }
    };

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        getScheduleById: jest.fn().mockRejectedValue(mockError)
      };
    });

    return request(app).get("/v1/bff/schedule/1").expect(404).expect("Not Found");
  });

  it("should delete schedule by id", () => {
    const mockResponse = {
      status: 200,
      data: {
        success: true
      }
    } as BFFResponse<{ success: boolean }>;

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        deleteScheduleById: jest.fn().mockResolvedValue(mockResponse)
      };
    });

    return request(app).delete("/v1/bff/schedule/1").expect(200).expect(mockResponse.data);
  });

  it("should return an error when deleting schedule by id", () => {
    const mockError = {
      response: {
        status: 404,
        data: "Not Found"
      }
    };

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        deleteScheduleById: jest.fn().mockRejectedValue(mockError)
      };
    });

    return request(app).delete("/v1/bff/schedule/1").expect(404).expect("Not Found");
  });

  it("should be able to pause the schedule", () => {
    const mockResponse = {
      status: 200,
      data: {
        status: ScheduleStatus.Paused,
        gatewayId: "1",
        userId: "1",
        gatewayName: "Test Gateway",
        id: "1",
        cronExpression: "0 0 0 0 0",
        lastRun: "2024-04-03T00:00:00Z",
        nextRun: "2024-04-03T00:00:00Z"
      }
    } as BFFResponse<ClientScheduleCreateEdit>;

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        pauseSchedule: jest.fn().mockResolvedValue(mockResponse)
      };
    });

    return request(app).patch("/v1/bff/schedule/1/pause").expect(200).expect(mockResponse.data);
  });

  it("should handle error when pausing a schedule", () => {
    const mockError = {
      response: {
        status: 404,
        data: "Not Found"
      }
    };

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        pauseSchedule: jest.fn().mockRejectedValue(mockError)
      };
    });

    return request(app).patch("/v1/bff/schedule/1/pause").expect(404).expect("Not Found");
  });

  it("should be able to resume a schedule", () => {
    const mockResponse = {
      status: 200,
      data: {
        status: ScheduleStatus.Running,
        gatewayId: "1",
        userId: "1",
        gatewayName: "Test Gateway",
        id: "1",
        cronExpression: "0 0 0 0 0",
        lastRun: "2024-04-03T00:00:00Z",
        nextRun: "2024-04-03T00:00:00Z"
      }
    } as BFFResponse<ClientScheduleCreateEdit>;

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        resumeSchedule: jest.fn().mockResolvedValue(mockResponse)
      };
    });

    return request(app).patch("/v1/bff/schedule/1/resume").expect(200).expect(mockResponse.data);
  });

  it("should handle error when trying to resume a schedule", () => {
    const mockError = {
      response: {
        status: 404,
        data: "Not Found"
      }
    };

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        resumeSchedule: jest.fn().mockRejectedValue(mockError)
      };
    });

    return request(app).patch("/v1/bff/schedule/1/resume").expect(404).expect("Not Found");
  });

  const createMockScheduleSuccessResponse = (): BFFResponse<ClientSchedule> => {
    return {
      status: 200,
      data: {
        id: "1",
        status: ScheduleStatus.Running,
        gatewayId: "1",
        userId: "1",
        gateway: {
          id: "1",
          friendlyName: "Test Gateway",
          endpointUrl: "http://localhost:8080",
          description: "Test Gateway Description",
          dateCreated: "2024-04-03T00:00:00Z"
        } as Gateway,
        nextRun: "2024-04-03T00:00:00Z",
        lastRun: "2024-04-03T00:00:00Z",
        gatewayName: "Test Gateway",
        nextActionTimes: ["2024-04-03T00:00:00Z", "2024-04-03T00:00:00Z", "2024-04-03T00:00:00Z"],
        createdAt: "2024-04-03T00:00:00Z",
        updatedAt: "2024-04-03T00:00:00Z",
        recentActions: ["2024-04-03T00:00:00Z", "2024-04-03T00:00:00Z", "2024-04-03T00:00:00Z"]
      } as ClientSchedule
    } as BFFResponse<ClientSchedule>;
  };

  const createMockSchedulePageSuccessResponse = (): BFFResponse<ClientSchedulePage> => {
    return {
      status: 200,
      data: {
        schedules: [
          {
            id: "1",
            status: ScheduleStatus.Running,
            gatewayId: "1",
            userId: "1",
            gateway: {
              id: "1",
              friendlyName: "Test Gateway",
              endpointUrl: "http://localhost:8080",
              description: "Test Gateway Description",
              dateCreated: "2024-04-03T00:00:00Z"
            } as Gateway,
            nextRun: "2024-04-03T00:00:00Z",
            lastRun: "2024-04-03T00:00:00Z",
            gatewayName: "Test Gateway"
          } as BaseClientSchedule
        ],
        pageNumber: 1,
        pageSize: 1,
        totalElements: 1
      }
    } as BFFResponse<ClientSchedulePage>;
  };

});
