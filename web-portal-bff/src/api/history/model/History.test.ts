import { getStatusNumberByString, getStatusStringByNumber } from "./History";

describe("History Model Tests", () => {

  it("should return correct number for each status", () => {
    expect(getStatusNumberByString("Running")).toEqual(1);
    expect(getStatusNumberByString("Completed")).toEqual(2);
    expect(getStatusNumberByString("Failed")).toEqual(3);
    expect(getStatusNumberByString("Cancelled")).toEqual(4);
    expect(getStatusNumberByString("Terminated")).toEqual(5);
    expect(getStatusNumberByString("Any Status")).toEqual(0);
  });

  it("should return 0 for unknown status", () => {
    expect(getStatusNumberByString("Unknown")).toEqual(0);
  });


  it("should return correct status for each number", () => {
    expect(getStatusStringByNumber(1)).toEqual("Running");
    expect(getStatusStringByNumber(2)).toEqual("Completed");
    expect(getStatusStringByNumber(3)).toEqual("Failed");
    expect(getStatusStringByNumber(4)).toEqual("Cancelled");
    expect(getStatusStringByNumber(5)).toEqual("Terminated");
  });

  it("should return 'Unknown' for any other number", () => {
    expect(getStatusStringByNumber(0)).toEqual("Unknown");
    expect(getStatusStringByNumber(6)).toEqual("Unknown");
    expect(getStatusStringByNumber(-1)).toEqual("Unknown");
  });


});
