import express from "express";
import router from "./history-controller";
import { BFFResponse } from "../../../model/BFFResponse";
import { ClientHistoryItem, ClientHistoryPage, StopCommunicationResponse } from "../model/History";
import HistoryService from "../service/history-service";
import request from "supertest";

jest.mock("../service/history-service");

const app = express();
app.use(express.json());
app.use(router);

describe("History Controller", () => {
  it("should get history page", async () => {
    const mockHistoryPageResponse = createMockHistoryPageSuccessResponse();

    (HistoryService as jest.Mock).mockImplementation(() => {
      return {
        getHistoryPage: jest.fn().mockResolvedValue(mockHistoryPageResponse)
      };
    });

    const response = await request(app).get("/v1/bff/history");

    expect(response.status).toBe(200);
    expect(response.body).toEqual(mockHistoryPageResponse.data);
  });

  it("should return error when getting history page", () => {
    const mockError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    (HistoryService as jest.Mock).mockImplementation(() => {
      return {
        getHistoryPage: jest.fn().mockRejectedValue(mockError)
      };
    });

    return request(app).get("/v1/bff/history").expect(400).expect("Bad Request");
  });

  it("should be able to stop communication", async () => {
    const mockDeleteResponse: BFFResponse<StopCommunicationResponse> = {
      status: 204,
      data: "No Content."
    };

    (HistoryService as jest.Mock).mockImplementation(() => {
      return {
        stopCommunication: jest.fn().mockResolvedValue(mockDeleteResponse)
      };
    });

    const result = await request(app).delete("/v1/bff/history/stop/1/1");
    expect(result.status).toBe(204);
  });

  it("should handle error when stopping communication", () => {
    const mockError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    (HistoryService as jest.Mock).mockImplementation(() => {
      return {
        stopCommunication: jest.fn().mockRejectedValue(mockError)
      };
    });

    return request(app).delete("/v1/bff/history/stop/1/1").expect(400).expect("Bad Request");
  });

  const createMockHistoryPageSuccessResponse = (): BFFResponse<ClientHistoryPage> => {
    return {
      status: 200,
      data: {
        historyItems: [
          {
            id: "1",
            gatewayId: "1",
            gatewayName: "Gateway 1",
            scheduleId: "1",
            userId: "1",
            startTime: "2021-01-01T00:00:00",
            endTime: "2021-01-01T00:00:00",
            status: "Completed",
            workflowId: "1"
          } as ClientHistoryItem
        ],
        totalElements: 0,
        pageSize: 25,
        pageNumber: 0
      }
    };

  };
});
