import { BFFResponse } from "../../../model/BFFResponse";
import axiosClient from "../../../axios-client";
import extractAuthToken from "../../../helper/extract-auth-token";
import {
  BaseGateway,
  ClientGatewayPage,
  Gateway,
  GatewayPageQuery,
  GatewayUpdateRequest,
  ServerGatewayPage
} from "../model/Gateways";

export const GatewayService = () => {
  const getGateways = async (token: string | undefined, params: GatewayPageQuery): Promise<BFFResponse<ClientGatewayPage>> => {
    if (params.gatewayId === undefined) {
      return await axiosClient.get(process.env.GATEWAY_API_URL as string, {
        headers: extractAuthToken(token),
        params: params

      }).then((value) => {
        const serverGatewayPage = value.data as ServerGatewayPage;
        return {
          status: value.status,
          data: {
            gateways: serverGatewayPage.content.map((gateway) => {
              const date = new Date(gateway.dateCreated);
              const formattedDate = date.toLocaleString();
              return {
                ...gateway,
                dateCreated: formattedDate
              };
            }),
            totalElements: serverGatewayPage.totalElements,
            pageSize: serverGatewayPage.size,
            pageNumber: serverGatewayPage.number
          } as ClientGatewayPage
        };

      }).catch((reason) => {
        throw reason;
      });
    }
    return await getGatewayById(token, params.gatewayId).then((value) => {
      return {
        status: value.status,
        data: {
          gateways: [value.data],
          totalElements: 1,
          pageSize: 25,
          pageNumber: 0
        } as ClientGatewayPage
      };
    }).catch((reason) => {
      if (reason.response.status === 404) {
        return {
          status: 200,
          data: {
            gateways: [],
            totalElements: 0,
            pageSize: 25,
            pageNumber: 0
          } as ClientGatewayPage
        };
      }
      throw reason;
    });
  };

  const getGatewayById = async (token: string | undefined, gatewayId: string): Promise<BFFResponse<Gateway>> => {
    return await axiosClient.get(`${process.env.GATEWAY_API_URL as string}/${gatewayId}`, {
      headers: extractAuthToken(token)
    }).then((value) => {
      const serverGateway = value.data as Gateway;
      return {
        status: value.status,
        data: {
          ...serverGateway,
          dateCreated: new Date(serverGateway.dateCreated).toLocaleString()
        } as Gateway
      };

    }).catch((reason) => {
      throw reason;
    });
  };

  const deleteGatewayById = async (token: string | undefined, gatewayId: string): Promise<BFFResponse<undefined>> => {
    return await axiosClient.delete(`${process.env.GATEWAY_API_URL as string}/${gatewayId}`, {
      headers: extractAuthToken(token)
    }).then((value) => {
      return {
        status: value.status,
        data: undefined
      };
    }).catch((reason) => {
      throw reason;
    });
  };

  const updateGateway = async (token: string | undefined, gateway: GatewayUpdateRequest): Promise<BFFResponse<Gateway>> => {
    return await axiosClient.put(`${process.env.GATEWAY_API_URL as string}`, gateway, {
      headers: extractAuthToken(token)
    }).then((value) => {
      const serverGateway = value.data as Gateway;
      return {
        status: value.status,
        data: {
          ...serverGateway,
          dateCreated: new Date(serverGateway.dateCreated).toLocaleString()
        } as Gateway
      };
    }).catch((reason) => {
      throw reason;
    });
  };

  const createGateway = async (token: string | undefined, gateway: BaseGateway): Promise<BFFResponse<Gateway>> => {
    return await axiosClient.post(`${process.env.GATEWAY_API_URL as string}`, gateway, {
      headers: extractAuthToken(token)
    }).then((value) => {
      const serverGateway = value.data as Gateway;
      return {
        status: value.status,
        data: {
          ...serverGateway,
          dateCreated: new Date(serverGateway.dateCreated).toLocaleString()
        } as Gateway
      };
    }).catch((reason) => {
      throw reason;
    });
  };

  return { getGateways, getGatewayById, deleteGatewayById, updateGateway, createGateway };
};

export default GatewayService;
