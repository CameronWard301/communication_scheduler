import express from "express";
import router from "./stats-controller";
import MockAdapter from "axios-mock-adapter";
import axiosClient from "../../../axios-client";
import request from "supertest";
import proxy from "../../../proxy";
import { Readable } from "node:stream";


const app = express();
app.use(express.json());
app.use(router);
jest.mock("../../schedule/service/schedule-service");
jest.mock("../../../proxy");
const mockProxy = jest.mocked(proxy);

describe("Stats Controller", () => {
  let mockAxios = new MockAdapter(axiosClient);

  beforeEach(() => {
    mockAxios.reset();
    process.env.GATEWAY_API_URL = "http://gateway-api:8080";
  });

  afterAll(() => {
    mockProxy.close();
  })

  it("should forward request to grafana service", () => {
    const mockData = new Readable();
    mockData.push("test");
    mockData.push(null);

    mockAxios.onAny().reply(200, mockData);


    return request(app)
      .get("/grafana/test")
      .expect(200)
      .expect("test");
  });

  it("should return 500 error if an error occurs internally", () => {
    mockAxios.onAny().reply(500, "Internal Server Error");

    return request(app)
      .get("/grafana/test")
      .expect(500)
      .expect("Internal BFF Server error");
  });

  it("should pipe the error if sent back from downstream", () => {
    const mockData = new Readable();
    mockData.push("test-error");
    mockData.push(null);

    mockAxios.onAny().reply(400, mockData);

    return request(app)
      .get("/grafana/test")
      .expect(400)
      .expect("test-error");
  });

  it("should proxy websocket", () => {
    mockProxy.ws.mockImplementation((req, res, head) => {
      return res.end();
    });

    return request(app)
      .post("/grafana/api/live/ws")
      .expect(200);

  });

})
