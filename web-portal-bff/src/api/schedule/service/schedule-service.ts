import {
  BaseClientSchedule,
  ClientSchedule, ClientScheduleCreateEdit, ClientScheduleCreateRequest,
  ClientSchedulePage,
  Schedule,
  ScheduleQueryParams,
  ScheduleStatus, ServerScheduleCreateEdit, ServerScheduleCreateRequest,
  ServerSchedulePage
} from "../model/Schedules";
import axiosClient from "../../../axios-client";
import extractAuthToken from "../../../helper/extract-auth-token";
import {TotalMatches} from "../../../model/Shared";
import {BFFResponse} from "../../../model/BFFResponse";
import GatewayService from "../../gateways/service/gateway-service";
import {Gateway} from "../../gateways/model/Gateways";
import {ScheduleSpecService} from "./schedule-spec-service";

export const ScheduleService = () => {

  const convertToBaseClientSchedule = (schedule: Schedule, gateway: Gateway) : BaseClientSchedule => {
    const nextRunDate = new Date(schedule.info.nextActionTimes[0]);
    const formattedNextRunDate = nextRunDate.toLocaleString();
    let formattedLastRunDate;

    if (schedule.info.recentActions.length === 0) {
      formattedLastRunDate = "";
    } else {
      const lastRunDate = new Date(schedule.info.recentActions[0].scheduledAt);
      formattedLastRunDate = lastRunDate.toLocaleString();
    }

    return {
      id: schedule.scheduleId,
      status: schedule.schedule.state.paused ? ScheduleStatus.Paused : ScheduleStatus.Running,
      gatewayName: gateway.friendlyName,
      gatewayId: schedule.searchAttributes.gatewayId? schedule.searchAttributes.gatewayId[0]: "0",
      userId: schedule.searchAttributes.userId? schedule.searchAttributes.userId[0]: "0",
      nextRun: formattedNextRunDate,
      lastRun: formattedLastRunDate
    };
  }

  const getSchedules = async (token: string | undefined, params: ScheduleQueryParams): Promise<BFFResponse<ClientSchedulePage>> => {
    if (params.scheduleId === undefined) {
      return await axiosClient.get(process.env.SCHEDULE_API_URL as string, {
        headers: extractAuthToken(token),
        params: params

      }).then(async (value) => {
        const serverSchedulePage = value.data as ServerSchedulePage;

        const gatewayPromises = serverSchedulePage.content.map(async (schedule) => {
            try {
              return await GatewayService().getGatewayById(token, schedule.searchAttributes.gatewayId[0])
            } catch (error) {
              return {data: {friendlyName: "Gateway not found"}};
            }
          }
        );

        const gateways = await Promise.all(gatewayPromises);


        return {
          status: value.status,
          data: {
            schedules: gateways.map((gateway, index) => {
              const schedule = serverSchedulePage.content[index];
              return convertToBaseClientSchedule(schedule, gateway.data as Gateway);
            }),
            totalElements: serverSchedulePage.totalElements,
            pageSize: serverSchedulePage.size,
            pageNumber: serverSchedulePage.number
          } as ClientSchedulePage
        };

      }).catch((reason) => {
        throw reason;
      });
    }
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

  const getScheduleById = async (token: string | undefined, scheduleId: string) => {
    return await axiosClient.get(`${process.env.SCHEDULE_API_URL as string}/${scheduleId}`, {
      headers: extractAuthToken(token)

    }).then(async (value) => {
      const serverSchedule = value.data as Schedule;
      let gateway;
      try {
        gateway = await GatewayService().getGatewayById(token, serverSchedule.searchAttributes.gatewayId[0])
      } catch (e) {
        gateway = {data: {friendlyName: "Gateway not found"}};
      }
      const baseSchedule = convertToBaseClientSchedule(serverSchedule, gateway.data as Gateway);

      return {
        status: value.status,
        data: {
          ...baseSchedule,
          createdAt: new Date(serverSchedule.info.createdAt).toLocaleString(),
          updatedAt: new Date(serverSchedule.info.updatedAt).toLocaleString(),
          nextActionTimes: serverSchedule.info.nextActionTimes.map((action) => new Date(action).toLocaleString()),
          recentActions: serverSchedule.info.recentActions.map((action) => new Date(action.scheduledAt).toLocaleString())
        } as ClientSchedule
      }

    }).catch((reason) => {
      throw reason;
    });
  }

  const updateScheduleStatus = async (token: string | undefined, scheduleId: string, paused: boolean) => {
    return await axiosClient.get(`${process.env.SCHEDULE_API_URL as string}/${scheduleId}`, {
      headers: extractAuthToken(token)
    }).then(async (value) => {
      const serverSchedule = value.data as Schedule;

      let scheduleUpdate = {
        scheduleId: serverSchedule.scheduleId,
        gatewayId: serverSchedule.searchAttributes.gatewayId? serverSchedule.searchAttributes.gatewayId[0]: "0",
        userId: serverSchedule.searchAttributes.userId? serverSchedule.searchAttributes.userId[0] : "0",
        paused: paused,
      } as ServerScheduleCreateEdit;

      if (serverSchedule.schedule.spec.calendars.length > 0) {
        scheduleUpdate.calendar = serverSchedule.schedule.spec.calendars[0];
      }

      if (serverSchedule.schedule.spec.intervals.length > 0) {
        scheduleUpdate.interval = serverSchedule.schedule.spec.intervals[0];
      }

      if (serverSchedule.schedule.spec.cronExpressions.length > 0) {
        scheduleUpdate.cronExpression = serverSchedule.schedule.spec.cronExpressions[0];
      }

      try {
        const updatedSchedule = await axiosClient.put(`${process.env.SCHEDULE_API_URL as string}`, scheduleUpdate, {
          headers: extractAuthToken(token)
        });


        let gateway;
        try {
          gateway = await GatewayService().getGatewayById(token, serverSchedule.searchAttributes.gatewayId[0])
        } catch (e) {
          gateway = {data: {friendlyName: "Gateway not found"}};
        }

        const updatedScheduleData = updatedSchedule.data as Schedule;
        return {
          status: updatedSchedule.status,
          data: {
            id: updatedScheduleData.scheduleId,
            calendar: updatedScheduleData.schedule.spec.calendars[0],
            interval: updatedScheduleData.schedule.spec.intervals[0],
            cronExpression: updatedScheduleData.schedule.spec.cronExpressions[0],
            status: updatedScheduleData.schedule.state.paused? ScheduleStatus.Paused : ScheduleStatus.Running,
            gatewayId: updatedScheduleData.searchAttributes.gatewayId[0],
            userId: updatedScheduleData.searchAttributes.userId[0],
            gatewayName: gateway.data.friendlyName,
            nextRun: new Date(updatedScheduleData.info.nextActionTimes[0]).toLocaleString(),
            lastRun: updatedScheduleData.info.recentActions.length > 0 ? new Date(updatedScheduleData.info.recentActions[0].scheduledAt).toLocaleString() : "",
          } as ClientScheduleCreateEdit
        };
      } catch (reason) {
        throw reason;
      }
    }).catch((reason) => {
      throw reason;
    });
  }

  const pauseSchedule = async (token: string | undefined, scheduleId: string) => {
    return await updateScheduleStatus(token, scheduleId, true);
  }

  const resumeSchedule = async (token: string | undefined, scheduleId: string) => {
    return await updateScheduleStatus(token, scheduleId, false);
  }


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
  }

  const createSchedule = async (authorization: string | undefined, schedule: ClientScheduleCreateRequest)  => {
    let scheduleRequest: ServerScheduleCreateRequest = {
      gatewayId: schedule.gatewayId,
      userId: schedule.userId,
      paused: false,
    }

    switch (schedule.scheduleType) {
      case "Interval":
        scheduleRequest.interval = ScheduleSpecService().getIntervalSpec(schedule.intervalSpec!)
        break;
      case "CalendarWeek":
        scheduleRequest.calendar = ScheduleSpecService().getCalendarWeekSpec(schedule.calendarWeekSpec!)
        break;
      case "CalendarMonth":
      case "Cron":
        break;
    }

    return axiosClient.post(`${process.env.SCHEDULE_API_URL as string}`, scheduleRequest, {
      headers: extractAuthToken(authorization),
    }).then((value) => {
      const schedule = value.data as Schedule;
      return {
        status: value.status,
        data: {
          id: schedule.scheduleId,
          status: schedule.schedule.state.paused? ScheduleStatus.Paused : ScheduleStatus.Running,
          userId: schedule.searchAttributes.userId[0],
          createdAt: new Date(schedule.info.createdAt).toLocaleString(),
          updatedAt: new Date(schedule.info.updatedAt).toLocaleString(),
          nextRun: new Date(schedule.info.nextActionTimes[0]).toLocaleString(),
          lastRun: schedule.info.recentActions.length > 0 ? new Date(value.data.info.recentActions[0].scheduledAt).toLocaleString() : "",
          gatewayId: schedule.searchAttributes.gatewayId[0],
          nextActionTimes: schedule.info.nextActionTimes.map((action) => new Date(action).toLocaleString()),
          recentActions: schedule.info.recentActions.map((action) => new Date(action.scheduledAt).toLocaleString())
        } as ClientSchedule
      } as BFFResponse<ClientSchedule>;
    }).catch((reason) => {
      throw reason;
    });
  }

  return {getScheduleCount, getSchedules, pauseSchedule, resumeSchedule, getScheduleById, createSchedule}
}
