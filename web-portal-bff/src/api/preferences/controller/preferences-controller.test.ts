import router from "./preferences-controller";
import express from "express";
import {BFFResponse} from "../../../model/BFFResponse";
import {ClientPreferences} from "../model/Preferences";
import {PreferencesService} from "../service/preferences-service";
import request from "supertest";

jest.mock("../service/preferences-service");

const app = express();
app.use(express.json());
app.use(router);

describe('Preferences Controller', () => {
  it('should get preferences', async () => {
    const mockPreferencesResponse = {
      status: 200,
      data: {
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
      } as ClientPreferences
    } as BFFResponse<ClientPreferences>

    (PreferencesService as jest.Mock).mockImplementation(() => {
      return {
        getPreferences: jest.fn().mockResolvedValue(mockPreferencesResponse)
      };
    });

    const res = await request(app)
      .get('/v1/bff/preferences')


    expect(res.status).toBe(200);
    expect(res.body).toEqual(mockPreferencesResponse.data);
  });

  it('should handle error with response', async () => {
    const mockError = {
      response: {
        status: 400,
        data: 'Bad Request',
      }
    };

    (PreferencesService as jest.Mock).mockImplementation(() => {
      return {
        getPreferences: jest.fn().mockRejectedValue(mockError),
      };
    });

    const res = await request(app)
      .get('/v1/bff/preferences')


    expect(res.status).toBe(400);
    expect(res.text).toEqual('Bad Request');
  });

  it('should put preferences', async () => {
    const mockPreferencesResponse = {
      status: 200,
      data: {
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
      } as ClientPreferences
    } as BFFResponse<ClientPreferences>

    (PreferencesService as jest.Mock).mockImplementation(() => {
      return {
        putPreferences: jest.fn().mockResolvedValue(mockPreferencesResponse)
      };
    });

    const res = await request(app)
      .put('/v1/bff/preferences')
      .send(mockPreferencesResponse.data)

    expect(res.status).toBe(200);
    expect(res.body).toEqual(mockPreferencesResponse.data);
  });

  it('should return bad request 400 when putting preferences', async () => {
    const mockError = {
      response: {
        status: 400,
        data: 'Bad Request',
      }
    };

    (PreferencesService as jest.Mock).mockImplementation(() => {
      return {
        putPreferences: jest.fn().mockRejectedValue(mockError),
      };
    });

    const res = await request(app)
      .put('/v1/bff/preferences')
      .send({})

    expect(res.status).toBe(400);
    expect(res.text).toEqual('Bad Request');
  });
});

