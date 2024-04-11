import HistoryService from "./history-service";
import MockAdapter from "axios-mock-adapter";
import axiosClient from "../../../axios-client";
import {
  ClientHistoryItem,
  ClientHistoryPage,
  ClientHistoryPageQuery,
  ServerHistoryItem,
  ServerHistoryPage
} from "../model/History";
import { Gateway } from "../../gateways/model/Gateways";
import { getDateString } from "../../../helper/date-converter";


describe("History Service", () => {
  let historyService: ReturnType<typeof HistoryService>;
  let mockAxios = new MockAdapter(axiosClient);

  beforeEach(() => {
    mockAxios.reset();
    historyService = HistoryService();
    process.env.SCHEDULE_API_URL = "http://schedule-api:8080";
    process.env.GATEWAY_API_URL = "http://gateway-api:8080";
  });

  const getAndCheckHistoryPage = (mockServerHistoryResponse: ServerHistoryPage, expectedResponse: ClientHistoryPage, mockGatewayResponse: Gateway, query: ClientHistoryPageQuery) => {
    mockAxios.onGet(`${process.env.HISTORY_API_URL as string}`).reply(200, mockServerHistoryResponse);
    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${mockServerHistoryResponse.content[0].gatewayId}`).reply(200, mockGatewayResponse);


    historyService.getHistoryPage("test-token", query).then((response) => {
      expect(response.status).toBe(200);
      expect(response.data).toEqual(expectedResponse);
    });
  }

  it("should get a page of history items when there are no filters", () => {
    const query: ClientHistoryPageQuery = {
      pageSize: "10",
      pageNumber: "0"
    };

    const mockServerHistoryResponse: ServerHistoryPage = {
      content: [
        {
          runId: "test-run-id",
          gatewayId: "test-gateway-id",
          status: 1,
          taskQueue: "test-task-queue-id",
          workflowId: "test-workflow-id",
          type: "test-type",
          userId: "test-user-id",
          scheduleId: "test-schedule-id",
          startTime: {
            seconds: 1,
            nanos: 1
          },
          endTime: {
            seconds: 30,
            nanos: 30
          }
        } as ServerHistoryItem
      ],
      totalElements: 1,
      size: Number(query.pageSize),
      number: Number(query.pageNumber)
    };

    const mockGatewayResponse: Gateway = {
      id: mockServerHistoryResponse.content[0].gatewayId,
      friendlyName: "test-gateway-name",
      description: "test-gateway-description",
      dateCreated: "2023-01-01T00:00:00Z",
      endpointUrl: "test-gateway-url"

    };

    const expectedResponse: ClientHistoryPage = {
      pageNumber: mockServerHistoryResponse.number,
      pageSize: mockServerHistoryResponse.size,
      totalElements: mockServerHistoryResponse.totalElements,
      historyItems: [
        {
          id: mockServerHistoryResponse.content[0].runId,
          status: "Running",
          workflowId: mockServerHistoryResponse.content[0].workflowId,
          scheduleId: mockServerHistoryResponse.content[0].scheduleId,
          userId: mockServerHistoryResponse.content[0].userId,
          gatewayId: mockServerHistoryResponse.content[0].gatewayId,
          startTime: getDateString(getDateFromSecondsAndNanos(mockServerHistoryResponse.content[0].startTime.seconds, mockServerHistoryResponse.content[0].startTime.nanos)),
          endTime: getDateString(getDateFromSecondsAndNanos(mockServerHistoryResponse.content[0].endTime!.seconds, mockServerHistoryResponse.content[0].endTime!.nanos)),
          gatewayName: mockGatewayResponse.friendlyName
        } as ClientHistoryItem
      ]
    };

    getAndCheckHistoryPage(mockServerHistoryResponse, expectedResponse, mockGatewayResponse, query)

  });


  it("should get a page of history items when there are filters", () => {
    const query: ClientHistoryPageQuery = {
      pageSize: "10",
      pageNumber: "0",
      status: "Completed",
      gatewayId: "test-gateway-id",
      scheduleId: "test-schedule-id",
      userId: "test-user-id"
    };

    const mockServerHistoryResponse: ServerHistoryPage = {
      content: [
        {
          runId: "test-run-id",
          gatewayId: "test-gateway-id",
          status: 2,
          taskQueue: "test-task-queue-id",
          workflowId: "test-workflow-id",
          type: "test-type",
          userId: "test-user-id",
          scheduleId: "test-schedule-id",
          startTime: {
            seconds: 1,
            nanos: 1
          },
          endTime: {
            seconds: 30,
            nanos: 30
          }
        } as ServerHistoryItem
      ],
      totalElements: 1,
      size: Number(query.pageSize),
      number: Number(query.pageNumber)
    };

    const mockGatewayResponse: Gateway = {
      id: mockServerHistoryResponse.content[0].gatewayId,
      friendlyName: "test-gateway-name",
      description: "test-gateway-description",
      dateCreated: "2023-01-01T00:00:00Z",
      endpointUrl: "test-gateway-url"

    };

    const expectedResponse: ClientHistoryPage = {
      pageNumber: mockServerHistoryResponse.number,
      pageSize: mockServerHistoryResponse.size,
      totalElements: mockServerHistoryResponse.totalElements,
      historyItems: [
        {
          id: mockServerHistoryResponse.content[0].runId,
          status: "Completed",
          workflowId: mockServerHistoryResponse.content[0].workflowId,
          scheduleId: mockServerHistoryResponse.content[0].scheduleId,
          userId: mockServerHistoryResponse.content[0].userId,
          gatewayId: mockServerHistoryResponse.content[0].gatewayId,
          startTime: getDateString(getDateFromSecondsAndNanos(mockServerHistoryResponse.content[0].startTime.seconds, mockServerHistoryResponse.content[0].startTime.nanos)),
          endTime: getDateString(getDateFromSecondsAndNanos(mockServerHistoryResponse.content[0].endTime!.seconds, mockServerHistoryResponse.content[0].endTime!.nanos)),
          gatewayName: mockGatewayResponse.friendlyName
        } as ClientHistoryItem
      ]
    };

    getAndCheckHistoryPage(mockServerHistoryResponse, expectedResponse, mockGatewayResponse, query)
  });

  it("should return error if thrown when resolving gateway ids", () => {
    const query: ClientHistoryPageQuery = {
      pageSize: "10",
      pageNumber: "0"
    };

    const mockServerHistoryResponse: ServerHistoryPage = {
      content: [
        {
          runId: "test-run-id",
          gatewayId: "test-gateway-id",
          status: 1,
          taskQueue: "test-task-queue-id",
          workflowId: "test-workflow-id",
          type: "test-type",
          userId: "test-user-id",
          scheduleId: "test-schedule-id",
          startTime: {
            seconds: 1,
            nanos: 1
          },
          endTime: {
            seconds: 30,
            nanos: 30
          }
        } as ServerHistoryItem
      ],
      totalElements: 1,
      size: Number(query.pageSize),
      number: Number(query.pageNumber)
    };

    mockAxios.onGet(`${process.env.HISTORY_API_URL as string}`).reply(200, mockServerHistoryResponse);
    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${mockServerHistoryResponse.content[0].gatewayId}`).reply(404, {});

    historyService.getHistoryPage("test-token", query).catch((error) => {
      expect(error.response.status).toBe(404);
    });
  });

  it("should throw error if calling the history api throws an error", () => {
    const query: ClientHistoryPageQuery = {
      pageSize: "10",
      pageNumber: "0"
    };

    mockAxios.onGet(`${process.env.HISTORY_API_URL as string}`).reply(500, {});

    historyService.getHistoryPage("test-token", query).catch((error) => {
      expect(error.response.status).toBe(500);
    });
  });

  it("should stop workflow", () => {
    const workflowId = "test-workflow-id";
    const runId = "test-run-id";
    const mockResponse = {
      status: 204,
      data: "No Content."
    };

    mockAxios.onDelete(`${process.env.HISTORY_API_URL as string}/${workflowId}/${runId}`).reply(204, "No Content.");

    historyService.stopCommunication("test-token", workflowId, runId).then((response) => {
      expect(response).toEqual(mockResponse);
    });
  });

  it("should throw error when trying to stop communication", () => {
    const workflowId = "test-workflow-id";
    const runId = "test-run-id";

    mockAxios.onDelete(`${process.env.HISTORY_API_URL as string}/${workflowId}/${runId}`).reply(400, "Bad Request");

    historyService.stopCommunication("test-token", workflowId, runId).catch((error) => {
      expect(error.response.status).toBe(400);
      expect(error.response.data).toBe("Bad Request");
    });
  });

  const getDateFromSecondsAndNanos = (seconds: number, nanos: number): Date => {
    const milliseconds = seconds * 1000 + nanos / 1e6;
    return new Date(milliseconds);
  }
});
