import extractAuthToken from "./extract-auth-token";

describe("Extract Auth Token", () => {
  it("should extract auth token if provided", () => {
    expect(extractAuthToken("test-token")).toEqual({ Authorization: "test-token" });
  });

  it("should return empty object if no token is provided", () => {
    expect(extractAuthToken(undefined)).toEqual({});
  });
});
