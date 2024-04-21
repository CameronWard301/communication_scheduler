import parser from "cron-parser";
import dayjs from "dayjs";
import {
  CalendarMonthSpec,
  CalendarWeekSpec,
  DayOfWeek,
  getMonthNumber,
  IntervalSpec,
  Month
} from "../../models/Schedules.ts";

export const useUpcomingCommunications = () => {
  const numberOfCommunications = 7;

  const calculateIntervalsFromCronString = (cronString: string): string[] => {
    const interval = parser.parseExpression(cronString);
    const communications: string[] = []
    while (communications.length < numberOfCommunications) {
      const nextInterval = interval.next().toString();
      if (!dayjs(nextInterval).isBefore(dayjs())) {
        communications.push(dayjs(nextInterval).toDate().toLocaleString());
      }
    }
    return communications;
  }

  const calculateIntervalFromMonthSpec = (calendarMonthSpec: CalendarMonthSpec): string[] => {
    let currentDate = dayjs().set("second", 0).set("hours", 0).set("minutes", 0).set("date", calendarMonthSpec.dayOfMonth);
    currentDate = currentDate.add(calendarMonthSpec.hours, "hour").add(calendarMonthSpec.minutes, "minute")
    const communications: string[] = []

    switch (calendarMonthSpec.month) {
      case Month.EveryMonth:
        for (let i = 0; i < numberOfCommunications; i++) {
          communications.push(currentDate.toDate().toLocaleString());
          currentDate = currentDate.add(1, "month");
        }
        return communications;
      default:
        currentDate = currentDate.month(getMonthNumber(calendarMonthSpec.month));
    }
    while (communications.length < numberOfCommunications) {
      if (!currentDate.isBefore(dayjs())) {
        communications.push(currentDate.toDate().toLocaleString());
      }
      currentDate = currentDate.add(1, "year");
    }

    return communications;
  }

  const calculateIntervalFromDayOFWeekSpec = (calendarWeekSpec: CalendarWeekSpec): string[] => {
    let currentDate = dayjs().set("second", 0).set("hours", 0).set("minutes", 0);
    currentDate = currentDate.add(calendarWeekSpec.hours, "hour").add(calendarWeekSpec.minutes, "minute")
    const communications: string[] = []


    if (calendarWeekSpec.dayOfWeek === DayOfWeek.EveryDay) {
      while (communications.length < numberOfCommunications) {
        if (!currentDate.isBefore(dayjs())) {
          communications.push(currentDate.toDate().toLocaleString());
        }
        currentDate = currentDate.add(1, "day");
      }
      return communications;
    }

    if (calendarWeekSpec.dayOfWeek === DayOfWeek.Weekdays) {
      while (communications.length < numberOfCommunications) {
        if (currentDate.day() === 0) {
          currentDate = currentDate.add(1, "day");
        }
        if (currentDate.day() === 6) {
          currentDate = currentDate.add(2, "day");
        }
        if (!currentDate.isBefore(dayjs())) {
          communications.push(currentDate.toDate().toLocaleString());
        }
        currentDate = currentDate.add(1, "day");
      }

      return communications;
    }

    if (calendarWeekSpec.dayOfWeek === DayOfWeek.Weekend) {
      currentDate = currentDate.day(6);

      while (communications.length < numberOfCommunications) {
        if (!currentDate.isBefore(dayjs())) {
          communications.push(currentDate.toDate().toLocaleString());
        }
        if (currentDate.day() === 0) {
          currentDate = currentDate.add(6, "day");
          continue;
        }
        if (currentDate.day() === 6) {
          currentDate = currentDate.add(1, "day");
        }
      }
      return communications;
    }

    switch (calendarWeekSpec.dayOfWeek) {
      case "Monday":
        currentDate = currentDate.day(1);
        break
      case "Tuesday":
        currentDate = currentDate.day(2);
        break
      case "Wednesday":
        currentDate = currentDate.day(3);
        break
      case "Thursday":
        currentDate = currentDate.day(4);
        break
      case "Friday":
        currentDate = currentDate.day(5);
        break
      case "Saturday":
        currentDate = currentDate.day(6);
        break
      case "Sunday":
        currentDate = currentDate.day(0);
        break
    }
    while (communications.length < numberOfCommunications) {
      if (!currentDate.isBefore(dayjs())) {
        communications.push(currentDate.toDate().toLocaleString());
      }
      currentDate = currentDate.add(7, "day");
    }

    return communications;

  }

  const calculateIntervalInSecondsFromIntervalSpec = (intervalSpec: IntervalSpec): number => {
    const interval = parseInt(intervalSpec.seconds) + (parseInt(intervalSpec.minutes) * 60) + (parseInt(intervalSpec.hours) * 60 * 60) + (parseInt(intervalSpec.days) * 60 * 60 * 24);

    if (parseInt(intervalSpec.offset) > 0) {
      switch (intervalSpec.offsetPeriod) {
        case "S":
          return interval + parseInt(intervalSpec.offset);
        case "M":
          return interval + parseInt(intervalSpec.offset) * 60;
        case "H":
          return interval + parseInt(intervalSpec.offset) * 60 * 60;
        case "D":
          return interval + parseInt(intervalSpec.offset) * 60 * 60 * 24;
      }
    }
    return interval;

  }

  const getUpcomingTimestampsForInterval = (intervalSpec: IntervalSpec) => {
    const intervalInSeconds = calculateIntervalInSecondsFromIntervalSpec(intervalSpec);
    const timestamps: string[] = [];
    const currentDate = new Date();
    if (parseInt(intervalSpec.days) > 0) {
      currentDate.setHours(0, 0, 0, 0);
    } else if (parseInt(intervalSpec.hours) > 0) {
      currentDate.setMinutes(0, 0, 0);
    } else if (parseInt(intervalSpec.minutes) > 0 || parseInt(intervalSpec.seconds) > 0) {
      currentDate.setSeconds(0, 0);
    }
    for (let i = 0; i < numberOfCommunications; i++) {
      timestamps.push(new Date(currentDate.getTime() + intervalInSeconds * 1000 * (i + 1)).toLocaleString());
    }
    return timestamps;
  }

  return {
    getUpcomingTimestampsForInterval,
    calculateIntervalInSecondsFromIntervalSpec,
    calculateIntervalFromDayOFWeekSpec,
    calculateIntervalFromMonthSpec,
    calculateIntervalsFromCronString,
    numberOfCommunications
  }

}
