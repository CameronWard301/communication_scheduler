export interface Gateway {
  id: string,
  endpointUrl: string,
  friendlyName: string,
  description: string
  dateCreated: string
}

export interface GatewayPage {
  gateways: Gateway[];
  totalElements: number;
  pageNumber: number;
  pageSize: number;
}

export interface TotalMatches {
  total: number;
}
