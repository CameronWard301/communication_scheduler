import {ScheduleService} from "./schedule-service";
import axiosClient from "../../../axios-client";
import MockAdapter from "axios-mock-adapter";
import {ScheduleQueryParams} from "../model/Schedules";
import {TotalMatches} from "../../../model/Shared";

describe('ScheduleService', () => {
  let scheduleService: ReturnType<typeof ScheduleService>;
  let mockAxios = new MockAdapter(axiosClient);

  beforeEach(() => {
    mockAxios.reset();
    scheduleService = ScheduleService();
  });

  it('should get schedule count', () => {
    const scheduleQueryParams = {
      userId: 'test-user-id',
      gatewayId: 'test-gateway-id',
    } as ScheduleQueryParams;

    const expectedResponse = {
      total: 100
    } as TotalMatches;

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/count`).reply(200, expectedResponse);

    scheduleService.getScheduleCount('test-token', scheduleQueryParams).then((result) => {
      expect(result.status).toBe(200);
      expect(result.data).toBeDefined();
      expect(result.data).toEqual(expectedResponse);
    });
  });

  it('should return error when getting schedule count for schedules', () => {
    const scheduleQueryParams = {
      userId: 'test-user-id',
      gatewayId: 'test-gateway-id',
    } as ScheduleQueryParams;

    const expectedError = {
      response: {
        status: 400,
        data: 'Bad Request',
      }
    };

    mockAxios.onGet(`${process.env.SCHEDULE_API_URL}/count`).reply(() => Promise.reject({
      response: {
        status: 400,
        data: 'Bad Request',
      }
    }));

    scheduleService.getScheduleCount('test-token', scheduleQueryParams).catch((reason) => {
      expect(reason.response.data).toEqual("Bad Request");
      expect(reason.response.status).toBe(400);
    });
  });

});
