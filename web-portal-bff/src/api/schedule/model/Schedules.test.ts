import { getMonthNumber, Month } from "./Schedules";

describe("Schedule model tests", () => {
  it("should return the correct month number for January", () => {
    const monthNumber = getMonthNumber(Month.January);
    expect(monthNumber).toBe(1);
  });

  it("should return the correct month number for February", () => {
    const monthNumber = getMonthNumber(Month.February);
    expect(monthNumber).toBe(2);
  });

  it("should return the correct month number for March", () => {
    const monthNumber = getMonthNumber(Month.March);
    expect(monthNumber).toBe(3);
  });

  it("should return the correct month number for April", () => {
    const monthNumber = getMonthNumber(Month.April);
    expect(monthNumber).toBe(4);
  });

  it("should return the correct month number for May", () => {
    const monthNumber = getMonthNumber(Month.May);
    expect(monthNumber).toBe(5);
  });

  it("should return the correct month number for June", () => {
    const monthNumber = getMonthNumber(Month.June);
    expect(monthNumber).toBe(6);
  });

  it("should return the correct month number for July", () => {
    const monthNumber = getMonthNumber(Month.July);
    expect(monthNumber).toBe(7);
  });

  it("should return the correct month number for August", () => {
    const monthNumber = getMonthNumber(Month.August);
    expect(monthNumber).toBe(8);
  });

  it("should return the correct month number for September", () => {
    const monthNumber = getMonthNumber(Month.September);
    expect(monthNumber).toBe(9);
  });

  it("should return the correct month number for October", () => {
    const monthNumber = getMonthNumber(Month.October);
    expect(monthNumber).toBe(10);
  });

  it("should return the correct month number for November", () => {
    const monthNumber = getMonthNumber(Month.November);
    expect(monthNumber).toBe(11);
  });

  it("should return the correct month number for December", () => {
    const monthNumber = getMonthNumber(Month.December);
    expect(monthNumber).toBe(12);
  });
});
