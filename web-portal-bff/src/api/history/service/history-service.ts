import { BFFResponse } from "../../../model/BFFResponse";
import {
  ClientHistoryItem,
  ClientHistoryPage,
  ClientHistoryPageQuery,
  getStatusNumberByString,
  getStatusStringByNumber,
  ServerHistoryPage,
  ServerHistoryPageQuery,
  StopCommunicationResponse
} from "../model/History";
import axiosClient from "../../../axios-client";
import extractAuthToken from "../../../helper/extract-auth-token";
import GatewayService from "../../gateways/service/gateway-service";
import { getDateString } from "../../../helper/date-converter";

export const HistoryService = () => {
  const resolveQueryParams = (params: ClientHistoryPageQuery): ServerHistoryPageQuery => {
    const query: ServerHistoryPageQuery = {
      pageSize: params.pageSize,
      pageNumber: params.pageNumber,
      gatewayId: params.gatewayId,
      scheduleId: params.scheduleId,
      userId: params.userId
    };
    if (params.status) {
      query.status = getStatusNumberByString(params.status);
    }
    return query;
  };

  const getDateFromSecondsAndNanos = (seconds: number, nanos: number): Date => {
    return new Date(seconds * 1000 + nanos / 1e6);
  };

  const getHistoryPage = async (token: string | undefined, params: ClientHistoryPageQuery): Promise<BFFResponse<ClientHistoryPage>> => {
    return await axiosClient.get(`${process.env.HISTORY_API_URL as string}`, {
      headers: extractAuthToken(token),
      params: resolveQueryParams(params)
    }).then(async (value) => {
      const serverHistoryPage = value.data as ServerHistoryPage;
      const gatewayIds = [...new Set(serverHistoryPage.content.map((historyItem) => historyItem.gatewayId))];
      return await GatewayService().resolveGatewayIds(token, gatewayIds).then((gateways) => {
        return {
          status: value.status,
          data: {
            historyItems: serverHistoryPage.content.map((historyItem) => {
              return {
                id: historyItem.runId,
                gatewayId: historyItem.gatewayId,
                userId: historyItem.userId,
                scheduleId: historyItem.scheduleId,
                workflowId: historyItem.workflowId,
                status: getStatusStringByNumber(historyItem.status),
                gatewayName: gateways[historyItem.gatewayId].friendlyName,
                startTime: getDateString(getDateFromSecondsAndNanos(historyItem.startTime.seconds, historyItem.startTime.nanos)),
                endTime: historyItem.endTime ? getDateString(getDateFromSecondsAndNanos(historyItem.endTime.seconds, historyItem.endTime.nanos)) : null
              } as ClientHistoryItem;
            }),
            totalElements: serverHistoryPage.totalElements,
            pageSize: serverHistoryPage.size,
            pageNumber: serverHistoryPage.number
          } as ClientHistoryPage
        };
      });
    }).catch((error) => {
      throw error;
    });
  };

  const stopCommunication = async (token: string | undefined, workflowId: string, runId: string): Promise<BFFResponse<StopCommunicationResponse>> => {
    return await axiosClient.delete(`${process.env.HISTORY_API_URL as string}/${workflowId}/${runId}`, {
      headers: extractAuthToken(token)
    }).then((result) => {
      return {
        status: result.status,
        data: result.data
      } as BFFResponse<StopCommunicationResponse>;
    }).catch((error) => {
      throw error;
    });
  };

  return { getHistoryPage, stopCommunication };
};

export default HistoryService;
