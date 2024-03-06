import {ScheduleQueryParams} from "../model/Schedules";
import axiosClient from "../../../axios-client";
import extractAuthToken from "../../../helper/extract-auth-token";
import {TotalMatches} from "../../../model/Shared";

export const ScheduleService = () => {
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
  return {getScheduleCount}
}
