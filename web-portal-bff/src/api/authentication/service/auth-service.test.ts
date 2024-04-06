import { AuthService } from "./auth-service";
import mockAxios from "jest-mock-axios";

jest.mock("../../../axios-client", () => {
  return jest.requireActual("jest-mock-axios");
});
describe("AuthService", () => {
  let authService: ReturnType<typeof AuthService>;

  beforeEach(() => {
    mockAxios.reset();
    authService = AuthService();
  });

  it("should get auth token", async () => {
    const scopes = ["scope1", "scope2"];

    const promise = authService.getAuthToken(scopes);

    mockAxios.mockResponse({
      data: {
        token: "test-token",
        expires: "2023-01-01T00:00:00Z"
      },
      status: 200
    });

    const result = await promise;

    expect(mockAxios.post).toHaveBeenCalledWith(process.env.AUTH_API_URL + "/auth", scopes);


    expect(result.status).toBe(200);
    expect(result.data).toBeDefined();
    expect(result.data.token).toBe("test-token");
    expect(result.data.expires).toBe("2023-01-01T00:00:00Z");
  });

  it("should pass non 200 status back", async () => {
    const scopes = ["scope1", "scope2"];

    const promise = authService.getAuthToken(scopes);

    mockAxios.mockResponse({
      data: {},
      status: 400
    });

    const result = await promise;

    expect(mockAxios.post).toHaveBeenCalledWith(process.env.AUTH_API_URL + "/auth", scopes);


    expect(result.status).toBe(400);
    expect(result.data).toBeDefined();
    expect(result.data.token).toBeUndefined();
  });

  it("should throw exception if an exception is thrown", () => {
    const scopes = ["scope1", "scope2"];

    const promise = authService.getAuthToken(scopes);

    mockAxios.mockError(new Error("test error"));

    expect(promise).rejects.toThrow("test error");
  });
});
