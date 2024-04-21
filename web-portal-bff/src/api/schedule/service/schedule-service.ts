import {
  BaseClientSchedule,
  BulkActionResult,
  BulkActionSelectionType,
  ClientBulkUpdateScheduleRequest,
  ClientSchedule,
  ClientScheduleCreateEdit,
  ClientScheduleCreateRequest,
  ClientScheduleEditRequest,
  ClientSchedulePage,
  Schedule,
  ScheduleBulkActionQueryParams,
  SchedulePageQueryParams,
  ScheduleQueryParams,
  ScheduleStatus,
  ServerScheduleCreateEdit,
  ServerScheduleCreateRequest,
  ServerScheduleEditRequest,
  ServerSchedulePage
} from "../model/Schedules";
import axiosClient from "../../../axios-client";
import extractAuthToken from "../../../helper/extract-auth-token";
import { TotalMatches } from "../../../model/Shared";
import { BFFResponse } from "../../../model/BFFResponse";
import GatewayService from "../../gateways/service/gateway-service";
import { Gateway } from "../../gateways/model/Gateways";
import { ScheduleSpecService } from "./schedule-spec-service";
import { getDateString } from "../../../helper/date-converter";

export const ScheduleService = () => {

  const convertToBaseClientSchedule = (schedule: Schedule, gateway: Gateway): BaseClientSchedule => {
    const formattedNextRunDate = getDateString(new Date(schedule.info.nextActionTimes[0]));
    let formattedLastRunDate;

    if (schedule.info.recentActions.length === 0) {
      formattedLastRunDate = "";
    } else {
      formattedLastRunDate = getDateString(new Date(schedule.info.recentActions[0].scheduledAt));
    }

    return {
      id: schedule.scheduleId,
      status: schedule.schedule.state.paused ? ScheduleStatus.Paused : ScheduleStatus.Running,
      gatewayName: gateway.friendlyName,
      gateway: gateway,
      gatewayId: schedule.searchAttributes.gatewayId ? schedule.searchAttributes.gatewayId[0] : "0",
      userId: schedule.searchAttributes.userId ? schedule.searchAttributes.userId[0] : "0",
      nextRun: formattedNextRunDate,
      lastRun: formattedLastRunDate
    };
  };

  const getScheduleById = async (token: string | undefined, scheduleId: string): Promise<BFFResponse<ClientSchedule>> => {
    return await axiosClient.get(`${process.env.SCHEDULE_API_URL as string}/${scheduleId}`, {
      headers: extractAuthToken(token)

    }).then(async (value) => {
      const serverSchedule = value.data as Schedule;
      let gateway;
      try {
        gateway = await GatewayService().getGatewayById(token, serverSchedule.searchAttributes.gatewayId[0]);
      } catch (e) {
        gateway = {
          data: {
            id: "",
            dateCreated: "",
            description: "",
            endpointUrl: "",
            friendlyName: "Gateway not found"
          } as Gateway
        };
      }
      const baseSchedule = convertToBaseClientSchedule(serverSchedule, gateway.data as Gateway);

      return {
        status: value.status,
        data: {
          ...baseSchedule,
          createdAt: getDateString(new Date(serverSchedule.info.createdAt)),
          updatedAt: serverSchedule.info.lastUpdatedAt ? getDateString(new Date(serverSchedule.info.lastUpdatedAt)) : "",
          nextActionTimes: serverSchedule.info.nextActionTimes.map((action) => getDateString(new Date(action))),
          recentActions: serverSchedule.info.recentActions.map((action) => getDateString(new Date(action.scheduledAt)))
        } as ClientSchedule
      };

    }).catch((reason) => {
      throw reason;
    });
  };

  const getSchedules = async (token: string | undefined, params: SchedulePageQueryParams): Promise<BFFResponse<ClientSchedulePage>> => {
    if (params.scheduleId === undefined) {
      return await axiosClient.get(process.env.SCHEDULE_API_URL as string, {
        headers: extractAuthToken(token),
        params: params

      }).then(async (value) => {
        const serverSchedulePage = value.data as ServerSchedulePage;

        const gatewayIds = [...new Set(serverSchedulePage.content.map((scheduleItem) => scheduleItem.searchAttributes.gatewayId[0]))];
        return await GatewayService().resolveGatewayIds(token, gatewayIds).then((gateways) => {
          return {
            status: value.status,
            data: {
              schedules: serverSchedulePage.content.map((schedule) => {
                return convertToBaseClientSchedule(schedule, gateways[schedule.searchAttributes.gatewayId[0]]);
              }),
              totalElements: serverSchedulePage.totalElements,
              pageSize: serverSchedulePage.size,
              pageNumber: serverSchedulePage.number
            } as ClientSchedulePage
          };
        });

      }).catch((reason) => {
        throw reason;
      });
    } else {
      return await getScheduleById(token, params.scheduleId).then((value) => {
        return {
          status: value.status,
          data: {
            schedules: [value.data],
            totalElements: 1,
            pageSize: 25,
            pageNumber: 0
          } as ClientSchedulePage
        };
      }).catch((reason) => {
        if (reason.response.status === 404) {
          return {
            status: 200,
            data: {
              schedules: [],
              totalElements: 0,
              pageSize: 25,
              pageNumber: 0
            } as ClientSchedulePage
          };
        }
        throw reason;
      });
    }
  };

  const getSchedulesForBulkAction = async (token: string | undefined, params: ScheduleBulkActionQueryParams) => {
    if (params.selectionType == BulkActionSelectionType.QUERY) {
      return await getSchedules(token, params);
    }

    const scheduleIds = JSON.parse(params.scheduleIds!);
    const schedulePromises = scheduleIds.map((scheduleId: string) => getScheduleById(token, scheduleId));
    const scheduleResults = await Promise.allSettled(schedulePromises);
    const schedules = scheduleResults
      .filter((result) => result.status === "fulfilled")
      .map((result) => (result as PromiseFulfilledResult<BFFResponse<ClientSchedule>>).value.data);

    return {
      status: 200,
      data: {
        schedules: schedules,
        totalElements: schedules.length,
        pageSize: schedules.length > 100 ? schedules.length : 100,
        pageNumber: 0
      } as ClientSchedulePage
    };
  };

  const bulkUpdateSchedules = async (token: string | undefined, updateRequest: ClientBulkUpdateScheduleRequest, params: ScheduleBulkActionQueryParams): Promise<BFFResponse<BulkActionResult>> => {
    if (params.selectionType == BulkActionSelectionType.QUERY) {
      let updateData = {};
      switch (updateRequest.actionType) {
        case "Update Gateway":
          updateData = { gatewayId: updateRequest.gatewayId };
          break;
        case "Pause":
        case "Resume":
          updateData = { paused: updateRequest.actionType === "Pause" };
          break;
        case "Delete":
          return await axiosClient.delete(process.env.SCHEDULE_API_URL as string, {
            headers: extractAuthToken(token),
            params: params
          }).then(result => ({
            status: result.status,
            data: { success: true, totalModified: result.data.totalModified } as BulkActionResult
          }));
      }
      return await axiosClient.patch(process.env.SCHEDULE_API_URL as string, updateData, {
        headers: extractAuthToken(token),
        params: params
      }).then(result => ({
        status: result.status,
        data: { success: true, totalModified: result.data.totalModified } as BulkActionResult
      }));
    } else {
      const scheduleIds = JSON.parse(params.scheduleIds!);
      const bulkUpdatePromises = scheduleIds.map((scheduleId: string) => {

        switch (updateRequest.actionType) {
          case "Update Gateway":
            return updateSchedule(token, { scheduleId: scheduleId, gatewayId: updateRequest.gatewayId! });
          case "Pause":
            return pauseSchedule(token, scheduleId, null);
          case "Resume":
            return resumeSchedule(token, scheduleId, null);
          case "Delete":
            return deleteScheduleById(token, scheduleId);
        }
      });
      const bulkUpdateResults = await Promise.allSettled(bulkUpdatePromises);

      //if all have been rejected:
      if (bulkUpdateResults.every((result) => result.status === "rejected")) {
        return {
          status: 400,
          data: {
            success: false,
            totalModified: 0,
            failureReasons: bulkUpdateResults.map((result) => (result as PromiseRejectedResult).reason)
          } as BulkActionResult
        };
      }

      const status = bulkUpdateResults.some((result) => result.status === "rejected") ? 202 : 200;
      const successfulUpdates = bulkUpdateResults.filter((result) => result.status === "fulfilled");
      return {
        status: status,
        data: {
          success: true,
          totalModified: successfulUpdates.length
        } as BulkActionResult
      };

    }

  };


  const deleteScheduleById = async (token: string | undefined, scheduleId: string): Promise<BFFResponse<{
    status: number;
    data: never
  }>> => {
    return await axiosClient.delete(`${process.env.SCHEDULE_API_URL as string}/${scheduleId}`, {
      headers: extractAuthToken(token)
    }).then((value) => {
      return {
        status: value.status,
        data: value.data
      };
    }).catch((reason) => {
      throw reason;
    });
  };

  const updateScheduleStatus = async (token: string | undefined, scheduleId: string, paused: boolean, gateway: Gateway | null): Promise<BFFResponse<ClientScheduleCreateEdit>> => {
    const scheduleUpdate = {
      scheduleId: scheduleId,
      paused: paused
    } as ServerScheduleCreateEdit;


    // eslint-disable-next-line no-useless-catch
    try {
      const updatedSchedule = await axiosClient.put(`${process.env.SCHEDULE_API_URL as string}`, scheduleUpdate, {
        headers: extractAuthToken(token)
      });


      const updatedScheduleData = updatedSchedule.data as Schedule;
      return {
        status: updatedSchedule.status,
        data: {
          id: updatedScheduleData.scheduleId,
          calendar: updatedScheduleData.schedule.spec.calendars[0],
          interval: updatedScheduleData.schedule.spec.intervals[0],
          cronExpression: updatedScheduleData.schedule.spec.cronExpressions[0],
          status: updatedScheduleData.schedule.state.paused ? ScheduleStatus.Paused : ScheduleStatus.Running,
          gatewayId: updatedScheduleData.searchAttributes.gatewayId[0],
          userId: updatedScheduleData.searchAttributes.userId[0],
          gatewayName: gateway?.friendlyName || "Gateway not loaded",
          nextRun: getDateString(new Date(updatedScheduleData.info.nextActionTimes[0])),
          lastRun: updatedScheduleData.info.recentActions.length > 0 ? getDateString(new Date(updatedScheduleData.info.recentActions[0].scheduledAt)) : ""
        } as ClientScheduleCreateEdit
      };
    } catch (reason) {
      throw reason;
    }

  };

  const pauseSchedule = async (token: string | undefined, scheduleId: string, gateway: Gateway | null): Promise<BFFResponse<ClientScheduleCreateEdit>> => {
    return await updateScheduleStatus(token, scheduleId, true, gateway);
  };

  const resumeSchedule = async (token: string | undefined, scheduleId: string, gateway: Gateway | null): Promise<BFFResponse<ClientScheduleCreateEdit>> => {
    return await updateScheduleStatus(token, scheduleId, false, gateway);
  };


  const getScheduleCount = async (token: string | undefined, params: ScheduleQueryParams) => {
    return await axiosClient.get(`${process.env.SCHEDULE_API_URL as string}/count`, {
      headers: extractAuthToken(token),
      params: params

    }).then((value) => {
      const totalMatches = value.data as TotalMatches;
      return {
        status: value.status,
        data: totalMatches
      };

    }).catch((reason) => {
      throw reason;
    });
  };

  const setScheduleSpec = (serverSchedule: ServerScheduleCreateRequest, clientSchedule: ClientScheduleCreateRequest) => {
    switch (clientSchedule.scheduleType) {
      case "Interval":
        serverSchedule.interval = ScheduleSpecService().getIntervalSpec(clientSchedule.intervalSpec!);
        break;
      case "CalendarWeek":
        serverSchedule.calendar = ScheduleSpecService().getCalendarWeekSpec(clientSchedule.calendarWeekSpec!);
        break;
      case "CalendarMonth":
        serverSchedule.calendar = ScheduleSpecService().getCalendarMonthSpec(clientSchedule.calendarMonthSpec!);
        break;
      case "Cron":
        serverSchedule.cronExpression = clientSchedule.cronSpec!;
        break;
    }
    return serverSchedule;
  };

  const createSchedule = async (authorization: string | undefined, schedule: ClientScheduleCreateRequest) => {
    let scheduleRequest: ServerScheduleCreateRequest = {
      gatewayId: schedule.gatewayId,
      userId: schedule.userId,
      paused: false
    };
    scheduleRequest = setScheduleSpec(scheduleRequest, schedule);

    return axiosClient.post(`${process.env.SCHEDULE_API_URL as string}`, scheduleRequest, {
      headers: extractAuthToken(authorization)
    }).then(async (value) => {

      let gateway: BFFResponse<Gateway>;
      try {
        gateway = await GatewayService().getGatewayById(authorization, scheduleRequest.gatewayId);
      } catch (e) {
        gateway = {
          data: {
            friendlyName: "Gateway not found",
            id: "",
            endpointUrl: "",
            dateCreated: "",
            description: ""
          } as Gateway
        } as BFFResponse<Gateway>;
      }

      const schedule = value.data as Schedule;
      return createResponse(value.status, schedule, gateway.data);
    }).catch((reason) => {
      throw reason;
    });
  };

  const updateSchedule = async (authorization: string | undefined, schedule: ClientScheduleEditRequest) => {
    let scheduleRequest: ServerScheduleEditRequest = {
      gatewayId: schedule.gatewayId,
      userId: schedule.userId,
      paused: false,
      scheduleId: schedule.scheduleId
    };
    let scheduleCreateRequest = {} as ServerScheduleCreateRequest;
    if (schedule.scheduleType !== undefined) {
      scheduleCreateRequest = setScheduleSpec({
        gatewayId: schedule.gatewayId,
        userId: schedule.userId,
        paused: false
      } as ServerScheduleCreateRequest, {
        scheduleType: schedule.scheduleType,
        cronSpec: schedule.cronSpec,
        calendarWeekSpec: schedule.calendarWeekSpec,
        calendarMonthSpec: schedule.calendarMonthSpec,
        intervalSpec: schedule.intervalSpec
      } as ClientScheduleCreateRequest);
    }

    scheduleRequest = {
      ...scheduleRequest,
      interval: scheduleCreateRequest.interval,
      calendar: scheduleCreateRequest.calendar,
      cronExpression: scheduleCreateRequest.cronExpression
    } as ServerScheduleEditRequest;


    return axiosClient.put(`${process.env.SCHEDULE_API_URL as string}`, scheduleRequest, {
      headers: extractAuthToken(authorization)
    }).then(async (value) => {
      const schedule = value.data as Schedule;
      let gateway: BFFResponse<Gateway>;
      try {
        gateway = await GatewayService().getGatewayById(authorization, schedule.searchAttributes.gatewayId[0]);
      } catch (e) {
        gateway = {
          data: {
            friendlyName: "Gateway not found",
            id: "",
            description: "",
            dateCreated: "",
            endpointUrl: ""
          }
        } as BFFResponse<Gateway>;
      }


      return createResponse(value.status, schedule, gateway.data);
    }).catch((reason) => {
      throw reason;
    });
  };

  const createResponse = (status: number, schedule: Schedule, gateway: Gateway): BFFResponse<ClientSchedule> => {
    return {
      status: status,
      data: {
        id: schedule.scheduleId,
        status: schedule.schedule.state.paused ? ScheduleStatus.Paused : ScheduleStatus.Running,
        userId: schedule.searchAttributes.userId[0],
        createdAt: getDateString(new Date(schedule.info.createdAt)),
        updatedAt: getDateString(new Date(schedule.info.lastUpdatedAt)),
        nextRun: getDateString(new Date(schedule.info.nextActionTimes[0])),
        gateway: gateway as Gateway,
        lastRun: schedule.info.recentActions.length > 0 ? getDateString(new Date(schedule.info.recentActions[0].scheduledAt)) : "",
        gatewayId: schedule.searchAttributes.gatewayId[0],
        nextActionTimes: schedule.info.nextActionTimes.map((action) => getDateString(new Date(action))),
        recentActions: schedule.info.recentActions.map((action) => getDateString(new Date(action.scheduledAt)))
      } as ClientSchedule
    } as BFFResponse<ClientSchedule>;
  };

  return {
    getScheduleCount,
    getSchedules,
    pauseSchedule,
    resumeSchedule,
    getScheduleById,
    createSchedule,
    deleteScheduleById,
    updateSchedule,
    getSchedulesForBulkAction,
    bulkUpdateSchedules
  };
};
