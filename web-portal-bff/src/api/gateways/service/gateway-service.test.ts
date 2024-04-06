import GatewayService from "./gateway-service";
import { BaseGateway, ClientGatewayPage, Gateway, GatewayPageQuery, ServerGatewayPage } from "../model/Gateways";
import MockAdapter from "axios-mock-adapter";
import axiosClient from "../../../axios-client";

describe("GatewayService", () => {
  let gatewayService: ReturnType<typeof GatewayService>;
  let mockAxios = new MockAdapter(axiosClient);

  beforeEach(() => {
    mockAxios.reset();
    gatewayService = GatewayService();
  });

  it("should get a page of gateways when there are no filters", () => {
    const pageQuery = {
      pageNumber: "0",
      pageSize: "25"
    } as GatewayPageQuery;

    const mockGatewayResponse = {
      content: [
        {
          id: "test-id",
          friendlyName: "test-name",
          description: "test-description",
          endpointUrl: "test-endpoint-url",
          dateCreated: "2023-01-01T00:00:00Z"
        }
      ],
      totalElements: 1,
      size: Number(pageQuery.pageSize),
      number: Number(pageQuery.pageNumber)
    } as ServerGatewayPage;

    const expectedResponse = {
      gateways: [
        {
          id: "test-id",
          friendlyName: "test-name",
          description: "test-description",
          endpointUrl: "test-endpoint-url",
          dateCreated: new Date(mockGatewayResponse.content[0].dateCreated).toLocaleString()
        }
      ],
      totalElements: 1,
      pageSize: Number(pageQuery.pageSize),
      pageNumber: Number(pageQuery.pageNumber)
    } as ClientGatewayPage;

    mockAxios.onGet(process.env.GATEWAY_API_URL).reply(200, mockGatewayResponse);

    gatewayService.getGateways("test-token", pageQuery).then((result) => {
      expect(result.status).toBe(200);
      expect(result.data).toBeDefined();
      expect(result.data).toEqual(expectedResponse);
    });
  });

  it("should throw exception when getting page of gateways", () => {
    const pageQuery = {
      pageNumber: "0",
      pageSize: "25"
    } as GatewayPageQuery;

    mockAxios.onGet(process.env.GATEWAY_API_URL).reply(() => Promise.reject({
      response: {
        status: 404,
        data: "not found"
      }
    }));

    gatewayService.getGateways("test-token", pageQuery).catch((reason) => {
      expect(reason.response.status).toBe(404);
      expect(reason.response.data).toBe("not found");
    });
  });

  it("should get a page of gateways when filtering by gatewayId", () => {
    const pageQuery = {
      pageNumber: "0",
      pageSize: "25",
      gatewayId: "test-id"
    } as GatewayPageQuery;

    const mockGatewayResponse = {
      id: "test-id",
      friendlyName: "test-name",
      description: "test-description",
      endpointUrl: "test-endpoint-url",
      dateCreated: "2023-01-01T00:00:00Z"
    } as Gateway;

    const expectedResponse = {
      gateways: [
        {
          id: "test-id",
          friendlyName: "test-name",
          description: "test-description",
          endpointUrl: "test-endpoint-url",
          dateCreated: new Date(mockGatewayResponse.dateCreated).toLocaleString()
        }
      ],
      totalElements: 1,
      pageSize: 25,
      pageNumber: 0
    } as ClientGatewayPage;

    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${pageQuery.gatewayId}`).reply(200, mockGatewayResponse);

    gatewayService.getGateways("test-token", pageQuery).then((result) => {
      expect(result.status).toBe(200);
      expect(result.data).toBeDefined();
      expect(result.data).toEqual(expectedResponse);
    });
  });

  it("should return an empty page of results if filtering by id does not match any gateways", () => {
    const pageQuery = {
      pageNumber: "0",
      pageSize: "25",
      gatewayId: "test-id"
    } as GatewayPageQuery;

    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${pageQuery.gatewayId}`).reply(404, "not found");

    gatewayService.getGateways("test-token", pageQuery).then((result) => {
      expect(result.status).toBe(200);
      expect(result.data).toBeDefined();
      expect(result.data.gateways).toHaveLength(0);
      expect(result.data.totalElements).toBe(0);
    });
  });

  it("should throw an error if filtering by id produces a non 404 error", () => {
    const pageQuery = {
      pageNumber: "0",
      pageSize: "25",
      gatewayId: "test-id"
    } as GatewayPageQuery;

    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${pageQuery.gatewayId}`).reply(() => Promise.reject({
      response: {
        status: 400,
        data: "bad request"
      }
    }));

    gatewayService.getGateways("test-token", pageQuery).catch((reason) => {
      expect(reason.response.status).toBe(400);
      expect(reason.response.data).toBe("bad request");
    });
  });

  it("should get gateway by id", async () => {
    const mockGatewayResponse = {
      id: "test-id",
      friendlyName: "test-name",
      description: "test-description",
      endpointUrl: "test-endpoint-url",
      dateCreated: "2023-01-01T00:00:00Z"
    } as Gateway;

    const expectedResponse = {
      id: "test-id",
      friendlyName: "test-name",
      description: "test-description",
      endpointUrl: "test-endpoint-url",
      dateCreated: new Date(mockGatewayResponse.dateCreated).toLocaleString()
    } as Gateway;

    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/${mockGatewayResponse.id}`).reply(200, mockGatewayResponse);

    const result = await gatewayService.getGatewayById("test-token", mockGatewayResponse.id);
    expect(result.status).toBe(200);
    expect(result.data).toBeDefined();
    expect(result.data).toEqual(expectedResponse);
  });

  it("should throw exception when getting gateway by id", async () => {
    mockAxios.onGet(`${process.env.GATEWAY_API_URL}/test-id`).reply(() => Promise.reject({
      response: {
        status: 404,
        data: "not found"
      }
    }));

    gatewayService.getGatewayById("test-token", "test-id").catch((reason) => {
      expect(reason.response.status).toBe(404);
      expect(reason.response.data).toBe("not found");
    });
  });

  it("should delete gateway by id", () => {
    mockAxios.onDelete(`${process.env.GATEWAY_API_URL}/test-id`).reply(204, "deleted");

    gatewayService.deleteGatewayById("test-token", "test-id").then((result) => {
      expect(result.status).toBe(204);
      expect(result.data).toBe(undefined);
    });
  });

  it("should throw exception when deleting a gateway by id", async () => {
    mockAxios.onDelete(`${process.env.GATEWAY_API_URL}/test-id`).reply(() => Promise.reject({
      response: {
        status: 404,
        data: "not found"
      }
    }));

    gatewayService.deleteGatewayById("test-token", "test-id").catch((reason) => {
      expect(reason.response.status).toBe(404);
      expect(reason.response.data).toBe("not found");
    });
  });

  it("should update gateway", () => {
    const mockGatewayRequest = {
      id: "test-id",
      friendlyName: "test-name",
      description: "test-description",
      endpointUrl: "test-endpoint-url"
    } as Gateway;

    const serverResponse = {
      id: mockGatewayRequest.id,
      friendlyName: mockGatewayRequest.friendlyName,
      description: mockGatewayRequest.description,
      endpointUrl: mockGatewayRequest.endpointUrl,
      dateCreated: "2023-01-01T00:00:00Z"
    } as Gateway;

    const expectedResponse = {
      id: mockGatewayRequest.id,
      friendlyName: mockGatewayRequest.friendlyName,
      description: mockGatewayRequest.description,
      endpointUrl: mockGatewayRequest.endpointUrl,
      dateCreated: new Date(serverResponse.dateCreated).toLocaleString()
    } as Gateway;

    mockAxios.onPut(`${process.env.GATEWAY_API_URL}`, mockGatewayRequest).reply(200, serverResponse);

    gatewayService.updateGateway("test-token", mockGatewayRequest).then((result) => {
      expect(result.status).toBe(200);
      expect(result.data).toEqual(expectedResponse);
    });
  });

  it("should throw exception when updating a gateway", async () => {
    const mockGatewayRequest = {
      id: "test-id",
      friendlyName: "test-name",
      description: "test-description",
      endpointUrl: "test-endpoint-url"
    } as Gateway;

    mockAxios.onPut(`${process.env.GATEWAY_API_URL}`).reply(() => Promise.reject({
      response: {
        status: 400,
        data: "bad request"
      }
    }));

    gatewayService.updateGateway("test-token", mockGatewayRequest).catch((reason) => {
      expect(reason.response.status).toBe(400);
      expect(reason.response.data).toBe("bad request");
    });
  });

  it("should create gateway", () => {
    const requestedGateway = {
      endpointUrl: "test-endpoint-url",
      friendlyName: "test-name",
      description: "test-description"
    } as BaseGateway;

    const serverResponse = {
      id: "test-id",
      friendlyName: requestedGateway.friendlyName,
      description: requestedGateway.description,
      endpointUrl: requestedGateway.endpointUrl,
      dateCreated: "2023-01-01T00:00:00Z"
    } as Gateway;

    const expectedResponse = {
      id: serverResponse.id,
      friendlyName: serverResponse.friendlyName,
      description: serverResponse.description,
      endpointUrl: serverResponse.endpointUrl,
      dateCreated: new Date(serverResponse.dateCreated).toLocaleString()
    } as Gateway;

    mockAxios.onPost(`${process.env.GATEWAY_API_URL}`, requestedGateway).reply(200, serverResponse);

    gatewayService.createGateway("test-token", requestedGateway).then((result) => {
      expect(result.status).toBe(200);
      expect(result.data).toEqual(expectedResponse);
    });
  });

  it("should throw exception when creating a gateway", async () => {
    const mockGatewayRequest = {
      friendlyName: "test-name",
      description: "test-description"
    } as BaseGateway;

    mockAxios.onPost(`${process.env.GATEWAY_API_URL}`).reply(() => Promise.reject({
      response: {
        status: 400,
        data: "bad request"
      }
    }));

    gatewayService.createGateway("test-token", mockGatewayRequest).catch((reason) => {
      expect(reason.response.status).toBe(400);
      expect(reason.response.data).toBe("bad request");
    });
  });
});
