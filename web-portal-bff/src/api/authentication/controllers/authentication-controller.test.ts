import express from "express";
import router from "./authentication-controller";
import { AuthToken } from "../model/auth-models";
import { BFFResponse } from "../../../model/BFFResponse";
import request from "supertest";
import { AuthService } from "../service/auth-service";
import { AxiosError } from "axios";

jest.mock("../service/auth-service");

const app = express();
app.use(express.json());
app.use(router);

describe("Authentication Controller", () => {
  it("should get auth token", async () => {
    const mockAuthResponse = {
      status: 200,
      data: {
        token: "test-token",
        expires: "2023-01-01T00:00:00Z"
      } as AuthToken
    } as BFFResponse<AuthToken>;

    (AuthService as jest.Mock).mockImplementation(() => {
      return {
        getAuthToken: jest.fn().mockResolvedValue(mockAuthResponse)
      };
    });

    const res = await request(app)
      .post("/v1/bff/auth")
      .send(["scope1", "scope2"]);

    expect(res.status).toBe(200);
    expect(res.body).toEqual(mockAuthResponse.data);
  });

  it("should handle error with response", async () => {
    const mockError = {
      response: {
        status: 400,
        data: "Bad Request"
      }
    };

    (AuthService as jest.Mock).mockImplementation(() => {
      return {
        getAuthToken: jest.fn().mockRejectedValue(mockError)
      };
    });

    const res = await request(app)
      .post("/v1/bff/auth")
      .send(["scope1", "scope2"]);

    expect(res.status).toBe(400);
    expect(res.text).toEqual("Bad Request");
  });

  it("should 500 error for generic exception", async () => {
    const mockError = new AxiosError("test", "test");

    (AuthService as jest.Mock).mockImplementation(() => {
      return {
        getAuthToken: jest.fn().mockResolvedValue(mockError)
      };
    });

    const res = await request(app)
      .post("/v1/bff/auth")
      .send(["scope1", "scope2"]);

    expect(res.status).toBe(500);
    expect(res.body).toMatchObject({});
    expect(res.text).toEqual("Internal Server Error");
  });

  it("should handle error with request but no response", async () => {
    const mockError = {
      request: {}
    };

    (AuthService as jest.Mock).mockImplementation(() => {
      return {
        getAuthToken: jest.fn().mockRejectedValue(mockError)
      };
    });

    const res = await request(app)
      .post("/v1/bff/auth")
      .send(["scope1", "scope2"]);

    expect(res.status).toBe(500);
    expect(res.text).toEqual("No response from server");
  });

});
