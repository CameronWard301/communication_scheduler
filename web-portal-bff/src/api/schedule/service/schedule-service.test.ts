import { describe } from "@jest/globals";
import { ScheduleService } from "./schedule-service";
import axiosClient from "../../../axios-client";
import MockAdapter from "axios-mock-adapter";
import {
  ActionRun,
  BaseClientSchedule,
  BulkActionResult,
  BulkActionSelectionType,
  BulkActionType,
  ClientBulkUpdateScheduleRequest,
  ClientSchedule,
  ClientScheduleCreateRequest,
  ClientScheduleEditRequest,
  ClientSchedulePage,
  DayOfWeek,
  Month,
  Period,
  Schedule,
  ScheduleBulkActionQueryParams,
  ScheduleQueryParams,
  ScheduleStatus,
  ScheduleTableItem,
  ScheduleType,
  ServerScheduleCreateRequest,
  ServerScheduleEditRequest,
  ServerSchedulePage
} from "../model/Schedules";
import { TotalMatches } from "../../../model/Shared";
import { randomUUID } from "node:crypto";
import { BFFResponse } from "../../../model/BFFResponse";
import { Gateway } from "../../gateways/model/Gateways";

describe("ScheduleService", () => {
  let scheduleService: ReturnType<typeof ScheduleService>;
  let mockAxios = new MockAdapter(axiosClient);


  beforeEach(() => {
    mockAxios.reset();
    scheduleService = ScheduleService();
    process.env.SCHEDULE_API_URL = "http://schedule-api:8080";
    process.env.GATEWAY_API_URL = "http://gateway-api:8080";
  });

  const getDateString = (date: Date) => {
    const userTimezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    return new Intl.DateTimeFormat("en-GB", {
      timeZone: userTimezone,
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
      hour12: false,
      timeZoneName: "short"
    }).format(date);
  };

  const callAndCheckGetSchedules = (scheduleQueryParams: ScheduleQueryParams, expectedBffResponse: BFFResponse<ClientSchedulePage>) => {
    scheduleService.getSchedules("test-token", scheduleQueryParams).then((result) => {
      expect(result.status).toBe(expectedBffResponse.status);
      expect(result.data).toBeDefined();
      expect(result.data).toStrictEqual(expectedBffResponse.data);
    });
  };

  const callGetSchedulesForBulkAction = (params: ScheduleBulkActionQueryParams, expectedBffResponse: BFFResponse<ClientSchedulePage>) => {
    scheduleService.getSchedulesForBulkAction("test-token", params).then((result) => {
      expect(result.status).toBe(expectedBffResponse.status);
      expect(result.data).toBeDefined();
      expect(result.data).toStrictEqual(expectedBffResponse.data);
    });
  };

  const callBulkUpdateSchedules = (updateRequest: ClientBulkUpdateScheduleRequest) => {
    const updateParams: ScheduleBulkActionQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      selectionType: BulkActionSelectionType.QUERY
    };

    const serverBulkUpdateResponse: BulkActionResult = {
      success: true,
      totalModified: 4
    };

    const expectedBffResponse: BFFResponse<BulkActionResult> = {
      status: 200,
      data: serverBulkUpdateResponse
    };

    mockAxios.onPatch(`${process.env.SCHEDULE_API_URL}`).reply(200, serverBulkUpdateResponse);
    bulkUpdate(updateRequest, updateParams, expectedBffResponse);
  };

  const callBulkUpdateForSchedulesById = (bulkUpdateRequest: ClientBulkUpdateScheduleRequest, updateParams: ScheduleBulkActionQueryParams, updateSchedule1Request: {
    scheduleId: string,
    paused: boolean
  }, updateSchedule2Request: { scheduleId: string, paused: boolean }) => {
    const expectedBffResponse: BFFResponse<BulkActionResult> = {
      status: 200,
      data: {
        success: true,
        totalModified: 2
      }
    };

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/test-schedule-id-1`).reply(200, createServerScheduleResponse(updateParams.userId!, updateParams.gatewayId!, ScheduleType.Cron, true, "test-schedule-id-1"));
    mockAxios.onPut(`${process.env.SCHEDULE_API_URL}`, updateSchedule1Request).reply(200, createServerScheduleResponse(updateParams.userId!, updateParams.gatewayId!, ScheduleType.Cron, true, "test-schedule-id-1", true));
    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/test-schedule-id-2`).reply(200, createServerScheduleResponse(updateParams.userId!, updateParams.gatewayId!, ScheduleType.Cron, true, "test-schedule-id-2"));
    mockAxios.onPut(`${process.env.SCHEDULE_API_URL}`, updateSchedule2Request).reply(200, createServerScheduleResponse(updateParams.userId!, updateParams.gatewayId!, ScheduleType.Cron, true, "test-schedule-id-2", true));
    bulkUpdate(bulkUpdateRequest, updateParams, expectedBffResponse);
  };

  const bulkUpdate = (bulkUpdateRequest: ClientBulkUpdateScheduleRequest, updateParams: ScheduleBulkActionQueryParams, expectedBffResponse: BFFResponse<BulkActionResult>) => {
    scheduleService.bulkUpdateSchedules("test-token", bulkUpdateRequest, updateParams).then((result) => {
      expect(result.status).toBe(expectedBffResponse.status);
      expect(result.data).toBeDefined();
      expect(result.data).toStrictEqual(expectedBffResponse.data);
    });
  };

  const callCreateSchedule = (scheduleCreateRequest: ClientScheduleCreateRequest, expectedServerCreateRequest: ServerScheduleCreateRequest) => {
    const gatewayResponse = getServerGatewayResponse(scheduleCreateRequest.gatewayId);

    const createdSchedule = createServerScheduleResponse(scheduleCreateRequest.userId, scheduleCreateRequest.gatewayId, ScheduleType.Interval);

    const expectedBffResponse: BFFResponse<ClientSchedule> = {
      status: 201,
      data: getExpectedClientSchedule(createdSchedule, gatewayResponse, false)
    };

    mockAxios.onPost(`${process.env.SCHEDULE_API_URL}`, expectedServerCreateRequest).reply(201, createdSchedule);
    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${scheduleCreateRequest.gatewayId}`).reply(200, gatewayResponse);

    scheduleService.createSchedule("test-token", scheduleCreateRequest).then((result) => {
      expect(result.status).toBe(201);
      expect(result.data).toBeDefined();
      expect(result.data).toStrictEqual(expectedBffResponse.data);
    });
  };

  it("should get page of schedules when the scheduleId param is null", () => {
    const scheduleQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id"
    } as ScheduleQueryParams;

    const serverScheduleResponse = {
      content: [
        createServerScheduleResponse(scheduleQueryParams.userId!, scheduleQueryParams.gatewayId!, ScheduleType.Interval, false),
        createServerScheduleResponse(scheduleQueryParams.userId!, scheduleQueryParams.gatewayId!, ScheduleType.CalendarWeek),
        createServerScheduleResponse(scheduleQueryParams.userId!, scheduleQueryParams.gatewayId!, ScheduleType.Cron)
      ],
      totalElements: 3,
      size: 0,
      number: 0
    } as ServerSchedulePage;

    const serverGatewayResponse = getServerGatewayResponse(scheduleQueryParams.gatewayId!);

    const scheduleTableItems = serverScheduleResponse.content.map((schedule): ScheduleTableItem => {
      return getExpectedBaseClientSchedule(schedule, serverGatewayResponse);
    });

    const expectedBffResponse: BFFResponse<ClientSchedulePage> = {
      status: 200,
      data: {
        schedules: scheduleTableItems,
        totalElements: serverScheduleResponse.totalElements,
        pageSize: serverScheduleResponse.size,
        pageNumber: serverScheduleResponse.number
      }
    };

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}`).reply(200, serverScheduleResponse);
    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${scheduleQueryParams.gatewayId}`).reply(200, serverGatewayResponse);

    callAndCheckGetSchedules(scheduleQueryParams, expectedBffResponse);

  });

  it("should set the gateway to be Gateway Not found if the id cannot be resolved when getting all gateways with no scheduleId param", () => {
    const scheduleQueryParams = {
      userId: "test-user-id-2",
      gatewayId: "test-gateway-id-2"
    } as ScheduleQueryParams;

    const serverScheduleResponse = {
      content: [
        createServerScheduleResponse(scheduleQueryParams.userId!, scheduleQueryParams.gatewayId!, ScheduleType.Interval),
        createServerScheduleResponse(scheduleQueryParams.userId!, scheduleQueryParams.gatewayId!, ScheduleType.CalendarWeek),
        createServerScheduleResponse(scheduleQueryParams.userId!, scheduleQueryParams.gatewayId!, ScheduleType.Cron)
      ],
      totalElements: 3,
      size: 0,
      number: 0
    } as ServerSchedulePage;

    const scheduleTableItems = serverScheduleResponse.content.map((schedule): ScheduleTableItem => {
      return getExpectedBaseClientSchedule(schedule, null);
    });

    const expectedBffResponse: BFFResponse<ClientSchedulePage> = {
      status: 200,
      data: {
        schedules: scheduleTableItems,
        totalElements: serverScheduleResponse.totalElements,
        pageSize: serverScheduleResponse.size,
        pageNumber: serverScheduleResponse.number
      }
    };

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}`).reply(200, serverScheduleResponse);
    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${scheduleQueryParams.gatewayId}`).reply(404, "Gateway Not Found");
    callAndCheckGetSchedules(scheduleQueryParams, expectedBffResponse);

  });


  it("should return error when getting schedules", () => {
    const scheduleQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id"
    } as ScheduleQueryParams;

    const expectedError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}`).reply(() => Promise.reject(expectedError));

    scheduleService.getSchedules("test-token", scheduleQueryParams).catch((reason) => {
      expect(reason.response.data).toEqual(expectedError.response.data);
      expect(reason.response.status).toBe(expectedError.response.status);
    });
  });

  it("should return schedule by id when getting all schedules with the query parameter set", () => {
    const scheduleQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      scheduleId: "test-schedule-id"
    } as ScheduleQueryParams;

    const serverScheduleResponse = createServerScheduleResponse(scheduleQueryParams.userId!, scheduleQueryParams.gatewayId!, ScheduleType.Interval);
    const serverGatewayResponse = getServerGatewayResponse(scheduleQueryParams.gatewayId!);


    const expectedBffResponse: BFFResponse<ClientSchedulePage> = {
      status: 200,
      data: {
        schedules: [getExpectedClientSchedule(serverScheduleResponse, serverGatewayResponse)],
        totalElements: 1,
        pageSize: 25,
        pageNumber: 0
      }
    };

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/${scheduleQueryParams.scheduleId}`).reply(200, serverScheduleResponse);
    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${scheduleQueryParams.gatewayId}`).reply(200, serverGatewayResponse);

    scheduleService.getSchedules("test-token", scheduleQueryParams).then((result) => {
      expect(result.status).toBe(expectedBffResponse.status);
      expect(result.data).toBeDefined();
      expect(result.data).toStrictEqual(expectedBffResponse.data);
    });
  });

  it("should return an empty page if getting the schedule by id is not found", () => {
    const scheduleQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      scheduleId: "test-schedule-id"
    } as ScheduleQueryParams;

    const expectedBffResponse: BFFResponse<ClientSchedulePage> = {
      status: 200,
      data: {
        schedules: [],
        totalElements: 0,
        pageSize: 25,
        pageNumber: 0
      }
    };

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/${scheduleQueryParams.scheduleId}`).reply(404, "Schedule Not Found");

    scheduleService.getSchedules("test-token", scheduleQueryParams).then((result) => {
      expect(result.status).toBe(expectedBffResponse.status);
      expect(result.data).toBeDefined();
      expect(result.data).toStrictEqual(expectedBffResponse.data);
    });
  });

  it("should throw an error if getting by id produces a non 404 error", () => {
    const scheduleQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      scheduleId: "test-schedule-id"
    } as ScheduleQueryParams;

    const expectedError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/${scheduleQueryParams.scheduleId}`).reply(() => Promise.reject(expectedError));

    scheduleService.getSchedules("test-token", scheduleQueryParams).catch((reason) => {
      expect(reason.response.data).toEqual(expectedError.response.data);
      expect(reason.response.status).toBe(expectedError.response.status);
    });

  });

  it("should return schedules by query for bulk action", () => {
    const params: ScheduleBulkActionQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      selectionType: BulkActionSelectionType.QUERY
    };

    const serverScheduleResponse = {
      content: [
        createServerScheduleResponse(params.userId!, params.gatewayId!, ScheduleType.Interval, false),
        createServerScheduleResponse(params.userId!, params.gatewayId!, ScheduleType.CalendarWeek),
        createServerScheduleResponse(params.userId!, params.gatewayId!, ScheduleType.Cron)
      ],
      totalElements: 3,
      size: 0,
      number: 0
    } as ServerSchedulePage;

    const serverGatewayResponse = getServerGatewayResponse(params.gatewayId!);

    const scheduleTableItems = serverScheduleResponse.content.map((schedule): ScheduleTableItem => {
      return getExpectedBaseClientSchedule(schedule, serverGatewayResponse);
    });

    const expectedBffResponse: BFFResponse<ClientSchedulePage> = {
      status: 200,
      data: {
        schedules: scheduleTableItems,
        totalElements: serverScheduleResponse.totalElements,
        pageSize: serverScheduleResponse.size,
        pageNumber: serverScheduleResponse.number
      }
    };

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}`).reply(200, serverScheduleResponse);
    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${params.gatewayId}`).reply(200, serverGatewayResponse);
    callGetSchedulesForBulkAction(params, expectedBffResponse);

  });

  it("should return schedules for bulk action by ids", () => {
    const params: ScheduleBulkActionQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      selectionType: BulkActionSelectionType.IDs,
      scheduleIds: JSON.stringify(["test-schedule-id-1", "test-schedule-id-2"])
    };

    const serverScheduleResponse1 = createServerScheduleResponse(params.userId!, params.gatewayId!, ScheduleType.Interval, false, "test-schedule-id-1");
    const serverScheduleResponse2 = createServerScheduleResponse(params.userId!, params.gatewayId!, ScheduleType.CalendarWeek, true, "test-schedule-id-2");

    const serverGatewayResponse = getServerGatewayResponse(params.gatewayId!);

    const expectedBffResponse: BFFResponse<ClientSchedulePage> = {
      status: 200,
      data: {
        schedules: [getExpectedClientSchedule(serverScheduleResponse1, serverGatewayResponse), getExpectedClientSchedule(serverScheduleResponse2, serverGatewayResponse)],
        totalElements: 2,
        pageSize: 100,
        pageNumber: 0
      }
    };

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/test-schedule-id-1`).reply(200, serverScheduleResponse1);
    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/test-schedule-id-2`).reply(200, serverScheduleResponse2);
    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${params.gatewayId}`).reply(200, serverGatewayResponse);
    callGetSchedulesForBulkAction(params, expectedBffResponse);
  });


  it("should return only successfully resolved schedules for bulk action by ids", () => {
    const params: ScheduleBulkActionQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      selectionType: BulkActionSelectionType.IDs,
      scheduleIds: JSON.stringify(["test-schedule-id-1", "test-schedule-unknown-id-2"])
    };

    const serverScheduleResponse1 = createServerScheduleResponse(params.userId!, params.gatewayId!, ScheduleType.Interval, false, "test-schedule-id-1");
    const serverScheduleResponse2 = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };
    const serverGatewayResponse = getServerGatewayResponse(params.gatewayId!);

    const expectedBffResponse: BFFResponse<ClientSchedulePage> = {
      status: 200,
      data: {
        schedules: [getExpectedClientSchedule(serverScheduleResponse1, serverGatewayResponse)],
        totalElements: 1,
        pageSize: 100,
        pageNumber: 0
      }
    };

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/test-schedule-id-1`).reply(200, serverScheduleResponse1);
    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/test-schedule-unknown-id-2`).reply(() => Promise.reject(serverScheduleResponse2));
    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${params.gatewayId}`).reply(200, serverGatewayResponse);
    callGetSchedulesForBulkAction(params, expectedBffResponse);
  });

  it("should bulk update schedules gateway", () => {
    const updateRequest: ClientBulkUpdateScheduleRequest = {
      gatewayId: "test-gateway-id",
      actionType: BulkActionType.Gateway
    };
    callBulkUpdateSchedules(updateRequest);

  });

  it("should bulk pause schedules", () => {
    const updateRequest: ClientBulkUpdateScheduleRequest = {
      actionType: BulkActionType.Pause
    };
    callBulkUpdateSchedules(updateRequest);
  });

  it("should bulk delete schedules", () => {
    const updateRequest: ClientBulkUpdateScheduleRequest = {
      actionType: BulkActionType.Delete
    };

    const updateParams: ScheduleBulkActionQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      selectionType: BulkActionSelectionType.QUERY
    };

    const serverBulkUpdateResponse: BulkActionResult = {
      success: true,
      totalModified: 4
    };

    const expectedBffResponse: BFFResponse<BulkActionResult> = {
      status: 200,
      data: serverBulkUpdateResponse
    };

    mockAxios.onDelete(`${process.env.SCHEDULE_API_URL}`).reply(200, serverBulkUpdateResponse);
    scheduleService.bulkUpdateSchedules("test-token", updateRequest, updateParams).then((result) => {
      expect(result.status).toBe(expectedBffResponse.status);
      expect(result.data).toBeDefined();
      expect(result.data).toStrictEqual(expectedBffResponse.data);
    });
  });

  it("should bulk update schedules gateway by schedule IDs", () => {
    const bulkUpdateRequest: ClientBulkUpdateScheduleRequest = {
      gatewayId: "test-gateway-id-update",
      actionType: BulkActionType.Gateway
    };

    const updateParams: ScheduleBulkActionQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      selectionType: BulkActionSelectionType.IDs,
      scheduleIds: JSON.stringify(["test-schedule-id-1", "test-schedule-id-2"])
    };

    const updateSchedule1Request = {
      gatewayId: bulkUpdateRequest.gatewayId,
      paused: false,
      scheduleId: "test-schedule-id-1"
    };
    const updateSchedule2Request = {
      gatewayId: bulkUpdateRequest.gatewayId,
      paused: false,
      scheduleId: "test-schedule-id-2"
    };

    const serverBulkUpdateResponse: BulkActionResult = {
      success: true,
      totalModified: 2
    };

    const expectedBffResponse: BFFResponse<BulkActionResult> = {
      status: 200,
      data: serverBulkUpdateResponse
    };

    mockAxios.onPut(`${process.env.SCHEDULE_API_URL}`, updateSchedule1Request).reply(200, createServerScheduleResponse(updateParams.userId!, bulkUpdateRequest.gatewayId!, ScheduleType.Cron, true, "test-schedule-id-1"));
    mockAxios.onPut(`${process.env.SCHEDULE_API_URL}`, updateSchedule2Request).reply(200, createServerScheduleResponse(updateParams.userId!, bulkUpdateRequest.gatewayId!, ScheduleType.Cron, true, "test-schedule-id-2"));
    scheduleService.bulkUpdateSchedules("test-token", bulkUpdateRequest, updateParams).then((result) => {
      expect(result.status).toBe(expectedBffResponse.status);
      expect(result.data).toBeDefined();
      expect(result.data).toStrictEqual(expectedBffResponse.data);
    });
  });

  it("should return 202 if some updates were not successful when selecting by ids", () => {
    const bulkUpdateRequest: ClientBulkUpdateScheduleRequest = {
      gatewayId: "test-gateway-id-update",
      actionType: BulkActionType.Gateway
    };

    const updateParams: ScheduleBulkActionQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      selectionType: BulkActionSelectionType.IDs,
      scheduleIds: JSON.stringify(["test-schedule-id-1", "test-schedule-id-2"])
    };

    const updateSchedule1Request = {
      gatewayId: bulkUpdateRequest.gatewayId,
      paused: false,
      scheduleId: "test-schedule-id-1"
    };
    const updateSchedule2Request = {
      gatewayId: bulkUpdateRequest.gatewayId,
      paused: false,
      scheduleId: "test-schedule-id-2"
    };

    const serverBulkUpdateResponse: BulkActionResult = {
      success: true,
      totalModified: 1
    };

    const expectedBffResponse: BFFResponse<BulkActionResult> = {
      status: 202,
      data: serverBulkUpdateResponse
    };

    mockAxios.onPut(`${process.env.SCHEDULE_API_URL}`, updateSchedule1Request).reply(200, createServerScheduleResponse(updateParams.userId!, bulkUpdateRequest.gatewayId!, ScheduleType.Cron, true, "test-schedule-id-1"));
    mockAxios.onPut(`${process.env.SCHEDULE_API_URL}`, updateSchedule2Request).reply(() => Promise.reject("Not Found"));
    bulkUpdate(bulkUpdateRequest, updateParams, expectedBffResponse);

  });

  it("should return 400 error if no schedules could be bulk updated", () => {
    const bulkUpdateRequest: ClientBulkUpdateScheduleRequest = {
      gatewayId: "test-gateway-id-update",
      actionType: BulkActionType.Gateway
    };

    const updateParams: ScheduleBulkActionQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      selectionType: BulkActionSelectionType.IDs,
      scheduleIds: JSON.stringify(["test-schedule-id-1", "test-schedule-id-2"])
    };
    const updateSchedule1Request = {
      gatewayId: bulkUpdateRequest.gatewayId,
      paused: false,
      scheduleId: "test-schedule-id-1"
    };
    const updateSchedule2Request = {
      gatewayId: bulkUpdateRequest.gatewayId,
      paused: false,
      scheduleId: "test-schedule-id-2"
    };

    const expectedBffResponse: BFFResponse<BulkActionResult> = {
      status: 400,
      data: {
        success: false,
        totalModified: 0,
        failureReasons: ["Not Found", "Not Found"]
      }
    };

    mockAxios.onPut(`${process.env.SCHEDULE_API_URL}`, updateSchedule1Request).reply(() => Promise.reject("Not Found"));
    mockAxios.onPut(`${process.env.SCHEDULE_API_URL}`, updateSchedule2Request).reply(() => Promise.reject("Not Found"));
    bulkUpdate(bulkUpdateRequest, updateParams, expectedBffResponse);
  });

  it("should bulk pause schedules by ID", () => {
    const bulkUpdateRequest: ClientBulkUpdateScheduleRequest = {
      actionType: BulkActionType.Pause
    };

    const updateParams: ScheduleBulkActionQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      selectionType: BulkActionSelectionType.IDs,
      scheduleIds: JSON.stringify(["test-schedule-id-1", "test-schedule-id-2"])
    };

    const updateSchedule1Request = {
      scheduleId: "test-schedule-id-1",
      paused: true
    };
    const updateSchedule2Request = {
      scheduleId: "test-schedule-id-2",
      paused: true
    };
    callBulkUpdateForSchedulesById(bulkUpdateRequest, updateParams, updateSchedule1Request, updateSchedule2Request);
  });

  it("should bulk run schedules by ID", () => {
    const bulkUpdateRequest: ClientBulkUpdateScheduleRequest = {
      actionType: BulkActionType.Resume
    };

    const updateParams: ScheduleBulkActionQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      selectionType: BulkActionSelectionType.IDs,
      scheduleIds: JSON.stringify(["test-schedule-id-1", "test-schedule-id-2"])
    };

    const updateSchedule1Request = {
      scheduleId: "test-schedule-id-1",
      paused: false
    };
    const updateSchedule2Request = {
      scheduleId: "test-schedule-id-2",
      paused: false
    };
    callBulkUpdateForSchedulesById(bulkUpdateRequest, updateParams, updateSchedule1Request, updateSchedule2Request);
  });

  it("should be able to bulk delete schedules by ID", () => {
    const bulkUpdateRequest: ClientBulkUpdateScheduleRequest = {
      actionType: BulkActionType.Delete
    };

    const updateParams: ScheduleBulkActionQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id",
      selectionType: BulkActionSelectionType.IDs,
      scheduleIds: JSON.stringify(["test-schedule-id-1", "test-schedule-id-2"])
    };

    const expectedBffResponse: BFFResponse<BulkActionResult> = {
      status: 200,
      data: {
        success: true,
        totalModified: 2
      }
    };

    mockAxios.onDelete(`${process.env.SCHEDULE_API_URL}/test-schedule-id-1`).reply(204);
    mockAxios.onDelete(`${process.env.SCHEDULE_API_URL}/test-schedule-id-2`).reply(204);
    scheduleService.bulkUpdateSchedules("test-token", bulkUpdateRequest, updateParams).then((result) => {
      expect(result.status).toBe(expectedBffResponse.status);
      expect(result.data).toBeDefined();
      expect(result.data).toStrictEqual(expectedBffResponse.data);
    });
  });

  it("should return gateway not found when getting schedule by ID when the gateway cannot be resolved", () => {
    const scheduleId = "test-schedule-id";
    const gatewayId = "test-unknown-gateway-id";

    const serverScheduleResponse = createServerScheduleResponse("test-user-id", gatewayId, ScheduleType.Interval, false, scheduleId);

    const expectedBffResponse: BFFResponse<ClientSchedule> = {
      status: 200,
      data: getExpectedClientSchedule(serverScheduleResponse, null)
    };

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/${scheduleId}`).reply(200, serverScheduleResponse);
    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${gatewayId}`).reply(404, "Gateway Not Found");

    scheduleService.getScheduleById("test-token", scheduleId).then((result) => {
      expect(result.status).toBe(expectedBffResponse.status);
      expect(result.data).toBeDefined();
      expect(result.data).toStrictEqual(expectedBffResponse.data);
    });
  });

  it("should throw error if cannot delete by id", () => {
    const scheduleId = "test-schedule-id";
    const expectedError = {
      response: {
        status: 404,
        data: "Not Found"
      }
    };

    mockAxios.onDelete(`${process.env.SCHEDULE_API_URL}/${scheduleId}`).reply(() => Promise.reject(expectedError));

    scheduleService.deleteScheduleById("test-token", scheduleId).catch((reason) => {
      expect(reason.response.data).toEqual("Not Found");
      expect(reason.response.status).toBe(404);
    });
  });

  it("should throw error if updating schedule status fails", () => {
    const scheduleId = "test-schedule-id";

    const scheduleUpdate = {
      scheduleId: scheduleId,
      paused: true
    };

    const expectedError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    mockAxios.onPut(`${process.env.SCHEDULE_API_URL}`, scheduleUpdate).reply(() => Promise.reject(expectedError));

    scheduleService.pauseSchedule("test-token", scheduleId, null).catch((reason) => {
      expect(reason.response.data).toEqual("Bad Request");
      expect(reason.response.status).toBe(400);
    });
  });

  it("should create a new schedule with interval", () => {
    const scheduleCreateRequest: ClientScheduleCreateRequest = {
      gatewayId: "test-gateway-id",
      userId: "test-user-id",
      scheduleType: ScheduleType.Interval,
      intervalSpec: {
        days: "0",
        hours: "0",
        minutes: "1",
        seconds: "10",
        offset: "10",
        offsetPeriod: Period.S
      }
    };

    const expectedServerCreateRequest: ServerScheduleCreateRequest = {
      gatewayId: scheduleCreateRequest.gatewayId,
      userId: scheduleCreateRequest.userId,
      paused: false,
      interval: {
        every: "PT0H1M10S",
        offset: "PT10S"
      }
    };
    callCreateSchedule(scheduleCreateRequest, expectedServerCreateRequest);
  });

  it("should create a new schedule for calendar week", () => {
    const scheduleCreateRequest: ClientScheduleCreateRequest = {
      gatewayId: "test-gateway-id",
      userId: "test-user-id",
      scheduleType: ScheduleType.CalendarWeek,
      calendarWeekSpec: {
        dayOfWeek: DayOfWeek.EveryDay,
        hours: "15",
        minutes: "33"
      }
    };

    const expectedServerCreateRequest: ServerScheduleCreateRequest = {
      gatewayId: scheduleCreateRequest.gatewayId,
      userId: scheduleCreateRequest.userId,
      paused: false,
      calendar: {
        seconds: [
          {
            start: "0",
            end: "0",
            step: "1"
          }
        ],
        minutes: [
          {
            start: "33",
            end: "33",
            step: "1"
          }
        ],
        hour: [
          {
            start: "15",
            end: "15",
            step: "1"
          }
        ],
        dayOfMonth: [
          {
            start: "1",
            end: "31",
            step: "1"
          }
        ],
        month: [
          {
            start: "1",
            end: "12",
            step: "1"
          }
        ],
        year: [],
        dayOfWeek: [
          {
            start: "0",
            end: "6",
            step: "1"
          }
        ],
        comment: ""
      }
    };

    callCreateSchedule(scheduleCreateRequest, expectedServerCreateRequest);
  });

  it("should create a new schedule for calendar month", () => {
    const scheduleCreateRequest: ClientScheduleCreateRequest = {
      gatewayId: "test-gateway-id",
      userId: "test-user-id",
      scheduleType: ScheduleType.CalendarMonth,
      calendarMonthSpec: {
        month: Month.December,
        dayOfMonth: 4,
        hours: 15,
        minutes: 33
      }
    };

    const expectedServerCreateRequest: ServerScheduleCreateRequest = {
      gatewayId: scheduleCreateRequest.gatewayId,
      userId: scheduleCreateRequest.userId,
      paused: false,
      calendar: {
        seconds: [
          {
            start: "0",
            end: "0",
            step: "1"
          }
        ],
        minutes: [
          {
            start: "33",
            end: "33",
            step: "1"
          }
        ],
        hour: [
          {
            start: "15",
            end: "15",
            step: "1"
          }
        ],
        dayOfMonth: [
          {
            start: "4",
            end: "4",
            step: "1"
          }
        ],
        month: [
          {
            start: "12",
            end: "12",
            step: "1"
          }
        ],
        year: [],
        dayOfWeek: [
          {
            start: "0",
            end: "6",
            step: "1"
          }
        ],
        comment: ""
      }
    };

    callCreateSchedule(scheduleCreateRequest, expectedServerCreateRequest);
  });

  it("should create a new schedule for cron string", () => {
    const scheduleCreateRequest: ClientScheduleCreateRequest = {
      gatewayId: "test-gateway-id",
      userId: "test-user-id",
      scheduleType: ScheduleType.Cron,
      cronSpec: "0 12 * * MON"
    };

    const expectedServerCreateRequest: ServerScheduleCreateRequest = {
      gatewayId: scheduleCreateRequest.gatewayId,
      userId: scheduleCreateRequest.userId,
      paused: false,
      cronExpression: "0 12 * * MON"
    };

    callCreateSchedule(scheduleCreateRequest, expectedServerCreateRequest);
  });

  it("should create a new schedule and return gateway not found if the gateway doesn't exist", () => {
    const scheduleCreateRequest: ClientScheduleCreateRequest = {
      gatewayId: "test-gateway-id",
      userId: "test-user-id",
      scheduleType: ScheduleType.Cron,
      cronSpec: "0 12 * * MON"
    };

    const expectedServerCreateRequest: ServerScheduleCreateRequest = {
      gatewayId: scheduleCreateRequest.gatewayId,
      userId: scheduleCreateRequest.userId,
      paused: false,
      cronExpression: "0 12 * * MON"
    };

    const createdSchedule = createServerScheduleResponse(scheduleCreateRequest.userId, scheduleCreateRequest.gatewayId, ScheduleType.Interval);

    const expectedBffResponse: BFFResponse<ClientSchedule> = {
      status: 201,
      data: getExpectedClientSchedule(createdSchedule, null, false, true)
    };

    mockAxios.onPost(`${process.env.SCHEDULE_API_URL}`, expectedServerCreateRequest).reply(201, createdSchedule);
    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${scheduleCreateRequest.gatewayId}`).reply(404, "Gateway Not Found");

    scheduleService.createSchedule("test-token", scheduleCreateRequest).then((result) => {
      expect(result.status).toBe(expectedBffResponse.status);
      expect(result.data).toBeDefined();
      expect(result.data).toStrictEqual(expectedBffResponse.data);
    });
  });

  it("should be able to update schedule with cron string", () => {
    const scheduleUpdateRequest: ClientScheduleEditRequest = {
      scheduleId: "test-schedule-id",
      cronSpec: "0 12 * * MON",
      scheduleType: ScheduleType.Cron
    };

    const expectedServerUpdateRequest: ServerScheduleEditRequest = {
      scheduleId: scheduleUpdateRequest.scheduleId,
      paused: false,
      cronExpression: "0 12 * * MON"
    };

    const updatedSchedule = createServerScheduleResponse("test-user-id", "test-gateway-id", ScheduleType.Cron, true, scheduleUpdateRequest.scheduleId);

    const expectedBffResponse: BFFResponse<ClientSchedule> = {
      status: 200,
      data: getExpectedClientSchedule(updatedSchedule, null, false, true)
    };

    mockAxios.onPut(`${process.env.SCHEDULE_API_URL}`, expectedServerUpdateRequest).reply(200, updatedSchedule);

    scheduleService.updateSchedule("test-token", scheduleUpdateRequest).then((result) => {
      expect(result.status).toBe(expectedBffResponse.status);
      expect(result.data).toBeDefined();
      expect(result.data).toStrictEqual(expectedBffResponse.data);
    });
  });


  it("should throw error if can't create schedule", () => {
    const scheduleCreateRequest: ClientScheduleCreateRequest = {
      gatewayId: "test-gateway-id",
      userId: "test-user-id",
      scheduleType: ScheduleType.Cron,
      cronSpec: "0 12 * * MON"
    };

    const expectedServerCreateRequest: ServerScheduleCreateRequest = {
      gatewayId: scheduleCreateRequest.gatewayId,
      userId: scheduleCreateRequest.userId,
      paused: false,
      cronExpression: "0 12 * * MON"
    };

    const expectedError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    mockAxios.onPost(`${process.env.SCHEDULE_API_URL}`, expectedServerCreateRequest).reply(() => Promise.reject(expectedError));

    scheduleService.createSchedule("test-token", scheduleCreateRequest).catch((reason) => {
      expect(reason.response.data).toEqual("Bad Request");
      expect(reason.response.status).toBe(400);
    });
  });

  it("should get schedule count", () => {
    const scheduleQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id"
    } as ScheduleQueryParams;

    const expectedResponse = {
      total: 100
    } as TotalMatches;

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/count`).reply(200, expectedResponse);

    scheduleService.getScheduleCount("test-token", scheduleQueryParams).then((result) => {
      expect(result.status).toBe(200);
      expect(result.data).toBeDefined();
      expect(result.data).toEqual(expectedResponse);
    });
  });

  it("should return error when getting schedule count for schedules", () => {
    const scheduleQueryParams = {
      userId: "test-user-id",
      gatewayId: "test-gateway-id"
    } as ScheduleQueryParams;

    const expectedError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/count`).reply(() => Promise.reject(expectedError));

    scheduleService.getScheduleCount("test-token", scheduleQueryParams).catch((reason) => {
      expect(reason.response.data).toEqual("Bad Request");
      expect(reason.response.status).toBe(400);
    });
  });

  const createServerScheduleResponse = (userId: string, gatewayId: string, scheduleType: ScheduleType, lastRun = true, scheduleId?: string, paused = false): Schedule => {
    let UUID: string;
    if (scheduleId) {
      UUID = scheduleId;
    } else {
      UUID = randomUUID();
    }
    const schedule = {
      scheduleId: UUID,
      info: {
        numActions: 0,
        numActionsMissedCatchupWindow: 0,
        numActionsSkippedOverlap: 0,
        runningActions: [
          {
            scheduledAt: "2024-04-04T13:15:02.602Z",
            runId: "3fa85f64-5717-4562-b3fc-2c963f66afa6"
          } as ActionRun
        ],
        recentActions: [],
        nextActionTimes: [
          "2024-04-04T13:15:02.602Z"
        ],
        createdAt: "2024-04-04T13:15:02.602Z",
        lastUpdatedAt: "2024-04-04T13:15:02.602Z"
      },
      schedule: {
        state: {
          note: "example-note",
          paused: paused,
          limitedAction: false,
          remainingActions: 0
        },
        spec: {
          intervals: [],
          calendars: [],
          cronExpressions: []
        }
      },
      searchAttributes: {
        userId: [
          userId
        ],
        gatewayId: [
          gatewayId
        ],
        scheduleId: [
          UUID
        ]
      }
    } as Schedule;
    if (lastRun) {
      schedule.info.recentActions = [
        {
          scheduledAt: "2024-01-04T13:15:02.602Z",
          runId: "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        } as ActionRun
      ];
    }

    switch (scheduleType) {
      case ScheduleType.Cron:
        schedule.schedule.spec.cronExpressions = [
          "0 12 * * MON"
        ];
        break;
      case ScheduleType.Interval:
        schedule.schedule.spec.intervals = [
          {
            every: "PT10S",
            offset: "PT10S"
          }
        ];
        break;
      case ScheduleType.CalendarMonth:
      case ScheduleType.CalendarWeek:
        schedule.schedule.spec.calendars = [
          {
            seconds: [
              {
                start: "0",
                end: "0",
                step: "1"
              }
            ],
            minutes: [
              {
                start: "0",
                end: "0",
                step: "1"
              }
            ],
            hour: [
              {
                start: "0",
                end: "0",
                step: "1"
              }
            ],
            dayOfMonth: [
              {
                start: "0",
                end: "0",
                step: "1"
              }
            ],
            month: [
              {
                start: "0",
                end: "0",
                step: "1"
              }
            ],
            year: [
              {
                start: "0",
                end: "0",
                step: "1"
              }
            ],
            dayOfWeek: [
              {
                start: "0",
                end: "0",
                step: "1"
              }
            ],
            comment: "example-comment"
          }
        ];
        break;
    }

    return schedule;
  };

  const getServerGatewayResponse = (gatewayId: string): Gateway => {
    return {
      id: gatewayId,
      endpointUrl: "https://example.com/email",
      friendlyName: "My Email Gateway",
      description: "This is a gateway for sending emails",
      dateCreated: "2024-04-04T13:15:02.602Z"
    } as Gateway;
  };

  const getExpectedGatewayResponse = (gateway: Gateway): Gateway => {
    return {
      id: gateway.id,
      endpointUrl: gateway.endpointUrl,
      friendlyName: gateway.friendlyName,
      description: gateway.description,
      dateCreated: new Date(gateway.dateCreated).toLocaleString()
    } as Gateway;
  };

  const getExpectedBaseClientSchedule = (serverSchedule: Schedule, gateway: Gateway | null, includeGatewayName = true, includeMissingGatewayId = false): BaseClientSchedule => {

    let schedule = {
      id: serverSchedule.scheduleId,
      status: serverSchedule.schedule.state.paused ? ScheduleStatus.Running : ScheduleStatus.Running,
      gatewayId: serverSchedule.searchAttributes.gatewayId[0],
      userId: serverSchedule.searchAttributes.userId[0],
      nextRun: getDateString(new Date(serverSchedule.info.createdAt))
    } as BaseClientSchedule;

    if (serverSchedule.info.recentActions.length == 1) {
      schedule = {
        ...schedule,
        lastRun: getDateString(new Date(serverSchedule.info.recentActions[0].scheduledAt))
      };
    } else {
      schedule = {
        ...schedule,
        lastRun: ""
      };
    }

    if (gateway) {
      schedule = {
        ...schedule,
        gateway: getExpectedGatewayResponse(gateway)
      };
      if (includeGatewayName) {
        schedule = {
          ...schedule,
          gatewayName: gateway.friendlyName
        };
      }
    } else {
      schedule = {
        ...schedule,
        gateway: {
          id: "",
          endpointUrl: "",
          dateCreated: "",
          description: "",
          friendlyName: "Gateway not found"
        } as Gateway
      };
      if (includeGatewayName) {
        schedule = {
          ...schedule,
          gatewayName: "Gateway not found"

        };
      }
      if (includeMissingGatewayId) {
        schedule = {
          ...schedule,
          gateway: {
            ...schedule.gateway,
            id: ""
          } as Gateway
        };

      }
    }
    return schedule;
  };

  const getExpectedClientSchedule = (serverSchedule: Schedule, gateway: Gateway | null, includeGatewayName = true, includeMissingGatewayId = false): ClientSchedule => {
    let schedule = getExpectedBaseClientSchedule(serverSchedule, gateway, includeGatewayName, includeMissingGatewayId) as ClientSchedule;
    schedule = {
      ...schedule,
      createdAt: getDateString(new Date(serverSchedule.info.createdAt)),
      updatedAt: getDateString(new Date(serverSchedule.info.lastUpdatedAt)),
      nextActionTimes: [getDateString(new Date(serverSchedule.info.nextActionTimes[0]))],
      recentActions: []
    };

    if (serverSchedule.info.recentActions.length == 1) {
      schedule = {
        ...schedule,
        recentActions: [getDateString(new Date(serverSchedule.info.recentActions[0].scheduledAt))]
      };
    }

    return schedule;

  };

});
