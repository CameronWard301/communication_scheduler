export interface BaseGateway {
  endpointUrl: string;
  friendlyName: string;
  description: string;
}

export interface Gateway extends BaseGateway {
  id: string;
  dateCreated: string;
}

export interface GatewayUpdateRequest extends BaseGateway {
  id: string;
}

export interface ServerGatewayPage {
  content: Gateway[];
  totalElements: number;
  size: number;
  number: number;
}

export interface ClientGatewayPage {
  gateways: Gateway[];
  totalElements: number;
  pageSize: number;
  pageNumber: number;
}

export interface GatewayPageQuery {
  pageSize: string;
  pageNumber: string;
  gatewayId: string;
}
