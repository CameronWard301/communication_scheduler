import {
  CalendarPeriod,
  CalendarSpec,
  ClientCalendarMonthSpec,
  ClientCalendarWeekSpec,
  ClientIntervalSpec,
  DayOfWeek,
  getMonthNumber,
  Month,
  Period,
  ServerIntervalSpec
} from "../model/Schedules";

export const ScheduleSpecService = () => {
  const getTimeFromSpec = (spec: string): CalendarPeriod => {
    return {
      start: spec,
      end: spec,
      step: "1"
    };
  };

  const getIntervalSpec = (interval: ClientIntervalSpec): ServerIntervalSpec => {
    const hours = parseInt(interval.hours) + (parseInt(interval.days) * 24);
    let offset = parseInt(interval.offset);
    let offsetPeriod = interval.offsetPeriod;
    if (interval.offsetPeriod === Period.D) {
      offset = offset * 24;
      offsetPeriod = Period.H;
    }
    return {
      every: `PT${hours.toString()}H${interval.minutes}M${interval.seconds}S`,
      offset: `PT${offset.toString()}${offsetPeriod}`
    };
  };

  const getCalendarMonthSpec = (calendarMonthSpec: ClientCalendarMonthSpec): CalendarSpec => {
    const calendarSpec = {
      comment: "",
      seconds: [{
        start: "0",
        end: "0",
        step: "1"
      }],
      minutes: [getTimeFromSpec(calendarMonthSpec.minutes.toString())],
      hour: [getTimeFromSpec(calendarMonthSpec.hours.toString())],
      dayOfMonth: [
        {
          start: calendarMonthSpec.dayOfMonth.toString(),
          end: calendarMonthSpec.dayOfMonth.toString(),
          step: "1"
        }
      ],
      dayOfWeek: [
        {
          start: "0",
          end: "6",
          step: "1"
        }
      ],
      year: []

    } as CalendarSpec;

    if (calendarMonthSpec.month === Month.EveryMonth) {
      calendarSpec.month = [{ start: "1", end: "12", step: "1" }];
    } else {
      calendarSpec.month = [{
        start: getMonthNumber(calendarMonthSpec.month).toString(),
        end: getMonthNumber(calendarMonthSpec.month).toString(),
        step: "1"
      }];
    }

    return calendarSpec;
  };

  const getCalendarWeekSpec = (calendarWeekSpec: ClientCalendarWeekSpec): CalendarSpec => {
    const calendarSpec = {
      comment: "",
      seconds: [{
        start: "0",
        end: "0",
        step: "1"
      }],
      minutes: [getTimeFromSpec(calendarWeekSpec.minutes)],
      hour: [getTimeFromSpec(calendarWeekSpec.hours)],
      month: [{ start: "1", end: "12", step: "1" }],
      year: [],
      dayOfMonth: [
        {
          start: "1",
          end: "31",
          step: "1"
        }
      ]
    } as CalendarSpec;

    switch (calendarWeekSpec.dayOfWeek) {
      case DayOfWeek.EveryDay:
        calendarSpec.dayOfWeek = [{ start: "0", end: "6", step: "1" }];
        break;
      case DayOfWeek.Weekdays:
        calendarSpec.dayOfWeek = [{ start: "1", end: "5", step: "1" }];
        break;
      case DayOfWeek.Weekend:
        calendarSpec.dayOfWeek = [{ start: "6", end: "6", step: "1" }, { start: "0", end: "0", step: "1" }];
        break;
      case DayOfWeek.Monday:
        calendarSpec.dayOfWeek = [getTimeFromSpec("1")];
        break;
      case DayOfWeek.Tuesday:
        calendarSpec.dayOfWeek = [getTimeFromSpec("2")];
        break;
      case DayOfWeek.Wednesday:
        calendarSpec.dayOfWeek = [getTimeFromSpec("3")];
        break;
      case DayOfWeek.Thursday:
        calendarSpec.dayOfWeek = [getTimeFromSpec("4")];
        break;
      case DayOfWeek.Friday:
        calendarSpec.dayOfWeek = [getTimeFromSpec("5")];
        break;
      case DayOfWeek.Saturday:
        calendarSpec.dayOfWeek = [getTimeFromSpec("6")];
        break;
      case DayOfWeek.Sunday:
        calendarSpec.dayOfWeek = [getTimeFromSpec("0")];
        break;
    }

    return calendarSpec;
  };

  return { getIntervalSpec, getCalendarWeekSpec, getCalendarMonthSpec };
};

