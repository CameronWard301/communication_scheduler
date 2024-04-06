import express from "express";
import router from "./gateway-controller";
import { ClientGatewayPage, Gateway } from "../model/Gateways";
import { BFFResponse } from "../../../model/BFFResponse";
import GatewayService from "../service/gateway-service";
import request from "supertest";
import { TotalMatches } from "../../../model/Shared";
import { ScheduleService } from "../../schedule/service/schedule-service";

jest.mock("../service/gateway-service");
jest.mock("../../schedule/service/schedule-service");

const app = express();
app.use(express.json());
app.use(router);

describe("Gateway Controller", () => {
  it("should get gateways", async () => {
    const mockGatewayResponse = {
      status: 200,
      data: {
        gateways: [
          {
            id: "1",
            friendlyName: "Test Gateway",
            endpointUrl: "http://localhost:8080",
            description: "Test Gateway Description",
            dateCreated: "2021-01-01T00:00:00Z"
          } as Gateway
        ],
        pageNumber: 1,
        pageSize: 1,
        totalElements: 1
      } as ClientGatewayPage
    } as BFFResponse<ClientGatewayPage>;

    (GatewayService as jest.Mock).mockImplementation(() => {
      return {
        getGateways: jest.fn().mockResolvedValue(mockGatewayResponse)
      };
    });

    const result = await request(app).get("/v1/bff/gateway");

    expect(result.status).toBe(200);
    expect(result.body).toEqual(mockGatewayResponse.data);
  });

  it("should handle error when getting gateways", async () => {
    const mockError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    (GatewayService as jest.Mock).mockImplementation(() => {
      return {
        getGateways: jest.fn().mockRejectedValue(mockError)
      };
    });

    await request(app).get("/v1/bff/gateway").expect(400).expect("Bad Request");
  });

  it("should get gateway by id", async () => {
    const mockGatewayResponse = {
      status: 200,
      data: {
        id: "1",
        friendlyName: "Test Gateway",
        endpointUrl: "http://localhost:8080",
        description: "Test Gateway Description",
        dateCreated: "2021-01-01T00:00:00Z"
      } as Gateway
    } as BFFResponse<Gateway>;

    (GatewayService as jest.Mock).mockImplementation(() => {
      return {
        getGatewayById: jest.fn().mockResolvedValue(mockGatewayResponse)
      };
    });

    await request(app).get("/v1/bff/gateway/1").expect(200).expect(mockGatewayResponse.data);
  });

  it("should handle error when getting gateway by id", async () => {
    const mockError = {
      response: {
        status: 404,
        data: "Not Found"
      }
    };

    (GatewayService as jest.Mock).mockImplementation(() => {
      return {
        getGatewayById: jest.fn().mockRejectedValue(mockError)
      };
    });

    await request(app).get("/v1/bff/gateway/2").expect(404).expect("Not Found");
  });

  it("should get schedule count by gateway id", async () => {
    const mockScheduleCountResponse = {
      status: 200,
      data: {
        total: 10
      } as TotalMatches
    } as BFFResponse<TotalMatches>;

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        getScheduleCount: jest.fn().mockResolvedValue(mockScheduleCountResponse)
      };
    });

    await request(app).get("/v1/bff/gateway/1/schedule/count").expect(200).expect(mockScheduleCountResponse.data);
  });

  it("should return error when getting schedule count by gateway id", () => {
    const mockError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    (ScheduleService as jest.Mock).mockImplementation(() => {
      return {
        getScheduleCount: jest.fn().mockRejectedValue(mockError)
      };
    });

    return request(app).get("/v1/bff/gateway/2/schedule/count").expect(400).expect("Bad Request");
  });

  it("should create gateway", () => {
    const mockGateway = {
      friendlyName: "Test Gateway",
      endpointUrl: "http://localhost:8080",
      description: "Test Gateway Description"
    } as Gateway;

    const mockGatewayResponse = {
      status: 201,
      data: {
        id: "1",
        friendlyName: mockGateway.friendlyName,
        endpointUrl: mockGateway.endpointUrl,
        description: mockGateway.description,
        dateCreated: "2021-01-01T00:00:00Z"
      } as Gateway
    } as BFFResponse<Gateway>;

    (GatewayService as jest.Mock).mockImplementation(() => {
      return {
        createGateway: jest.fn().mockResolvedValue(mockGatewayResponse)
      };
    });

    return request(app).post("/v1/bff/gateway").send(mockGateway).expect(201).expect(mockGatewayResponse.data);
  });

  it("should return error when creating gateway", () => {
    const mockGateway = {
      friendlyName: "Test Gateway",
      description: "Test Gateway Description"
    } as Gateway;

    const mockError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    (GatewayService as jest.Mock).mockImplementation(() => {
      return {
        createGateway: jest.fn().mockRejectedValue(mockError)
      };
    });

    return request(app).post("/v1/bff/gateway").send(mockGateway).expect(400).expect("Bad Request");
  });

  it("should update gateway", () => {
    const mockGateway = {
      id: "1",
      friendlyName: "Test Gateway",
      endpointUrl: "http://localhost:8080",
      description: "Test Gateway Description"
    } as Gateway;

    const mockGatewayResponse = {
      status: 200,
      data: {
        id: mockGateway.id,
        friendlyName: mockGateway.friendlyName,
        endpointUrl: mockGateway.endpointUrl,
        description: mockGateway.description,
        dateCreated: "2021-01-01T00:00:00Z"
      } as Gateway
    } as BFFResponse<Gateway>;

    (GatewayService as jest.Mock).mockImplementation(() => {
      return {
        updateGateway: jest.fn().mockResolvedValue(mockGatewayResponse)
      };
    });

    return request(app).put("/v1/bff/gateway").send(mockGateway).expect(200).expect(mockGatewayResponse.data);
  });

  it("should return error when updating gateway", () => {
    const mockGateway = {
      id: "1",
      friendlyName: "Test Gateway",
      description: "Test Gateway Description"
    } as Gateway;

    const mockError = {
      response: {
        status: 404,
        data: "Not Found"
      }
    };

    (GatewayService as jest.Mock).mockImplementation(() => {
      return {
        updateGateway: jest.fn().mockRejectedValue(mockError)
      };
    });

    return request(app).put("/v1/bff/gateway").send(mockGateway).expect(404).expect("Not Found");
  });

  it("should delete gateway by id", () => {
    const mockGatewayResponse = {
      status: 204,
      data: undefined
    } as BFFResponse<undefined>;

    (GatewayService as jest.Mock).mockImplementation(() => {
      return {
        deleteGatewayById: jest.fn().mockResolvedValue(mockGatewayResponse)
      };
    });

    return request(app).delete("/v1/bff/gateway/1").expect(204);
  });

  it("should return error when deleting gateway by id", () => {
    const mockError = {
      response: {
        status: 404,
        data: "Not Found"
      }
    };

    (GatewayService as jest.Mock).mockImplementation(() => {
      return {
        deleteGatewayById: jest.fn().mockRejectedValue(mockError)
      };
    });

    return request(app).delete("/v1/bff/gateway/2").expect(404).expect("Not Found");
  });
});
