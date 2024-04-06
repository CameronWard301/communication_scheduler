import { PreferencesService } from "./preferences-service";
import { ClientPreferences } from "../model/Preferences";
import axiosClient from "../../../axios-client";
import MockAdapter from "axios-mock-adapter";


describe("PreferencesService", () => {
  let preferencesService: ReturnType<typeof PreferencesService>;
  let mockAxios = new MockAdapter(axiosClient);

  beforeEach(() => {
    mockAxios.reset();
    preferencesService = PreferencesService();

  });


  it("should get preferences", async () => {


    const expectedResponse = {
      maximumAttempts: 100,
      backoffCoefficient: 2,
      gatewayTimeout: {
        value: 60,
        unit: "S"
      },
      initialInterval: {
        value: 1,
        unit: "S"
      },
      maximumInterval: {
        value: 100,
        unit: "S"
      },
      startToCloseTimeout: {
        value: 10,
        unit: "S"
      }
    } as ClientPreferences;

    mockAxios.onGet(process.env.PREFERENCES_API_URL).reply(200, {

      gatewayTimeoutSeconds: expectedResponse.gatewayTimeout.value,
      retryPolicy: {
        maximumAttempts: expectedResponse.maximumAttempts,
        backoffCoefficient: expectedResponse.backoffCoefficient,
        initialInterval: `PT${expectedResponse.initialInterval.value}${expectedResponse.initialInterval.unit}`,
        maximumInterval: `PT${expectedResponse.maximumInterval.value}${expectedResponse.maximumInterval.unit}`,
        startToCloseTimeout: `PT${expectedResponse.startToCloseTimeout.value}${expectedResponse.startToCloseTimeout.unit}`
      }
    });

    const result = await preferencesService.getPreferences("test-token");
    expect(result.status).toBe(200);
    expect(result.data).toBeDefined();
    expect(result.data).toEqual(expectedResponse);

  });

  it("should throw an exception when getting preferences", async () => {

    mockAxios.onGet(process.env.PREFERENCES_API_URL).reply(() => Promise.reject({
      response: {
        status: 400,
        data: "Bad Request"
      }
    }));


    preferencesService.getPreferences("test-token").catch((reason) => {
      expect(reason.response.status).toBe(400);
      expect(reason.response.data).toBe("Bad Request");
    });
  });

  it("should put preferences with correct seconds", async () => {

    const putRequest = {
      maximumAttempts: 100,
      backoffCoefficient: 2,
      gatewayTimeout: {
        value: 60,
        unit: "S"
      },
      initialInterval: {
        value: 1,
        unit: "S"
      },
      maximumInterval: {
        value: 100,
        unit: "S"
      },
      startToCloseTimeout: {
        value: 10,
        unit: "S"
      }
    } as ClientPreferences;


    await verifyPutRequest(putRequest, putRequest, mockAxios, preferencesService);

  });

  it("should put preferences with correct minutes", async () => {

    const putRequest = {
      maximumAttempts: 100,
      backoffCoefficient: 2,
      gatewayTimeout: {
        value: 60,
        unit: "M"
      },
      initialInterval: {
        value: 1,
        unit: "S"
      },
      maximumInterval: {
        value: 100,
        unit: "S"
      },
      startToCloseTimeout: {
        value: 10,
        unit: "S"
      }
    } as ClientPreferences;

    const putResponse = {
      maximumAttempts: 100,
      backoffCoefficient: 2,
      gatewayTimeout: {
        value: 3600,
        unit: "S"
      },
      initialInterval: {
        value: 1,
        unit: "S"
      },
      maximumInterval: {
        value: 100,
        unit: "S"
      },
      startToCloseTimeout: {
        value: 10,
        unit: "S"
      }
    } as ClientPreferences;


    await verifyPutRequest(putRequest, putResponse, mockAxios, preferencesService);


  });

  it("should put preferences with correct hours", async () => {

    const putRequest = {
      maximumAttempts: 100,
      backoffCoefficient: 2,
      gatewayTimeout: {
        value: 60,
        unit: "H"
      },
      initialInterval: {
        value: 1,
        unit: "S"
      },
      maximumInterval: {
        value: 100,
        unit: "S"
      },
      startToCloseTimeout: {
        value: 10,
        unit: "S"
      }
    } as ClientPreferences;

    const putResponse = {
      maximumAttempts: 100,
      backoffCoefficient: 2,
      gatewayTimeout: {
        value: 216000,
        unit: "S"
      },
      initialInterval: {
        value: 1,
        unit: "S"
      },
      maximumInterval: {
        value: 100,
        unit: "S"
      },
      startToCloseTimeout: {
        value: 10,
        unit: "S"
      }
    } as ClientPreferences;


    await verifyPutRequest(putRequest, putResponse, mockAxios, preferencesService);


  });

  it("should put preferences with correct days", async () => {

    const putRequest = {
      maximumAttempts: 100,
      backoffCoefficient: 2,
      gatewayTimeout: {
        value: 60,
        unit: "D"
      },
      initialInterval: {
        value: 1,
        unit: "S"
      },
      maximumInterval: {
        value: 100,
        unit: "S"
      },
      startToCloseTimeout: {
        value: 10,
        unit: "S"
      }
    } as ClientPreferences;

    const putResponse = {
      maximumAttempts: 100,
      backoffCoefficient: 2,
      gatewayTimeout: {
        value: 5184000,
        unit: "S"
      },
      initialInterval: {
        value: 1,
        unit: "S"
      },
      maximumInterval: {
        value: 100,
        unit: "S"
      },
      startToCloseTimeout: {
        value: 10,
        unit: "S"
      }
    } as ClientPreferences;


    await verifyPutRequest(putRequest, putResponse, mockAxios, preferencesService);


  });

  it("should put preferences with correct years", async () => {

    const putRequest = {
      maximumAttempts: 100,
      backoffCoefficient: 2,
      gatewayTimeout: {
        value: 2,
        unit: "Y"
      },
      initialInterval: {
        value: 1,
        unit: "S"
      },
      maximumInterval: {
        value: 100,
        unit: "S"
      },
      startToCloseTimeout: {
        value: 10,
        unit: "S"
      }
    } as ClientPreferences;

    const putResponse = {
      maximumAttempts: 100,
      backoffCoefficient: 2,
      gatewayTimeout: {
        value: 63072000,
        unit: "S"
      },
      initialInterval: {
        value: 1,
        unit: "S"
      },
      maximumInterval: {
        value: 100,
        unit: "S"
      },
      startToCloseTimeout: {
        value: 10,
        unit: "S"
      }
    } as ClientPreferences;


    await verifyPutRequest(putRequest, putResponse, mockAxios, preferencesService);


  });

  it("should throw an error if time unit is wrong for gateways seconds", async () => {

    const putRequest = {
      maximumAttempts: 100,
      backoffCoefficient: 2,
      gatewayTimeout: {
        value: 2,
        unit: "F"
      },
      initialInterval: {
        value: 1,
        unit: "S"
      },
      maximumInterval: {
        value: 100,
        unit: "S"
      },
      startToCloseTimeout: {
        value: 10,
        unit: "S"
      }
    } as ClientPreferences;


    preferencesService.putPreferences("test-token", putRequest).catch((reason) => {
      expect(reason.message).toBe("Invalid time unit");
    });
  });

});

const verifyPutRequest = async (putRequest: ClientPreferences, putResponse: ClientPreferences, mockAxios: MockAdapter, preferencesService: ReturnType<typeof PreferencesService>) => {
  mockAxios.onPut(process.env.PREFERENCES_API_URL + "/gateway-timeout").reply(200, {
    gatewayTimeoutSeconds: putResponse.gatewayTimeout.value
  });

  mockAxios.onPut(process.env.PREFERENCES_API_URL + "/retry-policy").reply(200, {
    maximumAttempts: putRequest.maximumAttempts,
    backoffCoefficient: putRequest.backoffCoefficient,
    initialInterval: `PT${putRequest.initialInterval.value}${putRequest.initialInterval.unit}`,
    maximumInterval: `PT${putRequest.maximumInterval.value}${putRequest.maximumInterval.unit}`,
    startToCloseTimeout: `PT${putRequest.startToCloseTimeout.value}${putRequest.startToCloseTimeout.unit}`
  });

  const result = await preferencesService.putPreferences("test-token", putRequest);

  expect(result.status).toBe(200);
  expect(result.data).toBeDefined();
  expect(result.data).toEqual(putResponse);
};


