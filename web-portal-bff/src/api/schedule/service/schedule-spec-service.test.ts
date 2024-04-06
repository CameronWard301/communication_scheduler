import { describe } from "@jest/globals";
import { ScheduleSpecService } from "./schedule-spec-service";
import {
  ClientCalendarMonthSpec,
  ClientCalendarWeekSpec,
  ClientIntervalSpec,
  DayOfWeek,
  Month,
  Period
} from "../model/Schedules";

describe("Schedule Service Spec", () => {

  let scheduleSpecService: ReturnType<typeof ScheduleSpecService>;

  beforeEach(() => {
    scheduleSpecService = ScheduleSpecService();
  });

  it("should get interval spec when offset period is set to days", () => {
    const intervalSpec: ClientIntervalSpec = {
      hours: "1",
      minutes: "0",
      seconds: "0",
      days: "2",
      offset: "1",
      offsetPeriod: Period.D
    };

    const result = scheduleSpecService.getIntervalSpec(intervalSpec);

    expect(result).toEqual({
      every: "PT49H0M0S",
      offset: "PT24H"
    });
  });

  it("should get calendar month spec when set to every month", () => {
    const calendarMonthSpec: ClientCalendarMonthSpec = {
      month: Month.EveryMonth,
      dayOfMonth: 1,
      hours: 0,
      minutes: 0
    };

    const result = scheduleSpecService.getCalendarMonthSpec(calendarMonthSpec);

    expect(result).toEqual({
      comment: "",
      seconds: [{ start: "0", end: "0", step: "1" }],
      minutes: [{ start: "0", end: "0", step: "1" }],
      hour: [{ start: "0", end: "0", step: "1" }],
      dayOfMonth: [{ start: "1", end: "1", step: "1" }],
      dayOfWeek: [{ start: "0", end: "6", step: "1" }],
      year: [],
      month: [{ start: "1", end: "12", step: "1" }]
    });
  });


  it("should get calendar week spec for every day", () => {
    const calendarWeekSpec: ClientCalendarWeekSpec = {
      dayOfWeek: DayOfWeek.EveryDay,
      hours: "0",
      minutes: "0"
    };

    const result = scheduleSpecService.getCalendarWeekSpec(calendarWeekSpec);

    expect(result.dayOfWeek).toEqual([{ start: "0", end: "6", step: "1" }]);
  });

  it("should get calendar week spec for weekdays", () => {
    const calendarWeekSpec: ClientCalendarWeekSpec = {
      dayOfWeek: DayOfWeek.Weekdays,
      hours: "0",
      minutes: "0"
    };

    const result = scheduleSpecService.getCalendarWeekSpec(calendarWeekSpec);

    expect(result.dayOfWeek).toEqual([{ start: "1", end: "5", step: "1" }]);
  });

  it("should get calendar week spec for weekend", () => {
    const calendarWeekSpec: ClientCalendarWeekSpec = {
      dayOfWeek: DayOfWeek.Weekend,
      hours: "0",
      minutes: "0"
    };

    const result = scheduleSpecService.getCalendarWeekSpec(calendarWeekSpec);

    expect(result.dayOfWeek).toEqual([{ start: "6", end: "6", step: "1" }, { start: "0", end: "0", step: "1" }]);
  });

  it("should get calendar week spec for monday", () => {
    const calendarWeekSpec: ClientCalendarWeekSpec = {
      dayOfWeek: DayOfWeek.Monday,
      hours: "0",
      minutes: "0"
    };

    const result = scheduleSpecService.getCalendarWeekSpec(calendarWeekSpec);

    expect(result.dayOfWeek).toEqual([{ start: "1", end: "1", step: "1" }]);
  });

  it("should get calendar week spec for tuesday", () => {
    const calendarWeekSpec: ClientCalendarWeekSpec = {
      dayOfWeek: DayOfWeek.Tuesday,
      hours: "0",
      minutes: "0"
    };

    const result = scheduleSpecService.getCalendarWeekSpec(calendarWeekSpec);

    expect(result.dayOfWeek).toEqual([{ start: "2", end: "2", step: "1" }]);
  });

  it("should get calendar week spec for wednesday", () => {
    const calendarWeekSpec: ClientCalendarWeekSpec = {
      dayOfWeek: DayOfWeek.Wednesday,
      hours: "0",
      minutes: "0"
    };

    const result = scheduleSpecService.getCalendarWeekSpec(calendarWeekSpec);

    expect(result.dayOfWeek).toEqual([{ start: "3", end: "3", step: "1" }]);
  });

  it("should get calendar week spec for thursday", () => {
    const calendarWeekSpec: ClientCalendarWeekSpec = {
      dayOfWeek: DayOfWeek.Thursday,
      hours: "0",
      minutes: "0"
    };

    const result = scheduleSpecService.getCalendarWeekSpec(calendarWeekSpec);

    expect(result.dayOfWeek).toEqual([{ start: "4", end: "4", step: "1" }]);
  });

  it("should get calendar week spec for friday", () => {
    const calendarWeekSpec: ClientCalendarWeekSpec = {
      dayOfWeek: DayOfWeek.Friday,
      hours: "0",
      minutes: "0"
    };

    const result = scheduleSpecService.getCalendarWeekSpec(calendarWeekSpec);

    expect(result.dayOfWeek).toEqual([{ start: "5", end: "5", step: "1" }]);
  });

  it("should get calendar week spec for saturday", () => {
    const calendarWeekSpec: ClientCalendarWeekSpec = {
      dayOfWeek: DayOfWeek.Saturday,
      hours: "0",
      minutes: "0"
    };

    const result = scheduleSpecService.getCalendarWeekSpec(calendarWeekSpec);

    expect(result.dayOfWeek).toEqual([{ start: "6", end: "6", step: "1" }]);
  });

  it("should get calendar week spec for sunday", () => {
    const calendarWeekSpec: ClientCalendarWeekSpec = {
      dayOfWeek: DayOfWeek.Sunday,
      hours: "0",
      minutes: "0"
    };

    const result = scheduleSpecService.getCalendarWeekSpec(calendarWeekSpec);

    expect(result.dayOfWeek).toEqual([{ start: "0", end: "0", step: "1" }]);
  });


});
